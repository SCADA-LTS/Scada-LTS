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
package com.serotonin.mango.web.dwr;

import com.serotonin.db.IntValuePair;
import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.db.dao.DataSourceDao;
import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.DataSourceVO.Type;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.web.dwr.DwrResponseI18n;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.SystemSettingsDAO;
import org.scada_lts.ds.state.UserChangeEnableStateDs;
import org.scada_lts.mango.service.UsersProfileService;

import java.util.*;

/**
 * @author Matthew Lohbihler
 */
public class DataSourceListDwr extends BaseDwr {
	private static final Log LOG = LogFactory.getLog(DataSourceListDwr.class);

	public DwrResponseI18n init() {
		Permissions.ensureAdmin();
		DwrResponseI18n response = new DwrResponseI18n();

		if (Common.getUser().isAdmin()) {
			List<IntValuePair> translatedTypes = new ArrayList<IntValuePair>();
			List<Type> types = new ArrayList<Type>(EnumSet.allOf(Type.class));
			// because sort not work in openJdk 1.7
			for (int i = 0; i < types.size()-1; i++) { 
				 Type temp;
				 for(int j = 1; j< types.size() -i; j++) {
					 Type o1 = types.get(j-1);
					 Type o2 = types.get(j);
					 if (getMessage(o1.getKey()).toString().compareToIgnoreCase(getMessage(o2.getKey()).toString())>0){
						 temp = types.get(j-1);
						 types.set(j-1,types.get(j));
						 types.set(j,temp);
					 }
				 }
			}
			
			/*types.sort(new Comparator<DataSourceVO.Type>(){
				@Override
				public int compare(Type o1, Type o2) {
					return getMessage(o1.getKey()).toString().compareToIgnoreCase(getMessage(o2.getKey()).toString());
				}
			});*/
			
			for (Type type : types) {
				if (type != Type.RADIUINO) {
					// Allow customization settings to overwrite the default display
					// value.
					boolean display = SystemSettingsDAO.getBooleanValue(type.name()
							+ SystemSettingsDAO.DATASOURCE_DISPLAY_SUFFIX,
							type.isDisplay());
					if (display)
						translatedTypes.add(new IntValuePair(type.getId(),
								getMessage(type.getKey())));
				}
			}
			
			
			response.addData("types", translatedTypes);
		}

		return response;
	}

	public Map<String, Object> toggleDataSource(int dataSourceId) {
		Permissions.ensureDataSourcePermission(Common.getUser(), dataSourceId);
		RuntimeManager runtimeManager = Common.ctx.getRuntimeManager();
		DataSourceVO<?> dataSource = runtimeManager.getDataSource(dataSourceId);
		Map<String, Object> result = new HashMap<String, Object>();

		dataSource.setEnabled(!dataSource.isEnabled());
		dataSource.setState(new UserChangeEnableStateDs());
		runtimeManager.saveDataSource(dataSource);

		result.put("state", dataSource.getState().getDescribe());
		result.put("enabled", dataSource.isEnabled());
		result.put("id", dataSourceId);
		return result;
	}

	public int deleteDataSource(int dataSourceId) {
		Permissions.ensureAdmin();
		Common.ctx.getRuntimeManager().deleteDataSource(dataSourceId);
		UsersProfileService usersProfileService = new UsersProfileService();
		usersProfileService.updateDataSourcePermissions();
		return dataSourceId;
	}

	public DwrResponseI18n toggleDataPoint(int dataPointId) {
		DataPointVO dataPoint = new DataPointDao().getDataPoint(dataPointId);
		Permissions.ensureDataSourcePermission(Common.getUser(),
				dataPoint.getDataSourceId());

		RuntimeManager runtimeManager = Common.ctx.getRuntimeManager();
		dataPoint.setEnabled(!dataPoint.isEnabled());
		runtimeManager.saveDataPoint(dataPoint);

		DwrResponseI18n response = new DwrResponseI18n();
		response.addData("id", dataPointId);
		response.addData("enabled", dataPoint.isEnabled());
		return response;
	}

	public int copyDataSource(int dataSourceId) {
		Permissions.ensureDataSourcePermission(Common.getUser(), dataSourceId);
		int dsId = new DataSourceDao().copyDataSource(dataSourceId,
				getResourceBundle());
		new UserDao().populateUserPermissions(Common.getUser());
		return dsId;
	}
}
