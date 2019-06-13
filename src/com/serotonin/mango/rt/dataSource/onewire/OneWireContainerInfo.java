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

import com.dalsemi.onewire.container.ADContainer;
import com.dalsemi.onewire.container.HumidityContainer;
import com.dalsemi.onewire.container.OneWireContainer;
import com.dalsemi.onewire.container.OneWireContainer1D;
import com.dalsemi.onewire.container.PotentiometerContainer;
import com.dalsemi.onewire.container.SwitchContainer;
import com.dalsemi.onewire.container.TemperatureContainer;
import com.dalsemi.onewire.utils.Address;
import com.serotonin.mango.vo.dataSource.onewire.OneWirePointLocatorVO;

/**
 * @author Matthew Lohbihler
 */
public class OneWireContainerInfo {
    private Long address;
    private String description;
    private List<OneWireContainerAttribute> attributes;

    public Long getAddress() {
        return address;
    }

    public void setAddress(Long address) {
        this.address = address;
    }

    public String getAddressString() {
        return Address.toString(address);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<OneWireContainerAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<OneWireContainerAttribute> attributes) {
        this.attributes = attributes;
    }

    public void inspect(OneWireContainer container, byte[] state) {
        description = container.getAlternateNames() + " (" + container.getName() + ")";
        attributes = new ArrayList<OneWireContainerAttribute>();

        if (container instanceof TemperatureContainer)
            attributes.add(new OneWireContainerAttribute(OneWirePointLocatorVO.AttributeTypes.TEMPURATURE));

        if (container instanceof HumidityContainer)
            attributes.add(new OneWireContainerAttribute(OneWirePointLocatorVO.AttributeTypes.HUMIDITY));

        if (container instanceof ADContainer) {
            ADContainer ac = (ADContainer) container;
            OneWireContainerAttribute attr = new OneWireContainerAttribute(
                    OneWirePointLocatorVO.AttributeTypes.AD_VOLTAGE, 0, ac.getNumberADChannels());
            attributes.add(attr);
        }

        if (container instanceof SwitchContainer) {
            SwitchContainer sc = (SwitchContainer) container;
            OneWireContainerAttribute attr = new OneWireContainerAttribute(
                    OneWirePointLocatorVO.AttributeTypes.LATCH_STATE, 0, sc.getNumberChannels(state));
            attributes.add(attr);
        }

        if (container instanceof PotentiometerContainer) {
            PotentiometerContainer pc = (PotentiometerContainer) container;
            OneWireContainerAttribute attr = new OneWireContainerAttribute(
                    OneWirePointLocatorVO.AttributeTypes.WIPER_POSITION, 0, pc.numberOfPotentiometers(state));
            attributes.add(attr);
        }

        if (container instanceof OneWireContainer1D) {
            OneWireContainerAttribute attr = new OneWireContainerAttribute(
                    OneWirePointLocatorVO.AttributeTypes.COUNTER, 12, 4);
            attributes.add(attr);
        }
    }
}
