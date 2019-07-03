/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.rt.dataSource.snmp;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.util.StringUtils;
import org.snmp4j.*;
import org.snmp4j.mp.*;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Matthew Lohbihler
 */
public class SnmpTrapRouter {
    private static SnmpTrapRouter instance;

    public synchronized static void addDataSource(SnmpDataSourceRT ds, Version version) throws IOException {
        if (instance == null) {
            CounterSupport.getInstance().addCounterListener(new DefaultCounterListener());
            instance = new SnmpTrapRouter();
        }
        instance.addDataSourceImpl(ds, version);
    }

    public synchronized static void removeDataSource(SnmpDataSourceRT ds) {
        if (instance != null)
            instance.removeDataSourceImpl(ds);
    }

    private final List<PortListener> portListeners = new LinkedList<PortListener>();

    private void addDataSourceImpl(SnmpDataSourceRT ds, Version version) throws IOException {
        PortListener l = getPortListener(ds.getTrapPort());
        if (l == null) {
            l = new PortListener(ds.getTrapPort(), version);
            portListeners.add(l);
        }
        l.addDataSource(ds);
    }

    private void removeDataSourceImpl(SnmpDataSourceRT ds) {
        PortListener l = getPortListener(ds.getTrapPort());
        if (l != null) {
            l.removeDataSource(ds);
            if (l.dataSources.size() == 0) {
                l.close();
                portListeners.remove(l);
            }
        }
    }

    private PortListener getPortListener(int port) {
        for (PortListener l : portListeners) {
            if (l.port == port)
                return l;
        }
        return null;
    }

    private class PortListener implements CommandResponder {

        final List<SnmpDataSourceRT> dataSources = new LinkedList<SnmpDataSourceRT>();
        final Version version;
        final int port;
        private Snmp snmp;

        PortListener(int port, Version version) throws IOException {
            this.port = port;
            this.version = version;

            run();

        }


        private void init() throws IOException {
            ThreadPool threadPool = ThreadPool.create("Trap", dataSources.size() + 1);
            MultiThreadedMessageDispatcher dispatcher = new MultiThreadedMessageDispatcher(threadPool, new MessageDispatcherImpl());
            UdpAddress trapListenAddress = new UdpAddress("0.0.0.0/" + this.port);

            snmp = new Snmp(dispatcher, new DefaultUdpTransportMapping(trapListenAddress));

            snmp.getMessageDispatcher().addMessageProcessingModel(new MPv1());
            snmp.getMessageDispatcher().addMessageProcessingModel(new MPv2c());
            snmp.getMessageDispatcher().addMessageProcessingModel(new MPv3());

            version.addUser(snmp);

            snmp.listen();

        }

        public void run() {
            try {
                init();
                snmp.addCommandResponder(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }




        public synchronized void processPdu(CommandResponderEvent evt) {
            PDU command = evt.getPDU();
            if (command != null) {
                // Get the peer address
                String peer = evt.getPeerAddress().toString();
                int slash = peer.indexOf('/');
                if (slash > 0)
                    peer = peer.substring(0, slash);

                String localAddress = "";
                if (command instanceof PDUv1)
                    localAddress = ((PDUv1) command).getAgentAddress().toString();

                // Look for the peer in the data source list.
                for (SnmpDataSourceRT ds : dataSources) {
                    if (ds.getAddress().equals(peer)) {
                        if (StringUtils.isEmpty(ds.getLocalAddress()) || localAddress.equals(ds.getLocalAddress()))
                            ds.receivedTrap(command);
                    }
                }
            }
        }

        synchronized void addDataSource(SnmpDataSourceRT ds) {
            dataSources.add(ds);
        }

        synchronized void removeDataSource(SnmpDataSourceRT ds) {
            dataSources.remove(ds);
        }

        void close() {
            try {
                snmp.close();
            }
            catch (IOException e) {
                throw new ShouldNeverHappenException(e);
            }
        }
    }
}
