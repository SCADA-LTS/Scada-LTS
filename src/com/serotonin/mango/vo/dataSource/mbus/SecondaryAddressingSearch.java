/*
 *   Mango - Open Source M2M - http://mango.serotoninsoftware.com
 *   Copyright (C) 2010 Arne Pl\u00f6se
 *   @author Arne Pl\u00f6se
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.vo.dataSource.mbus;

import net.sf.mbus4j.MBusAddressing;
import net.sf.mbus4j.MBusUtils;
import net.sf.mbus4j.dataframes.MBusMedium;

/**
 *
 * @author aploese
 *
 * //TODO move conversation to an earlier stage to give errormessage????
 *
 */
public class SecondaryAddressingSearch extends MBusSearchByAddressing {

    private String id;
    private String version;
    private String medium;
    private String manufacturer;

    public int maskedId() {
        if ((id == null) || (id.length() == 0)) {
            return (int)0xFFFFFFFF;
        } else {
            return (int)Long.parseLong(id, 16);
        }
    }

    public byte maskedVersion() {
        if ((version == null) || (version.length() == 0)) {
            return (byte)0xFF;
        } else {
            return (byte)Short.parseShort(version, 16);
        }
    }

    public byte maskedMedium() {
        if ((medium == null) || (medium.length() == 0)) {
            return (byte)0xFF;
        } else {
             return (byte)MBusMedium.fromLabel(medium).getId();
        }
    }

    public short maskedManufacturer() {
        if ((manufacturer == null) || (manufacturer.length() == 0)) {
            return (short)0xFFFF;
        } else {
             return MBusUtils.man2Short(manufacturer);
        }
    }

    @Override
    public MBusAddressing getAddressing() {
        return MBusAddressing.SECONDARY;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the medium
     */
    public String getMedium() {
        return medium;
    }

    /**
     * @param medium the medium to set
     */
    public void setMedium(String medium) {
        this.medium = medium;
    }

    /**
     * @return the manufacturer
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * @param manufacturer the manufacturer to set
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

}
