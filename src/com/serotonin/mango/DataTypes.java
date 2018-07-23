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
package com.serotonin.mango;

import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.web.i18n.LocalizableMessage;

public class DataTypes {
	public static final int UNKNOWN = 0;
	public static final int BINARY = 1;
	public static final int MULTISTATE = 2;
	public static final int NUMERIC = 3;
	public static final int ALPHANUMERIC = 4;
	public static final int IMAGE = 5;

	public static LocalizableMessage getDataTypeMessage(int id) {
		switch (id) {
		case BINARY:
			return new LocalizableMessage("common.dataTypes.binary");
		case MULTISTATE:
			return new LocalizableMessage("common.dataTypes.multistate");
		case NUMERIC:
			return new LocalizableMessage("common.dataTypes.numeric");
		case ALPHANUMERIC:
			return new LocalizableMessage("common.dataTypes.alphanumeric");
		case IMAGE:
			return new LocalizableMessage("common.dataTypes.image");
		}
		return new LocalizableMessage("common.unknown");
	}

	public static final ExportCodes CODES = new ExportCodes();
	static {
		CODES.addElement(BINARY, "BINARY");
		CODES.addElement(MULTISTATE, "MULTISTATE");
		CODES.addElement(NUMERIC, "NUMERIC");
		CODES.addElement(ALPHANUMERIC, "ALPHANUMERIC");
		CODES.addElement(IMAGE, "IMAGE");
	}

	public static int getDataType(MangoValue value) {
		if (value == null)
			return UNKNOWN;
		return value.getDataType();
	}

	public static String valueToString(MangoValue value) {
		if (value == null)
			return null;
		return value.toString();
	}
}
