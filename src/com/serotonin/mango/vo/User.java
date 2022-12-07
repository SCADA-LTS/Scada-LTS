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
package com.serotonin.mango.vo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import br.org.scadabr.vo.exporter.ZIPProjectManager;
import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.serotonin.ShouldNeverHappenException;
import com.serotonin.json.*;
import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.db.dao.DataSourceDao;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.event.type.SystemEventType;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.permission.DataPointAccess;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.mango.vo.publish.PublishedPointVO;
import com.serotonin.mango.vo.publish.PublisherVO;
import com.serotonin.mango.web.dwr.beans.DataExportDefinition;
import com.serotonin.mango.web.dwr.beans.EventExportDefinition;
import com.serotonin.mango.web.dwr.beans.ImportTask;
import com.serotonin.mango.web.dwr.beans.TestingUtility;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;
import org.scada_lts.dao.UsersProfileDAO;
import org.scada_lts.mango.service.UsersProfileService;
import org.scada_lts.web.ws.beans.ScadaPrincipal;

@JsonRemoteEntity
public class User implements SetPointSource, HttpSessionBindingListener,
		JsonSerializable {

	private int id = Common.NEW_ID;
	@JsonRemoteProperty
	private String username;
	@JsonRemoteProperty
	private String password;
	@JsonRemoteProperty
	private String firstName;
	@JsonRemoteProperty
	private String lastName;
	@JsonRemoteProperty
	private String email;
	@JsonRemoteProperty
	private String phone;
	@JsonRemoteProperty
	private boolean admin;
	@JsonRemoteProperty
	private boolean disabled;
	private List<Integer> dataSourcePermissions = new ArrayList<>();
	private List<DataPointAccess> dataPointPermissions = new ArrayList<>();

    private List<Integer> dataSourceProfilePermissions = new ArrayList<>();
    private List<DataPointAccess> dataPointProfilePermissions = new ArrayList<>();
    private List<WatchListAccess> watchListProfilePermissions = new ArrayList<>();
    private List<ViewAccess> viewProfilePermissions = new ArrayList<>();

    @JsonRemoteProperty
	private int selectedWatchList;
	@JsonRemoteProperty
	private String homeUrl;
	private long lastLogin;
	private int receiveAlarmEmails;
	@JsonRemoteProperty
	private boolean receiveOwnAuditEvents;

	@JsonRemoteProperty
	private String theme;
	@JsonRemoteProperty
	private boolean hideMenu;



	//
	// Session data. The user object is stored in session, and some other
	// session-based information is cached here
	// for convenience.
	//
	@JsonRemoteProperty
	private int userProfile = Common.NEW_ID;

	private transient View view;
	private transient WatchList watchList;
	private transient DataPointVO editPoint;
	private transient DataSourceVO<?> editDataSource;
	private transient TestingUtility testingUtility;
	private transient Map<String, byte[]> reportImageData;
	private transient PublisherVO<? extends PublishedPointVO> editPublisher;
	private transient ImportTask importTask;
	private transient boolean muted = false;
	private transient DataExportDefinition dataExportDefinition;
	private transient EventExportDefinition eventExportDefinition;
	private transient Map<String, Object> attributes = new ConcurrentHashMap<>();
	private transient boolean hideHeader = false;

	public User() { }

	public User(int id, String username, String email, String phone, boolean admin, boolean disabled, String homeUrl, long lastLogin) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.phone = phone;
		this.admin = admin;
		this.disabled = disabled;
		this.homeUrl = homeUrl;
		this.lastLogin = lastLogin;
	}

	public User(int id, String username, String firstName, String lastName, String email, String phone, boolean admin, boolean disabled, String homeUrl, long lastLogin) {
		this.id = id;
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phone = phone;
		this.admin = admin;
		this.disabled = disabled;
		this.homeUrl = homeUrl;
		this.lastLogin = lastLogin;
	}

	public User(User user) {
		this.id = user.id;
		this.username = user.username;
		this.password = user.password;
		this.email = user.email;
		this.phone = user.phone;
		this.admin = user.admin;
		this.disabled = user.disabled;
		this.dataSourcePermissions = new ArrayList<>(user.dataSourcePermissions);
		this.dataPointPermissions = new ArrayList<>(user.dataPointPermissions);
		this.dataSourceProfilePermissions = new ArrayList<>(user.dataSourceProfilePermissions);
		this.dataPointProfilePermissions = new ArrayList<>(user.dataPointProfilePermissions);
		this.watchListProfilePermissions = new ArrayList<>(user.watchListProfilePermissions);
		this.viewProfilePermissions = new ArrayList<>(user.viewProfilePermissions);
		this.selectedWatchList = user.selectedWatchList;
		this.homeUrl = user.homeUrl;
		this.lastLogin = user.lastLogin;
		this.receiveAlarmEmails = user.receiveAlarmEmails;
		this.receiveOwnAuditEvents = user.receiveOwnAuditEvents;
		this.theme = user.theme;
		this.hideMenu = user.hideMenu;
		this.userProfile = user.userProfile;
		this.view = user.view;
		this.watchList = user.watchList;
		this.editPoint = user.editPoint;
		this.editDataSource = user.editDataSource;
		this.testingUtility = user.testingUtility;
		this.reportImageData = user.reportImageData;
		this.editPublisher = user.editPublisher;
		this.importTask = user.importTask;
		this.muted = user.muted;
		this.dataExportDefinition = user.dataExportDefinition;
		this.eventExportDefinition = user.eventExportDefinition;
		this.attributes = user.attributes;
		this.uploadedProject = user.uploadedProject;
		this.firstName = user.firstName;
		this.lastName = user.lastName;
	}

	public static User onlyId(int userId) {
		return new User(userId, null, null, null, null, null, false, false, null, 0L);
	}

	public static User onlyIdAndProfile(int userId, int profileId) {
		User user = new User(userId, null, null, null, null, null, false, false, null, 0L);
		user.setUserProfileId(profileId);
		return user;
	}

	public static User onlyIdUsername(ScadaPrincipal principal) {
		return new User(principal.getId(), principal.getName(), null, null, null, null, false, false, null, 0L);
	}

	/**
	 * Used for various display purposes.
	 */
	public String getDescription() {
		return username + " (" + id + ")";
	}

	public boolean isFirstLogin() {
		return lastLogin == 0;
	}

	//
	// /
	// / SetPointSource implementation
	// /
	//
	public int getSetPointSourceId() {
		return id;
	}

	public int getSetPointSourceType() {
		return SetPointSource.Types.USER;
	}

	@Override
	public void raiseRecursionFailureEvent() {
		throw new ShouldNeverHappenException("");
	}

	//
	// /
	// / HttpSessionBindingListener implementation
	// /
	//
	public void valueBound(HttpSessionBindingEvent evt) {
		// User is bound to a session when logged in. Notify the event manager.
		SystemEventType.raiseEvent(new SystemEventType(
				SystemEventType.TYPE_USER_LOGIN, id), System
				.currentTimeMillis(), true, new LocalizableMessage(
				"event.login", username));
	}

	public void valueUnbound(HttpSessionBindingEvent evt) {
		// User is unbound from a session when logged out or the session
		// expires.
		SystemEventType.returnToNormal(new SystemEventType(
				SystemEventType.TYPE_USER_LOGIN, id), System
				.currentTimeMillis());

		// Terminate any testing utility
		if (testingUtility != null)
			testingUtility.cancel();
	}

	// Convenience method for JSPs
	@JsonIgnore
	public boolean isDataSourcePermission() {
		return Permissions.hasDataSourcePermission(this);
	}

	//
	// Testing utility management
	public <T extends TestingUtility> T getTestingUtility(Class<T> requiredClass) {
		TestingUtility tu = testingUtility;

		if (tu != null) {
			try {
				return requiredClass.cast(tu);
			} catch (ClassCastException e) {
				tu.cancel();
				testingUtility = null;
			}
		}
		return null;
	}

	public void setTestingUtility(TestingUtility testingUtility) {
		TestingUtility tu = this.testingUtility;
		if (tu != null)
			tu.cancel();
		this.testingUtility = testingUtility;
	}

	public void cancelTestingUtility() {
		setTestingUtility(null);
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	// Properties
	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Deprecated
	public View getView() {
		return view;
	}

	@Deprecated
	public void setView(View view) {
		this.view = view;
	}

	public WatchList getWatchList() {
		return watchList;
	}

	public void setWatchList(WatchList watchList) {
		this.watchList = watchList;
	}

	public DataPointVO getEditPoint() {
		return editPoint;
	}

	public void setEditPoint(DataPointVO editPoint) {
		this.editPoint = editPoint;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public List<Integer> getDataSourcePermissions() {
		return dataSourcePermissions;
		
	}

	public void setDataSourcePermissions(List<Integer> dataSourcePermissions) {
		this.dataSourcePermissions = dataSourcePermissions;
	}

	public List<DataPointAccess> getDataPointPermissions() {
		return dataPointPermissions;
		
	}

	public void setDataPointPermissions(List<DataPointAccess> dataPointPermissions) {
		this.dataPointPermissions = dataPointPermissions;
	}

	public DataSourceVO<?> getEditDataSource() {
		return editDataSource;
	}

	public void setEditDataSource(DataSourceVO<?> editDataSource) {
		this.editDataSource = editDataSource;
	}

	public int getSelectedWatchList() {
		return selectedWatchList;
	}

	public void setSelectedWatchList(int selectedWatchList) {
		this.selectedWatchList = selectedWatchList;
	}

	public String getHomeUrl() {
		return homeUrl;
	}

	public void setHomeUrl(String homeUrl) {
		this.homeUrl = homeUrl;
	}

	public long getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(long lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Map<String, byte[]> getReportImageData() {
		return reportImageData;
	}

	public void setReportImageData(Map<String, byte[]> reportImageData) {
		this.reportImageData = reportImageData;
	}

	public PublisherVO<? extends PublishedPointVO> getEditPublisher() {
		return editPublisher;
	}

	public void setEditPublisher(
			PublisherVO<? extends PublishedPointVO> editPublisher) {
		this.editPublisher = editPublisher;
	}

	public ImportTask getImportTask() {
		return importTask;
	}

	public void setImportTask(ImportTask importTask) {
		this.importTask = importTask;
	}

	public boolean isMuted() {
		return muted;
	}

	public void setMuted(boolean muted) {
		this.muted = muted;
	}

	public int getReceiveAlarmEmails() {
		return receiveAlarmEmails;
	}

	public void setReceiveAlarmEmails(int receiveAlarmEmails) {
		this.receiveAlarmEmails = receiveAlarmEmails;
	}

	public boolean isReceiveOwnAuditEvents() {
		return receiveOwnAuditEvents;
	}

	public void setReceiveOwnAuditEvents(boolean receiveOwnAuditEvents) {
		this.receiveOwnAuditEvents = receiveOwnAuditEvents;
	}

	public DataExportDefinition getDataExportDefinition() {
		return dataExportDefinition;
	}

	public void setDataExportDefinition(
			DataExportDefinition dataExportDefinition) {
		this.dataExportDefinition = dataExportDefinition;
	}

	public EventExportDefinition getEventExportDefinition() {
		return eventExportDefinition;
	}

	public void setEventExportDefinition(
			EventExportDefinition eventExportDefinition) {
		this.eventExportDefinition = eventExportDefinition;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public boolean isHideMenu() {
		return hideMenu;
	}

	public void setHideMenu(boolean hideMenu) {
		this.hideMenu = hideMenu;
	}

	public boolean isHideHeader() {
		return hideHeader;
	}

	public void setHideHeader(boolean hideHeader) {
		this.hideHeader = hideHeader;
	}

	public void setAttribute(String key, Object value) {
		attributes.put(key, value);
	}

	public Object removeAttribute(String key) {
		return attributes.remove(key);
	}

	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String key) {
		return (T) attributes.get(key);
	}

	public void validate(DwrResponseI18n response) {
		if (StringUtils.isEmpty(username))
			response.addMessage("username", new LocalizableMessage(
					"validate.required"));
		if (StringUtils.isEmpty(email))
			response.addMessage("email", new LocalizableMessage(
					"validate.required"));
		if (id == Common.NEW_ID && StringUtils.isEmpty(password))
			response.addMessage("password", new LocalizableMessage(
					"validate.required"));

		// Check field lengths
		if (StringUtils.isLengthGreaterThan(username, 40))
			response.addMessage("username", new LocalizableMessage(
					"validate.notLongerThan", 40));
		if (StringUtils.isLengthGreaterThan(email, 255))
			response.addMessage("email", new LocalizableMessage(
					"validate.notLongerThan", 255));
		if (StringUtils.isLengthGreaterThan(phone, 40))
			response.addMessage("phone", new LocalizableMessage(
					"validate.notLongerThan", 40));
	}

	//
	// /
	// / Serialization
	// /
	//
	@Override
	public void jsonDeserialize(JsonReader reader, JsonObject json) {
		// Note: data source permissions are explicitly deserialized by the
		// import/export because the data sources and
		// points need to be certain to exist before we can resolve the xids.
	}

	public void jsonDeserializePermissions(JsonReader reader, JsonObject json)
			throws JsonException {
		if (admin) {
			dataSourcePermissions.clear();
			dataPointPermissions.clear();
		} else {
			JsonArray jsonDataSources = json
					.getJsonArray("dataSourcePermissions");
			if (jsonDataSources != null) {
				dataSourcePermissions.clear();
				DataSourceDao dataSourceDao = new DataSourceDao();

				for (JsonValue jv : jsonDataSources.getElements()) {
					String xid = jv.toJsonString().getValue();
					DataSourceVO<?> ds = dataSourceDao.getDataSource(xid);
					if (ds == null)
						throw new LocalizableJsonException(
								"emport.error.missingSource", xid);
					dataSourcePermissions.add(ds.getId());
				}
			}

			JsonArray jsonPoints = json.getJsonArray("dataPointPermissions");
			if (jsonPoints != null) {
				// Get a list of points to which permission already exists due
				// to data source access.
				DataPointDao dataPointDao = new DataPointDao();
				List<Integer> permittedPoints = new ArrayList<Integer>();
				for (Integer dsId : dataSourcePermissions) {
					for (DataPointVO dp : dataPointDao
							.getDataPoints(dsId, null))
						permittedPoints.add(dp.getId());
				}

				dataPointPermissions.clear();

				for (JsonValue jv : jsonPoints.getElements()) {
					DataPointAccess access = reader.readPropertyValue(jv,
							DataPointAccess.class, null);
					if (!permittedPoints.contains(access.getDataPointId()))
						// The user doesn't already have access to the point.
						dataPointPermissions.add(access);
				}
			}

			UsersProfileDAO usersProfileDAO = new UsersProfileDAO();
			String userProfileXid = json.getString("userProfileXid");
			Integer userProfileId = json.getInt("userProfile");
			if (userProfileXid != null && !userProfileXid.isEmpty()) {
				Optional<UsersProfileVO> usersProfileVO = usersProfileDAO.selectProfileByXid(userProfileXid);
				if(!usersProfileVO.isPresent()) {
					throw new LocalizableJsonException("emport.error.missingObject", "profile: " + userProfileXid);
				}
				userProfile = usersProfileVO.get().getId();
			} else if(userProfileId != null && userProfileId != 0 && userProfileId != Common.NEW_ID) {
				Optional<UsersProfileVO> usersProfileVO = usersProfileDAO.selectProfileById(userProfileId);
				if(!usersProfileVO.isPresent()) {
					throw new LocalizableJsonException("emport.error.missingObject", "profile: " + userProfileId);
				}
				userProfile = usersProfileVO.get().getId();
			}
		}
	}

	@Override
	public void jsonSerialize(Map<String, Object> map) {
		if (!admin) {
			List<String> dsXids = new ArrayList<String>();
			DataSourceDao dataSourceDao = new DataSourceDao();
			for (Integer dsId : getDataSourcePermissions())
				dsXids.add(dataSourceDao.getDataSource(dsId).getXid());
			map.put("dataSourcePermissions", dsXids);
			map.put("dataPointPermissions", dataPointPermissions);
			UsersProfileService usersProfileService = new UsersProfileService();
			UsersProfileVO profile = usersProfileService.getUserProfileById(userProfile);
			if(profile != null)
				map.put("userProfileXid", profile.getXid());
			else
				map.put("userProfileXid", "");
		} else {
			map.put("dataSourcePermissions", Collections.emptyList());
			map.put("dataPointPermissions", Collections.emptyList());
			map.put("userProfileXid", "");
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		final User other = (User) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public void setUploadedProject(ZIPProjectManager uploadedProject) {
		this.uploadedProject = uploadedProject;
	}

	public ZIPProjectManager getUploadedProject() {
		return uploadedProject;
	}

	private ZIPProjectManager uploadedProject;

	public void setUserProfile(UsersProfileVO profile) {
		this.userProfile = profile.getId();
	}

	public void setUserProfileId(int userProfileId) {
		this.userProfile =userProfileId;
	}

	public int getUserProfile() {
		return userProfile;
	}

	public void resetUserProfile() {
		this.userProfile = Common.NEW_ID;
	}

	@Override
	public void pointSetComplete() {
		// TODO Auto-generated method stub

	}

    public List<Integer> getDataSourceProfilePermissions() {
        return dataSourceProfilePermissions;
    }

    public void setDataSourceProfilePermissions(List<Integer> dataSourceProfilePermissions) {
        this.dataSourceProfilePermissions = dataSourceProfilePermissions;
    }

    public List<DataPointAccess> getDataPointProfilePermissions() {
        return dataPointProfilePermissions;
    }

    public void setDataPointProfilePermissions(List<DataPointAccess> dataPointProfilePermissions) {
        this.dataPointProfilePermissions = dataPointProfilePermissions;
    }

    public List<WatchListAccess> getWatchListProfilePermissions() {
        return watchListProfilePermissions;
    }

    public void setWatchListProfilePermissions(List<WatchListAccess> watchListProfilePermissions) {
        this.watchListProfilePermissions = watchListProfilePermissions;
    }

    public List<ViewAccess> getViewProfilePermissions() {
        return viewProfilePermissions;
    }

    public void setViewProfilePermissions(List<ViewAccess> viewProfilePermissions) {
        this.viewProfilePermissions = viewProfilePermissions;
    }

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", username='" + username + '\'' +
				", password='" + password + '\'' +
				", email='" + email + '\'' +
				", phone='" + phone + '\'' +
				", admin=" + admin +
				", disabled=" + disabled +
				", homeUrl='" + homeUrl + '\'' +
				", lastLogin=" + lastLogin +
				", receiveAlarmEmails=" + receiveAlarmEmails +
				", receiveOwnAuditEvents=" + receiveOwnAuditEvents +
				", theme='" + theme + '\'' +
				", hideMenu=" + hideMenu +
				", userProfile=" + userProfile +
				", muted=" + muted +
				", attributes=" + attributes +
				'}';
	}
}
