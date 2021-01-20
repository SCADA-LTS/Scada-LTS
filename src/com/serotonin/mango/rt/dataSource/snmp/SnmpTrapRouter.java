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

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.mp.CounterSupport;
import org.snmp4j.mp.DefaultCounterListener;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import com.serotonin.ShouldNeverHappenException;

/**
 * @author Matthew Lohbihler
 */
public class SnmpTrapRouter {
    private static SnmpTrapRouter instance;

    public synchronized static void addDataSource(SnmpDataSourceRT ds) throws IOException {
        if(ds.isTrapEnabled()) {
            if (instance == null) {
                CounterSupport.getInstance().addCounterListener(new DefaultCounterListener());
                instance = new SnmpTrapRouter();
            }
            instance.addDataSourceImpl(ds);
        }
    }

    public synchronized static void removeDataSource(SnmpDataSourceRT ds) {
        if (instance != null)
            instance.removeDataSourceImpl(ds);
    }

    private final List<PortListener> portListeners = new LinkedList<PortListener>();

    private void addDataSourceImpl(SnmpDataSourceRT ds) throws IOException {
        PortListener l = getPortListener(ds.getTrapPort());
        if (l == null) {
            String localAddress = ds.getLocalAddress() != null && !ds.getLocalAddress().isEmpty()
                    ? ds.getLocalAddress() : "0.0.0.0";
            l = new PortListener(ds.getTrapPort(), localAddress);
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
        private final Snmp snmp;
        final int port;
        final List<SnmpDataSourceRT> dataSources = new LinkedList<SnmpDataSourceRT>();

        PortListener(int port, String localAddress) throws IOException {
            this.port = port;

            snmp = new Snmp(new DefaultUdpTransportMapping(new UdpAddress(localAddress + "/" + port)));
            snmp.addCommandResponder(this);
            snmp.listen();
        }

        public synchronized void processPdu(CommandResponderEvent evt) {
            PDU command = evt.getPDU();
            if (command != null) {

                // Look for the peer in the data source list.
                for (SnmpDataSourceRT ds : dataSources) {
                    ds.receivedTrap(command);
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
