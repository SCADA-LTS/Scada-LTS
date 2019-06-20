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

/**
 *
 * @author aploese
 */
public class PrimaryAddressingSearch extends MBusSearchByAddressing {

    private String lastPrimaryAddress;
    private String firstPrimaryAddress;

    public int firstAddr() {
        return Integer.parseInt(firstPrimaryAddress, 16);
    }

    public int lastAddr() {
        return Integer.parseInt(lastPrimaryAddress, 16);
    }

    /**
     * @return the lastPrimaryAddress
     */
    public String getLastPrimaryAddress() {
        return lastPrimaryAddress;
    }

    /**
     * @param lastPrimaryAddress the lastPrimaryAddress to set
     */
    public void setLastPrimaryAddress(String lastPrimaryAddress) {
        this.lastPrimaryAddress = lastPrimaryAddress;
    }

    /**
     * @return the firstPrimaryAddress
     */
    public String getFirstPrimaryAddress() {
        return firstPrimaryAddress;
    }

    /**
     * @param firstPrimaryAddress the firstPrimaryAddress to set
     */
    public void setFirstPrimaryAddress(String firstPrimaryAddress) {
        this.firstPrimaryAddress = firstPrimaryAddress;
    }

    @Override
    public MBusAddressing getAddressing() {
        return MBusAddressing.PRIMARY;
    }
}
