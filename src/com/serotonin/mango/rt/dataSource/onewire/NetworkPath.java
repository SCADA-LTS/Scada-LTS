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
import java.util.List;

import com.dalsemi.onewire.OneWireException;
import com.dalsemi.onewire.adapter.OneWireIOException;
import com.dalsemi.onewire.container.OneWireContainer;
import com.dalsemi.onewire.container.OneWireContainer1F;
import com.dalsemi.onewire.container.SwitchContainer;

/**
 * @author Matthew Lohbihler
 */
public class NetworkPath {
    /** Elements of the path */
    private final List<NetworkPathElement> elements = new ArrayList<NetworkPathElement>();

    /** Network where this path is based */
    private final Network network;

    private final OneWireContainer target;
    private final OneWireContainerInfo targetInfo;

    public NetworkPath(Network network) {
        this.network = network;
        target = null;
        targetInfo = null;
    }

    public NetworkPath(NetworkPath currentPath, OneWireContainer target, OneWireContainerInfo targetInfo) {
        network = currentPath.network;
        elements.addAll(currentPath.elements);
        this.target = target;
        this.targetInfo = targetInfo;
    }

    public NetworkPath(NetworkPath currentPath, SwitchContainer sc, Long address, int channel) {
        network = currentPath.network;
        elements.addAll(currentPath.elements);
        elements.add(new NetworkPathElement(sc, address, channel));
        target = null;
        targetInfo = null;
    }

    public OneWireContainer getTarget() {
        return target;
    }

    public OneWireContainerInfo getTargetInfo() {
        return targetInfo;
    }

    public boolean isCoupler() {
        return target instanceof OneWireContainer1F;
    }

    public boolean equals(NetworkPath otherPath) {
        return toString().equals(otherPath.toString());
    }

    public NetworkPathElement getTail() {
        if (elements.size() == 0)
            return null;
        return elements.get(elements.size() - 1);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(network.getAdapterName());
        try {
            String portName = network.getPortName();
            sb.append('_').append(portName);
        }
        catch (OneWireException e) {
            // no op
        }
        sb.append('/');

        for (NetworkPathElement element : elements) {
            sb.append(((OneWireContainer) element.getContainer()).getAddressAsString());
            sb.append('_');
            sb.append(element.getChannel());
            sb.append('/');
        }

        return sb.toString();
    }

    /**
     * Open the entire path.
     */
    public void open() throws OneWireException, OneWireIOException {
        open(null);
    }

    /**
     * Close the path elements in the last path following the common elements of this and the last path, and open the
     * same in this path.
     */
    public void open(NetworkPath lastPath) throws OneWireException, OneWireIOException {
        // Check if no depth in path, do a reset so a resetless search will work
        if (elements.size() == 0) {
            if (lastPath != null)
                lastPath.close();
            network.reset();
            return;
        }

        int uncommonIndex = 0;
        if (lastPath != null) {
            // Determine the index at which uncommon-ness begins
            List<NetworkPathElement> lastElements = lastPath.elements;
            int minSize = lastElements.size();
            if (elements.size() < minSize)
                minSize = elements.size();

            for (int i = 0; i < minSize; i++) {
                if (!lastElements.get(i).equals(elements.get(i)))
                    break;
                uncommonIndex++;
            }

            lastPath.close(uncommonIndex);
        }

        for (int i = uncommonIndex; i < elements.size(); i++) {
            NetworkPathElement element = elements.get(i);

            // Get the switch
            SwitchContainer sw = element.getContainer();

            // Turn on the elements channel
            byte[] state = sw.readDevice();

            sw.setLatchState(element.getChannel(), true, sw.hasSmartOn(), state);
            sw.writeDevice(state);
        }
    }

    /**
     * Close the entire path.
     */
    public void close() throws OneWireException, OneWireIOException {
        close(0);
    }

    /**
     * Only close elements following the common elements in the next path, so as to avoid having to reopen them again.
     */
    private void close(int downToInclusive) throws OneWireException, OneWireIOException {
        NetworkPathElement element;
        SwitchContainer sw;
        byte[] state;

        // Loop through elements in path in reverse order
        for (int i = elements.size() - 1; i >= downToInclusive; i--) {
            element = elements.get(i);
            sw = element.getContainer();

            // Turn off the elements channel
            state = sw.readDevice();

            sw.setLatchState(element.getChannel(), false, false, state);
            sw.writeDevice(state);
        }
    }
}
