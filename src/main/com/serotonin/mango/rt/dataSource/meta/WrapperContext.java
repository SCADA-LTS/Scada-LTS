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
package com.serotonin.mango.rt.dataSource.meta;

import com.serotonin.mango.util.DateUtils;

/**
 * @author Matthew Lohbihler
 */
public class WrapperContext {
	private final long runtime;

	public WrapperContext(long runtime) {
		this.runtime = runtime;
	}

	public long getRuntime() {
		return runtime;
	}

	public long millisInPrev(int periodType) {
		return millisInPrevious(periodType, 1);
	}

	public long millisInPrevious(int periodType) {
		return millisInPrevious(periodType, 1);
	}

	public long millisInPrev(int periodType, int count) {
		return millisInPrevious(periodType, count);
	}

	public long millisInPrevious(int periodType, int count) {
		long to = DateUtils.truncate(runtime, periodType);
		long from = DateUtils.minus(to, periodType, count);
		return to - from;
	}

	public long millisInPast(int periodType) {
		return millisInPast(periodType, 1);
	}

	public long millisInPast(int periodType, int count) {
		long from = DateUtils.minus(runtime, periodType, count);
		return runtime - from;
	}

	@Override
	public String toString() {
		return "{millisInPast(periodType, count), millisInPrev(periodType, count), "
				+ "millisInPrevious(periodType, count)}";
	}

	public String getHelp() {
		return toString();
	}
}
