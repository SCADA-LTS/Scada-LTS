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
package com.serotonin.mango.rt.dataSource.onewire;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dalsemi.onewire.OneWireAccessProvider;
import com.dalsemi.onewire.OneWireException;
import com.dalsemi.onewire.adapter.DSPortAdapter;
import com.dalsemi.onewire.adapter.OneWireIOException;
import com.dalsemi.onewire.container.OneWireContainer;
import com.dalsemi.onewire.container.OneWireContainer1D;
import com.dalsemi.onewire.container.OneWireContainer1F;
import com.dalsemi.onewire.container.OneWireSensor;
import com.dalsemi.onewire.container.SwitchContainer;
import com.dalsemi.onewire.utils.Address;

/**
 * A utility that can scan a network, and return the network to its pre-scan state. This is important because we can't
 * necessarily tell the difference between relays (that are controlling equipment), and latches (that are serving as
 * branches to other devices).
 * 
 * @author Matthew Lohbihler
 */
public class Network {
    private static final String DEFAULT_ADAPTER_NAME = "DS9097U";

    private final DSPortAdapter adapter;

    /**
     * The unique address of the device, and the network path to it.
     */
    final Map<Long, NetworkPath> pathsByAddress = new HashMap<Long, NetworkPath>();

    public Network(DSPortAdapter adapter) {
        this.adapter = adapter;
    }

    public Network(String commPortId) throws OneWireIOException, OneWireException {
        adapter = OneWireAccessProvider.getAdapter(DEFAULT_ADAPTER_NAME, commPortId);
    }

    public Network(String adapterName, String commPortId) throws OneWireIOException, OneWireException {
        adapter = OneWireAccessProvider.getAdapter(adapterName, commPortId);
    }

    public void initialize() throws Exception {
        // Setup the search
        adapter.setSearchAllDevices();
        adapter.targetAllFamilies();
        adapter.setSpeed(DSPortAdapter.SPEED_REGULAR);

        List<NetworkPathElement> toTurnBackOn = new ArrayList<NetworkPathElement>();
        initializeImpl(new NetworkPath(this), toTurnBackOn);

        // Turn back on any latches that we turned off. Note that only the leaves of the network will actually be
        // turned back on because the path close method will turn off all latches on the way to it. However, if
        // there are no latches to be turned on past a coupler, it will be turned on. Make sense?
        for (NetworkPathElement element : toTurnBackOn) {
            NetworkPath path = pathsByAddress.get(((OneWireContainer) element.getContainer()).getAddressAsLong());
            path.open();

            SwitchContainer sc = element.getContainer();
            byte[] state = sc.readDevice();
            sc.setLatchState(element.getChannel(), true, sc.hasSmartOn(), state);
            sc.writeDevice(state);

            path.close();
        }
    }

    public void quickInitialize() throws Exception {
        // Setup the search
        adapter.setSearchAllDevices();
        adapter.targetAllFamilies();
        adapter.setSpeed(DSPortAdapter.SPEED_REGULAR);

        pathsByAddress.clear();
        quickInitializeImpl(new NetworkPath(this));
    }

    public void terminate() throws OneWireException {
        adapter.freePort();
    }

    public void lock() throws OneWireException {
        adapter.beginExclusive(true);
    }

    public void reset() throws OneWireException {
        adapter.reset();
    }

    public void unlock() {
        if (adapter != null)
            adapter.endExclusive();
    }

    public String getAdapterName() {
        if (adapter != null)
            return adapter.getAdapterName();
        return null;
    }

    public String getPortName() throws OneWireException {
        if (adapter != null)
            return adapter.getPortName();
        return null;
    }

    public List<Long> getAddresses() {
        List<Long> sorted = new ArrayList<Long>(pathsByAddress.keySet());
        Collections.sort(sorted, new Comparator<Long>() {
            public int compare(Long addr1, Long addr2) {
                String path1 = pathsByAddress.get(addr1).toString();
                String path2 = pathsByAddress.get(addr2).toString();
                return path1.compareTo(path2);
            }
        });
        return sorted;
    }

    public NetworkPath getNetworkPath(Long address) {
        return pathsByAddress.get(address);
    }

