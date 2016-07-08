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
package com.serotonin.mango.web.dwr.beans;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.util.StringUtils;

/**
 * This class is used by DWR to package up information needed at the browser for
 * the display of the current state of point information.
 * 
 * @author Matthew Lohbihler
 */
public class ViewComponentState extends BasePointState {
	private String content;
	private String info;
	private boolean graph = false;

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		if (content != null)
			this.content = content.trim();
		else
			this.content = content;
	}

	@Override
	public ViewComponentState clone() {
		try {
			return (ViewComponentState) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new ShouldNeverHappenException(e);
		}
	}

	public void removeEqualValue(ViewComponentState that) {
		super.removeEqualValue(that);
		if (StringUtils.isEqual(content, that.content))
			content = null;
		if (StringUtils.isEqual(info, that.info))
			info = null;
	}

	@Override
	public boolean isEmpty() {
		return content == null && info == null && super.isEmpty();
	}

	public boolean isGraph() {
		return graph;
	}

	public void setGraph(boolean graph) {
		this.graph = graph;
	}

}
