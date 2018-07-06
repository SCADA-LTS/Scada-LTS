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
package com.serotonin.mango.db.dao;

import com.serotonin.mango.view.View;
import org.apache.commons.lang3.tuple.MutablePair;
import org.scada_lts.mango.convert.IdNameToIntValuePair;
import org.scada_lts.mango.service.ViewService;

import java.util.List;

public class ViewDao {
	
	private ViewService viewService;
	
	public ViewDao() {
		viewService = new ViewService();
	}
	
	public List<View> getViews() {
		return viewService.getViews();
	}

	public List<View> getViews(int userId, int userProfileId) {
		return viewService.getViews(userId, userProfileId);
	}

	public List<MutablePair<Integer, String>>  getViewNames(int userId, int userProfileId) {
		return IdNameToIntValuePair.convert(viewService.getViewNames(userId, userProfileId));
	}

	public List<MutablePair<Integer, String>>  getAllViewNames() {
		return IdNameToIntValuePair.convert(viewService.getAllViewNames());
	}

	public List<MutablePair<Integer, String>>  getViewNamesWithReadOrWritePermissions(
			int userId, int userProfileId) {
		return IdNameToIntValuePair.convert(viewService.getViewNamesWithReadOrWritePermissions(userId, userProfileId));
	}

	public View getView(int id) {
		return viewService.getView(id);
		
	}

	public View getViewByXid(String xid) {
		return viewService.getViewByXid(xid);
	}

	public View getView(String name) {
		return viewService.getView(name);
	}

	public String generateUniqueXid() {
		return viewService.generateUniqueXid();
	}

	public boolean isXidUnique(String xid, int excludeId) {
		return viewService.isXidUnique(xid, excludeId);
	}

	public void saveView(final View view) {
		viewService.saveView(view);
	}

	public void removeView(final int viewId) {
		viewService.removeView(viewId);
	}

	public void removeUserFromView(int viewId, int userId) {
		viewService.removeUserFromView(viewId, userId);
	}
	
}
