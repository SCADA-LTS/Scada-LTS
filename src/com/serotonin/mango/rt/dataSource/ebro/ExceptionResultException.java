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
package com.serotonin.mango.rt.dataSource.ebro;

import com.serotonin.modbus4j.ExceptionResult;

/**
 * @author Matthew Lohbihler
 */
public class ExceptionResultException extends Exception {
    private static final long serialVersionUID = 1L;

    private final String key;
    private final ExceptionResult exceptionResult;

    public ExceptionResultException(String key, ExceptionResult exceptionResult) {
        this.key = key;
        this.exceptionResult = exceptionResult;
    }

    public String getKey() {
        return key;
    }

    public ExceptionResult getExceptionResult() {
        return exceptionResult;
    }
}