    public String addressPathsToString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        boolean first = true;
        for (Long address : pathsByAddress.keySet()) {
            if (first)
                first = false;
            else
                sb.append(", ");
            sb.append(pathsByAddress.get(address));
            sb.append(Address.toString(address));
        }
        sb.append(']');
        return sb.toString();
    }

    /**
     * Recursive method for searching the network.
     * 
     * @param path
     * @throws OneWireException
     * @throws OneWireIOException
     * @throws Exception
     */
    private void initializeImpl(NetworkPath path, List<NetworkPathElement> toTurnBackOn) throws OneWireException {
        byte[] state;
        OneWireContainer owc;
        SwitchContainer sc;
        Long address;

        // Start a search for all devices.
        boolean searchResult = adapter.findFirstDevice();

        // Look for latches.
        while (searchResult) {
            boolean reSearch = false;

            address = adapter.getAddressAsLong();
            if (!pathsByAddress.containsKey(address)) {
                owc = adapter.getDeviceContainer();
                if (owc instanceof SwitchContainer) {
                    sc = (SwitchContainer) owc;

                    try {
                        state = sc.readDevice();
                        for (int ch = 0; ch < sc.getNumberChannels(state); ch++) {
                            if (sc.getLatchState(ch, state)) {
                                // We found a latch that is on. In order to reliably construct a network tree we need
                                // to turn this off temporarily, so we add it to the list to "turn back on", turn it
                                // off, and then note that we need to re-search.
                                sc.setLatchState(ch, false, false, state);
                                sc.writeDevice(state);

                                toTurnBackOn.add(new NetworkPathElement(sc, address, ch));

                                reSearch = true;
                            }
                        }
                    }
                    catch (OneWireIOException e) {
                        // The device may no longer be available because we could have turned off a branch that leads
                        // to it. Just ignore this.
                    }
                }
            }

            searchResult = adapter.findNextDevice();

            if (!searchResult && reSearch)
                // Restart the search.
                searchResult = adapter.findFirstDevice();
        }

        // Any new devices that we now find in the search will be children of the current path.
        searchResult = adapter.findFirstDevice();

        // Keep track of any new branches that we find so that we can search them recursively.
        List<NetworkPath> newBranches = new ArrayList<NetworkPath>();

        while (searchResult) {
            address = adapter.getAddressAsLong();

            // We only care about this device if we haven't already mapped it.
            if (!pathsByAddress.containsKey(address)) {
                owc = adapter.getDeviceContainer(address);

                // We only work with sensors for now.
                if (owc instanceof OneWireSensor) {
                    // Map it.
                    state = ((OneWireSensor) owc).readDevice();
                    OneWireContainerInfo info = new OneWireContainerInfo();
                    info.setAddress(address);
                    info.inspect(owc, state);
                    pathsByAddress.put(address, new NetworkPath(path, owc, info));

                    // Check to see if it's a switch
                    if (owc instanceof SwitchContainer) {
                        sc = (SwitchContainer) owc;
                        // Create a branch for every channel in this switch.
                        for (int ch = 0; ch < sc.getNumberChannels(state); ch++)
                            newBranches.add(new NetworkPath(path, sc, address, ch));
                    }
                }
                else if (owc instanceof OneWireContainer1D) {
                    // Map it.
                    OneWireContainerInfo info = new OneWireContainerInfo();
                    info.setAddress(address);
                    info.inspect(owc, null);
                    pathsByAddress.put(address, new NetworkPath(path, owc, info));
                }
            }

            searchResult = adapter.findNextDevice();
        }

        // All the new stuff has been recorded, so let's recurse over the new branches.
        for (NetworkPath newBranch : newBranches) {
            // Turn this latch on.
            NetworkPathElement tail = newBranch.getTail();
            sc = tail.getContainer();
            state = sc.readDevice();
            sc.setLatchState(tail.getChannel(), true, sc.hasSmartOn(), state);
            sc.writeDevice(state);

            // Search it.
            initializeImpl(newBranch, toTurnBackOn);

            // Ok, turn the latch back off.
            sc.setLatchState(tail.getChannel(), false, false, state);
            sc.writeDevice(state);
        }
    }

    /**
     * Recursive method for searching the network. Only treats 1F containers as network branches, so the overhead of
     * checking other latch types is avoided.
     * 
     * @param path
     * @throws OneWireException
     * @throws OneWireIOException
     * @throws Exception
     */
    private void quickInitializeImpl(NetworkPath path) throws OneWireException {
        byte[] state;
        OneWireContainer owc;
        SwitchContainer sc;
        Long address;

        // Start a search for all devices.
        boolean searchResult = adapter.findFirstDevice();

        // Keep track of any new branches that we find so that we can search them recursively.
        List<NetworkPath> newBranches = new ArrayList<NetworkPath>();

        while (searchResult) {
            address = adapter.getAddressAsLong();

            // We only care about this device if we haven't already mapped it.
            if (!pathsByAddress.containsKey(address)) {
                owc = adapter.getDeviceContainer(address);

                // We only work with sensors for now.
                if (owc instanceof OneWireSensor) {
                    // Map it.
                    state = ((OneWireSensor) owc).readDevice();
                    OneWireContainerInfo info = new OneWireContainerInfo();
                    info.setAddress(address);
                    info.inspect(owc, state);
                    pathsByAddress.put(address, new NetworkPath(path, owc, info));

                    // Check to see if it's a switch
                    if (owc instanceof OneWireContainer1F) {
                        sc = (SwitchContainer) owc;
                        // Create a branch for every channel in this switch.
                        for (int ch = 0; ch < sc.getNumberChannels(state); ch++)
                            newBranches.add(new NetworkPath(path, sc, address, ch));
                    }
                }
                else if (owc instanceof OneWireContainer1D) {
                    // Map it.
                    OneWireContainerInfo info = new OneWireContainerInfo();
                    info.setAddress(address);
                    info.inspect(owc, null);
                    pathsByAddress.put(address, new NetworkPath(path, owc, info));
                }
            }

            searchResult = adapter.findNextDevice();
        }

        // All the new stuff has been recorded, so let's recurse over the new branches.
        for (NetworkPath newBranch : newBranches) {
            // Turn this latch on.
            NetworkPathElement tail = newBranch.getTail();
            sc = tail.getContainer();
            state = sc.readDevice();
            sc.setLatchState(tail.getChannel(), true, sc.hasSmartOn(), state);
            sc.writeDevice(state);

            // Search it.
            quickInitializeImpl(newBranch);

            // Ok, turn the latch back off.
            sc.setLatchState(tail.getChannel(), false, false, state);
            sc.writeDevice(state);
        }
    }
}
