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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import com.serotonin.mango.web.email.IMsgSubjectContent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.WebContextFactory;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.db.dao.DataSourceDao;
import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.vo.DataPointNameComparator;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.permission.DataPointAccess;
import com.serotonin.mango.vo.permission.PermissionException;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.I18NUtils;
import com.serotonin.web.i18n.LocalizableMessage;
import org.scada_lts.dao.SystemSettingsDAO;
import org.scada_lts.mango.service.UserService;
import org.scada_lts.mango.service.UsersProfileService;

import static com.serotonin.mango.util.LoggingUtils.userInfo;
import static com.serotonin.mango.util.SendUtils.sendMsgTestSync;

public class UsersDwr extends BaseDwr {
	public Log LOG = LogFactory.getLog(UsersDwr.class);

	public Map<String, Object> getInitData() {
		Map<String, Object> initData = new HashMap<String, Object>();

		User user = Common.getUser();
		if (Permissions.hasAdmin(user)) {
			// Users
			initData.put("admin", true);
			initData.put("users", new UserDao().getUsers());
			initData.put("usersProfiles",
					new UsersProfileService().getUsersProfiles());

			// Data sources
			List<DataSourceVO<?>> dataSourceVOs = new DataSourceDao()
					.getDataSources();

			List<Map<String, Object>> dataSources = new ArrayList<Map<String, Object>>(
					dataSourceVOs.size());
			Map<String, Object> ds, dp;
			List<Map<String, Object>> points;
			DataPointDao dataPointDao = new DataPointDao();
			for (DataSourceVO<?> dsvo : dataSourceVOs) {
				ds = new HashMap<String, Object>();
				ds.put("id", dsvo.getId());
				ds.put("name", dsvo.getName());
				points = new LinkedList<Map<String, Object>>();
				for (DataPointVO dpvo : dataPointDao.getDataPoints(
						dsvo.getId(), DataPointNameComparator.instance)) {
					dp = new HashMap<String, Object>();
					dp.put("id", dpvo.getId());
					dp.put("name", dpvo.getName());
					dp.put("settable", dpvo.getPointLocator().isSettable());
					points.add(dp);
				}
				ds.put("points", points);
				dataSources.add(ds);
			}
			initData.put("dataSources", dataSources);
		} else
			initData.put("user", user);

		return initData;
	}

	public User getUser(int id) {
		Permissions.ensureAdmin();
		User user = null;
		if (id == Common.NEW_ID) {
			user = new User();
			user.setDataSourcePermissions(new ArrayList<Integer>(0));
			user.setDataPointPermissions(new ArrayList<DataPointAccess>(0));
		} else {
			user = new UserDao().getUser(id);
		}
		return user;

	}

