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
package com.serotonin.mango.view.export;

/**
 * @author Matthew Lohbihler
 */
public class CsvWriter {
    private static final String CRLF = "\r\n";

    private final StringBuilder sb = new StringBuilder();

    public String encodeRow(String[] data) {
        sb.setLength(0);

        boolean first = true;
        for (String s : data) {
            if (first)
                first = false;
            else
                sb.append(',');

            if (s != null)
                sb.append(encodeValue(s));
        }

        sb.append(CRLF);

        return sb.toString();
    }

    public String encodeValue(String fieldValue) {
        if (fieldValue == null)
            fieldValue = "";

        boolean needsQuotes = false;

        // Fields with embedded commas must be delimited with double-quote characters.
        if (fieldValue.indexOf(',') != -1)
            needsQuotes = true;

        // Fields that contain double quote characters must be surounded by double-quotes,
        // and the embedded double-quotes must each be represented by a pair of consecutive
        // double quotes.
        if (fieldValue.indexOf('"') != -1) {
            needsQuotes = true;
            fieldValue.replaceAll("\"", "\"\"");
        }

        // A field that contains embedded line-breaks must be surounded by double-quotes
        if (fieldValue.indexOf('\n') != -1 || fieldValue.indexOf('\r') != -1)
            needsQuotes = true;

        // Fields with leading or trailing spaces must be delimited with double-quote characters.
        if (fieldValue.startsWith(" ") || fieldValue.endsWith(" "))
            needsQuotes = true;

        if (needsQuotes)
            fieldValue = '"' + fieldValue + '"';

        return fieldValue;
    }
}
