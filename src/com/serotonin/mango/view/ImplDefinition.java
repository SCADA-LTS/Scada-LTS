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
package com.serotonin.mango.view;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.serotonin.util.ArrayUtils;

public class ImplDefinition implements Serializable {

	private static final long serialVersionUID = 1631252421570751506L;

	public static ImplDefinition findByName(List<ImplDefinition> list, String name) {
        for (ImplDefinition def : list) {
            if (def.getName().equals(name))
                return def;
        }
        return null;
    }

    public static ImplDefinition findByExportName(List<ImplDefinition> list, String exportName) {
        for (ImplDefinition def : list) {
            if (def.getExportName().equalsIgnoreCase(exportName))
                return def;
        }
        return null;
    }

    private int id;
    private String name;
    private String exportName;
    private final String nameKey;
    private final int[] supportedDataTypes;

    public ImplDefinition(int id, String exportName, String nameKey, int[] supportedDataTypes) {
        this.id = id;
        this.nameKey = nameKey;
        this.exportName = exportName;
        this.supportedDataTypes = supportedDataTypes;
    }

    public ImplDefinition(String name, String exportName, String nameKey, int[] supportedDataTypes) {
        this.name = name;
        this.nameKey = nameKey;
        this.exportName = exportName;
        this.supportedDataTypes = supportedDataTypes;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNameKey() {
        return nameKey;
    }

    public String getExportName() {
        return exportName;
    }

    public void setExportName(String exportName) {
        this.exportName = exportName;
    }

    public int[] getSupportedDataTypes() {
        return supportedDataTypes;
    }

    public boolean supports(int dataType) {
        return ArrayUtils.contains(supportedDataTypes, dataType);
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((nameKey == null) ? 0 : nameKey.hashCode());
		result = prime * result + Arrays.hashCode(supportedDataTypes);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImplDefinition other = (ImplDefinition) obj;
		if (id != other.id)
			return false;
		if (nameKey == null) {
			if (other.nameKey != null)
				return false;
		} else if (!nameKey.equals(other.nameKey))
			return false;
		if (!Arrays.equals(supportedDataTypes, other.supportedDataTypes))
			return false;
		return true;
	}
    
}