	public DwrResponseI18n saveUserAdmin(int id, String username,
			String firstName, String lastName,
			String password, String email, String phone, boolean admin,
			boolean disabled, int receiveAlarmEmails,
			boolean receiveOwnAuditEvents, List<Integer> dataSourcePermissions,
			List<DataPointAccess> dataPointPermissions, int usersProfileId, boolean hideMenu,
		    String theme, String homeUrl) {
		Permissions.ensureAdmin();

		// Validate the given information. If there is a problem, return an
		// appropriate error message.
		HttpServletRequest request = WebContextFactory.get()
				.getHttpServletRequest();
		User currentUser = Common.getUser(request);
		UserService userDao = new UserService();

		User user;
		if (id == Common.NEW_ID)
			user = new User();
		else
			user = userDao.getUser(id);
		user.setUsername(username);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		if (!StringUtils.isEmpty(password))
			user.setPassword(Common.encrypt(password));
		user.setEmail(email);
		user.setPhone(phone);
		user.setAdmin(admin);
		user.setDisabled(disabled);
		user.setReceiveAlarmEmails(receiveAlarmEmails);
		user.setReceiveOwnAuditEvents(receiveOwnAuditEvents);
		user.setHideMenu(hideMenu);
		user.setTheme(theme);
		user.setHomeUrl(homeUrl);
		if(usersProfileId == Common.NEW_ID) {
			user.setDataSourcePermissions(dataSourcePermissions);
			user.setDataPointPermissions(dataPointPermissions);
		} else {
			user.setDataSourcePermissions(new ArrayList<>());
			user.setDataPointPermissions(new ArrayList<>());
		}
		if(admin) {
			user.setUserProfileId(Common.NEW_ID);
		} else {
			user.setUserProfileId(usersProfileId);
		}
		user.setLang(SystemSettingsDAO.getValue(SystemSettingsDAO.LANGUAGE, "en"));

		DwrResponseI18n response = new DwrResponseI18n();
		user.validate(response);

		// Check if the username is unique.
		User dupUser = userDao.getUser(username);
		if (id == Common.NEW_ID && dupUser != null)
			response.addMessage(new LocalizableMessage(
					"users.validate.usernameUnique"));
		else if (dupUser != null && id != dupUser.getId())
			response.addMessage(new LocalizableMessage(
					"users.validate.usernameInUse"));

		// Cannot make yourself disabled or not admin
		if (currentUser.getId() == id) {
			if (!admin)
				response.addMessage(new LocalizableMessage(
						"users.validate.adminInvalid"));
			if (disabled)
				response.addMessage(new LocalizableMessage(
						"users.validate.adminDisable"));
		}

		if (!response.getHasMessages()) {
			userDao.saveUser(user);

			// If admin grant permissions to all WL and GViews
			if (admin) {
				// set permission on all views

				// set permission on all watchlists
			}

			if (currentUser.getId() == id)
				// Update the user object in session too. Why not?
				Common.updateUserInSession(request, user);

			response.addData("userId", user.getId());
		}

		return response;
	}

	public DwrResponseI18n saveUser(int id, String firstName, String lastName,
									String password, String email, String phone,
									int receiveAlarmEmails, boolean receiveOwnAuditEvents, int usersProfileId,
									String theme) {

		HttpServletRequest request = WebContextFactory.get()
				.getHttpServletRequest();
		User user = Common.getUser(request);
		if (user.getId() != id)
			throw new PermissionException("Cannot update a different user",
					user);

		UserDao userDao = new UserDao();
		User updateUser = userDao.getUser(id);
		if (!StringUtils.isEmpty(password))
			updateUser.setPassword(Common.encrypt(password));
		updateUser.setEmail(email);
		updateUser.setFirstName(firstName);
		updateUser.setLastName(lastName);
		updateUser.setPhone(phone);
		updateUser.setReceiveAlarmEmails(receiveAlarmEmails);
		updateUser.setReceiveOwnAuditEvents(receiveOwnAuditEvents);
		updateUser.setUserProfileId(usersProfileId);
		updateUser.setTheme(theme);
		DwrResponseI18n response = new DwrResponseI18n();
		updateUser.validate(response);

		if (!response.getHasMessages()) {
			userDao.saveUser(updateUser);
			userDao.updateUserScadaTheme(updateUser);
			Common.updateUserInSession(request, updateUser);
		}

		return response;
	}

	public Map<String, Object> sendTestEmail(String email, String username) {
		Permissions.ensureAdmin();
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ResourceBundle bundle = Common.getBundle();
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("message", new LocalizableMessage("ftl.userTestEmail",
					username));
			IMsgSubjectContent cnt = IMsgSubjectContent.newInstance("testEmail", model,
					bundle, I18NUtils.getMessage(bundle, "ftl.testEmail"),
					Common.UTF8);
			User user = Common.getUser();
			sendMsgTestSync(email, cnt, result, () -> "sendTestEmail from: " + this.getClass().getName()
					+ ", " + userInfo(user));
		} catch (Exception e) {
			result.put("exception", e.getMessage());
		}
		return result;
	}

	public DwrResponseI18n deleteUser(int id) {
		Permissions.ensureAdmin();
		DwrResponseI18n response = new DwrResponseI18n();
		User currentUser = Common.getUser();

		if (currentUser.getId() == id)
			// You can't delete yourself.
			response.addMessage(new LocalizableMessage(
					"users.validate.badDelete"));
		else {
			new UserDao().deleteUser(id);
			UsersProfileService usersProfileService = new UsersProfileService();
			usersProfileService.updatePermissions();
		}

		return response;
	}
}
