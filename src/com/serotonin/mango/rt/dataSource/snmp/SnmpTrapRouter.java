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
import rpc.core.Port;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Matthew Lohbihler
 */
public class SnmpTrapRouter {
    private static SnmpTrapRouter instance;

    public synchronized static void addDataSource(SnmpDataSourceRT ds) throws IOException {
        if (instance == null) {
            CounterSupport.getInstance().addCounterListener(new DefaultCounterListener());
            instance = new SnmpTrapRouter();
        }
        instance.addDataSourceImpl(ds);
    }

    public synchronized static void removeDataSource(SnmpDataSourceRT ds) {
        if (instance != null)
            instance.removeDataSourceImpl(ds);
    }

    private final List<SnmpDataSourceTrapListener> portListeners = new LinkedList<SnmpDataSourceTrapListener>();

    private void addDataSourceImpl(SnmpDataSourceRT ds) throws IOException {
        SnmpDataSourceTrapListener l = getPortListener(ds);
        if (l == null) {
            l = new SnmpDataSourceTrapListener(ds);
            portListeners.add(l);
        }
//        l.addDataSource(ds);
        l.setupTrapMessageProcessing(ds);
    }

    private void removeDataSourceImpl(SnmpDataSourceRT ds) {
        SnmpDataSourceTrapListener l = getPortListener(ds);
        if (l != null) {
            l.close();
            portListeners.remove(l);
//            l.removeDataSource(ds);
//            if (l.dataSources.size() == 0) {
//                l.close();
//                portListeners.remove(l);
//            }
        }
    }

    private SnmpDataSourceTrapListener getPortListener(SnmpDataSourceRT dataSource) {
        for (SnmpDataSourceTrapListener l : portListeners) {
            if (l.dataSource == dataSource)
                return l;
        }
        return null;
    }

    private class SnmpDataSourceTrapListener implements CommandResponder {

        final SnmpDataSourceRT dataSource;
        Snmp snmp;

//        final List<SnmpDataSourceRT> dataSources = new LinkedList<SnmpDataSourceRT>();
//        final List<Snmp> snmpTraps = new LinkedList<>();
//        final int port;

//        SnmpDataSourceTrapListener(int port) throws IOException {
//            this.port = port;
//        }

        SnmpDataSourceTrapListener(SnmpDataSourceRT ds) {
            this.dataSource = ds;
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
//                for (SnmpDataSourceRT ds : dataSources) {
//                    if (ds.getAddress().equals(peer)) {
//                        if (StringUtils.isEmpty(ds.getLocalAddress()) || localAddress.equals(ds.getLocalAddress()))
//                            ds.receivedTrap(command);
//                    }
//                }

                if (dataSource.getAddress().equals(peer)) {
                    if (StringUtils.isEmpty(dataSource.getLocalAddress()) || localAddress.equals(dataSource.getLocalAddress())) {
                        dataSource.receivedTrap(command);
                    }
                }

            }
        }

//        synchronized void addDataSource(SnmpDataSourceRT ds) {
//            dataSources.add(ds);
//        }
//
//        synchronized void removeDataSource(SnmpDataSourceRT ds) {
//            dataSources.remove(ds);
//        }

        private synchronized void setupTrapMessageProcessing(SnmpDataSourceRT ds) {

            ThreadPool tp = ThreadPool.create("TrapManager", 2);
            MultiThreadedMessageDispatcher dispatcher = new MultiThreadedMessageDispatcher(tp, new MessageDispatcherImpl());
            UdpAddress trapListenAddress = new UdpAddress("0.0.0.0/" + ds.getTrapPort());

            try {
                snmp = new Snmp(dispatcher, new DefaultUdpTransportMapping(trapListenAddress));
                if(ds.getVersion() instanceof Version3) {
                    snmp.getMessageDispatcher().addMessageProcessingModel(new MPv3());
                    ds.getVersion().addUser(snmp);
                } else if (ds.getVersion() instanceof Version2c) {
                    snmp.getMessageDispatcher().addMessageProcessingModel(new MPv2c());
                } else {
                    snmp.getMessageDispatcher().addMessageProcessingModel(new MPv1());
                }
                snmp.listen();
                snmp.addCommandResponder(this);

            } catch (IOException e) {
                e.printStackTrace();
            }
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
