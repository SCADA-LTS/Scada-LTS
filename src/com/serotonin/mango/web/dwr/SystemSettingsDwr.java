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

import br.org.scadabr.db.configuration.ConfigurationDB;
import com.serotonin.InvalidArgumentException;
import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.db.dao.EventDao;
import org.scada_lts.dao.SystemSettingsDAO;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.rt.event.type.SystemEventType;
import com.serotonin.mango.rt.maint.DataPurge;
import com.serotonin.mango.rt.maint.VersionCheck;
import com.serotonin.mango.rt.maint.work.EmailWorkItem;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.bean.PointHistoryCount;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.mango.web.dwr.beans.IntegerPair;
import com.serotonin.mango.web.email.MangoEmailContent;
import com.serotonin.util.DirectoryInfo;
import com.serotonin.util.DirectoryUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.dwr.MethodFilter;
import com.serotonin.web.i18n.I18NUtils;
import com.serotonin.web.i18n.LocalizableMessage;
import org.scada_lts.mango.service.SystemSettingsService;
import org.scada_lts.utils.ColorUtils;
import org.scada_lts.web.mvc.api.json.JsonSettingsMisc;

import java.io.File;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class SystemSettingsDwr extends BaseDwr {
	@MethodFilter
	public Map<String, Object> getSettings() {
		Permissions.ensureAdmin();
		Map<String, Object> settings = new HashMap<String, Object>();

		// Email
		settings.put(SystemSettingsDAO.EMAIL_SMTP_HOST,
				SystemSettingsDAO.getValue(SystemSettingsDAO.EMAIL_SMTP_HOST));
		settings.put(SystemSettingsDAO.EMAIL_SMTP_PORT, SystemSettingsDAO
				.getIntValue(SystemSettingsDAO.EMAIL_SMTP_PORT));
		settings.put(SystemSettingsDAO.EMAIL_FROM_ADDRESS, SystemSettingsDAO
				.getValue(SystemSettingsDAO.EMAIL_FROM_ADDRESS));
		settings.put(SystemSettingsDAO.EMAIL_FROM_NAME,
				SystemSettingsDAO.getValue(SystemSettingsDAO.EMAIL_FROM_NAME));
		settings.put(SystemSettingsDAO.EMAIL_AUTHORIZATION, SystemSettingsDAO
				.getBooleanValue(SystemSettingsDAO.EMAIL_AUTHORIZATION));
		settings.put(SystemSettingsDAO.EMAIL_SMTP_USERNAME, SystemSettingsDAO
				.getValue(SystemSettingsDAO.EMAIL_SMTP_USERNAME));
		settings.put(SystemSettingsDAO.EMAIL_SMTP_PASSWORD, SystemSettingsDAO
				.getValue(SystemSettingsDAO.EMAIL_SMTP_PASSWORD));
		settings.put(SystemSettingsDAO.EMAIL_TLS,
				SystemSettingsDAO.getBooleanValue(SystemSettingsDAO.EMAIL_TLS));
		settings.put(SystemSettingsDAO.EMAIL_CONTENT_TYPE, SystemSettingsDAO
				.getIntValue(SystemSettingsDAO.EMAIL_CONTENT_TYPE));

		// System event types
		settings.put("systemEventTypes", SystemEventType.getSystemEventTypes());

		// System event types
		settings.put("auditEventTypes", AuditEventType.getAuditEventTypes());

		// Http
		settings.put(SystemSettingsDAO.HTTP_CLIENT_USE_PROXY, SystemSettingsDAO
				.getBooleanValue(SystemSettingsDAO.HTTP_CLIENT_USE_PROXY));
		settings.put(SystemSettingsDAO.HTTP_CLIENT_PROXY_SERVER,
				SystemSettingsDAO
						.getValue(SystemSettingsDAO.HTTP_CLIENT_PROXY_SERVER));
		settings.put(SystemSettingsDAO.HTTP_CLIENT_PROXY_PORT,
				SystemSettingsDAO
						.getIntValue(SystemSettingsDAO.HTTP_CLIENT_PROXY_PORT));
		settings.put(SystemSettingsDAO.HTTP_CLIENT_PROXY_USERNAME,
				SystemSettingsDAO
						.getValue(SystemSettingsDAO.HTTP_CLIENT_PROXY_USERNAME));
		settings.put(SystemSettingsDAO.HTTP_CLIENT_PROXY_PASSWORD,
				SystemSettingsDAO
						.getValue(SystemSettingsDAO.HTTP_CLIENT_PROXY_PASSWORD));

		// Misc
		settings.put(SystemSettingsDAO.EVENT_PURGE_PERIOD_TYPE,
				SystemSettingsDAO
						.getIntValue(SystemSettingsDAO.EVENT_PURGE_PERIOD_TYPE));
		settings.put(SystemSettingsDAO.EVENT_PURGE_PERIODS, SystemSettingsDAO
				.getIntValue(SystemSettingsDAO.EVENT_PURGE_PERIODS));
		settings.put(
				SystemSettingsDAO.REPORT_PURGE_PERIOD_TYPE,
				SystemSettingsDAO
						.getIntValue(SystemSettingsDAO.REPORT_PURGE_PERIOD_TYPE));
		settings.put(SystemSettingsDAO.REPORT_PURGE_PERIODS, SystemSettingsDAO
				.getIntValue(SystemSettingsDAO.REPORT_PURGE_PERIODS));
		settings.put(SystemSettingsDAO.UI_PERFORMANCE, SystemSettingsDAO
				.getIntValue(SystemSettingsDAO.UI_PERFORMANCE));
		// settings.put(SystemSettingsDAO.GROVE_LOGGING, SystemSettingsDAO
		// .getBooleanValue(SystemSettingsDAO.GROVE_LOGGING));
		settings.put(
				SystemSettingsDAO.FUTURE_DATE_LIMIT_PERIOD_TYPE,
				SystemSettingsDAO
						.getIntValue(SystemSettingsDAO.FUTURE_DATE_LIMIT_PERIOD_TYPE));
		settings.put(
				SystemSettingsDAO.FUTURE_DATE_LIMIT_PERIODS,
				SystemSettingsDAO
						.getIntValue(SystemSettingsDAO.FUTURE_DATE_LIMIT_PERIODS));

		// System
		// settings.put(
		// SystemSettingsDAO.NEW_VERSION_NOTIFICATION_LEVEL,
		// SystemSettingsDAO
		// .getValue(SystemSettingsDAO.NEW_VERSION_NOTIFICATION_LEVEL));
		settings.put(SystemSettingsDAO.INSTANCE_DESCRIPTION, SystemSettingsDAO
				.getValue(SystemSettingsDAO.INSTANCE_DESCRIPTION));

		// Language
		settings.put(SystemSettingsDAO.LANGUAGE,
				SystemSettingsDAO.getValue(SystemSettingsDAO.LANGUAGE));

		// Colours
		settings.put(SystemSettingsDAO.CHART_BACKGROUND_COLOUR,
				SystemSettingsDAO
						.getValue(SystemSettingsDAO.CHART_BACKGROUND_COLOUR));
		settings.put(SystemSettingsDAO.PLOT_BACKGROUND_COLOUR,
				SystemSettingsDAO
						.getValue(SystemSettingsDAO.PLOT_BACKGROUND_COLOUR));
		settings.put(SystemSettingsDAO.PLOT_GRIDLINE_COLOUR, SystemSettingsDAO
				.getValue(SystemSettingsDAO.PLOT_GRIDLINE_COLOUR));

		SystemSettingsService systemSettingsService = new SystemSettingsService();
		settings.put(SystemSettingsDAO.DATAPOINT_RUNTIME_VALUE_SYNCHRONIZED,
				systemSettingsService.getDataPointRtValueSynchronized().getName());

		return settings;
	}

	@MethodFilter
	public Map<String, Object> getDatabaseSize() {
		Permissions.ensureAdmin();

		Map<String, Object> data = new HashMap<String, Object>();

		// Database size
		File dataDirectory = Common.ctx.getDatabaseAccess().getDataDirectory();
		long dbSize = 0;
		if (dataDirectory != null) {
			DirectoryInfo dbInfo = DirectoryUtils
					.getDirectorySize(dataDirectory);
			dbSize = dbInfo.getSize();
			data.put("databaseSize", DirectoryUtils.bytesDescription(dbSize));
		} else
			data.put("databaseSize", "(" + getMessage("common.unknown") + ")");

		// Filedata data
		DirectoryInfo fileDatainfo = DirectoryUtils.getDirectorySize(new File(
				Common.getFiledataPath()));
		long filedataSize = fileDatainfo.getSize();
		data.put("filedataCount", fileDatainfo.getCount());
		data.put("filedataSize", DirectoryUtils.bytesDescription(filedataSize));

		data.put("totalSize",
				DirectoryUtils.bytesDescription(dbSize + filedataSize));

		if (checkTypeDB().equals("mysql")) {
			double size = new SystemSettingsDAO().getDataBaseSize();
			data.put("databaseSize", size + " MB");
			data.put("filedataCount", 0);
			data.put("filedataSize", 0);
			data.put("totalSize", size + " MB");
			data.put("filedataCount", 0);
			data.put("filedataCount", 0);
		}

		// Point history counts.
		List<PointHistoryCount> counts = new DataPointDao()
				.getTopPointHistoryCounts();
		int sum = 0;
		for (PointHistoryCount c : counts)
			sum += c.getCount();

		data.put("historyCount", sum);
		data.put("topPoints", counts);
		data.put("eventCount", new EventDao().getEventCount());

		return data;
	}

	@MethodFilter
	public void saveEmailSettings(String host, int port, String from,
			String name, boolean auth, String username, String password,
			boolean tls, int contentType) {
		Permissions.ensureAdmin();
		SystemSettingsDAO SystemSettingsDAO = new SystemSettingsDAO();
		SystemSettingsDAO.setValue(SystemSettingsDAO.EMAIL_SMTP_HOST, host);
		SystemSettingsDAO.setIntValue(SystemSettingsDAO.EMAIL_SMTP_PORT, port);
		SystemSettingsDAO.setValue(SystemSettingsDAO.EMAIL_FROM_ADDRESS, from);
		SystemSettingsDAO.setValue(SystemSettingsDAO.EMAIL_FROM_NAME, name);
		SystemSettingsDAO.setBooleanValue(
				SystemSettingsDAO.EMAIL_AUTHORIZATION, auth);
		SystemSettingsDAO.setValue(SystemSettingsDAO.EMAIL_SMTP_USERNAME,
				username);
		SystemSettingsDAO.setValue(SystemSettingsDAO.EMAIL_SMTP_PASSWORD,
				password);
		SystemSettingsDAO.setBooleanValue(SystemSettingsDAO.EMAIL_TLS, tls);
		SystemSettingsDAO.setIntValue(SystemSettingsDAO.EMAIL_CONTENT_TYPE,
				contentType);
	}

	@MethodFilter
	public Map<String, Object> sendTestEmail(String host, int port,
			String from, String name, boolean auth, String username,
			String password, boolean tls, int contentType) {
		Permissions.ensureAdmin();

		// Save the settings
		saveEmailSettings(host, port, from, name, auth, username, password,
				tls, contentType);

		// Get the web context information
		User user = Common.getUser();

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ResourceBundle bundle = getResourceBundle();
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("message", new LocalizableMessage(
					"systemSettings.testEmail"));
			MangoEmailContent cnt = new MangoEmailContent("testEmail", model,
					bundle, I18NUtils.getMessage(bundle, "ftl.testEmail"),
					Common.UTF8);
			EmailWorkItem.queueEmail(user.getEmail(), cnt);
			result.put("message", new LocalizableMessage(
					"common.testEmailSent", user.getEmail()));
		} catch (Exception e) {
			result.put("exception", e.getMessage());
		}
		return result;
	}

	@MethodFilter
	public void saveSystemEventAlarmLevels(List<IntegerPair> eventAlarmLevels) {
		Permissions.ensureAdmin();
		for (IntegerPair eventAlarmLevel : eventAlarmLevels)
			SystemEventType.setEventTypeAlarmLevel(eventAlarmLevel.getI1(),
					eventAlarmLevel.getI2());
	}

	@MethodFilter
	public void saveAuditEventAlarmLevels(List<IntegerPair> eventAlarmLevels) {
		Permissions.ensureAdmin();
		for (IntegerPair eventAlarmLevel : eventAlarmLevels)
			AuditEventType.setEventTypeAlarmLevel(eventAlarmLevel.getI1(),
					eventAlarmLevel.getI2());
	}

	@MethodFilter
	public void saveHttpSettings(boolean useProxy, String host, int port,
			String username, String password) {
		Permissions.ensureAdmin();
		SystemSettingsDAO SystemSettingsDAO = new SystemSettingsDAO();
		SystemSettingsDAO.setBooleanValue(
				SystemSettingsDAO.HTTP_CLIENT_USE_PROXY, useProxy);
		SystemSettingsDAO.setValue(SystemSettingsDAO.HTTP_CLIENT_PROXY_SERVER,
				host);
		SystemSettingsDAO.setIntValue(SystemSettingsDAO.HTTP_CLIENT_PROXY_PORT,
				port);
		SystemSettingsDAO.setValue(
				SystemSettingsDAO.HTTP_CLIENT_PROXY_USERNAME, username);
		SystemSettingsDAO.setValue(
				SystemSettingsDAO.HTTP_CLIENT_PROXY_PASSWORD, password);
	}

	@MethodFilter
	public void saveMiscSettings(int uiPerformance, String dataPointRtValueSynchronized) {
		Permissions.ensureAdmin();

		SystemSettingsService systemSettingsService = new SystemSettingsService();

		JsonSettingsMisc jsonSettingsMisc = new JsonSettingsMisc();
		jsonSettingsMisc.setUiPerformance(uiPerformance);
		jsonSettingsMisc.setDataPointRuntimeValueSynchronized(dataPointRtValueSynchronized);

		systemSettingsService.saveMiscSettings(jsonSettingsMisc);
	}

	@MethodFilter
	public void saveDataRetentionSettings(int eventPurgePeriodType,
								 int eventPurgePeriods, int reportPurgePeriodType,
								 int reportPurgePeriods, boolean groveLogging,
								 int futureDateLimitPeriodType, int futureDateLimitPeriods) {
		Permissions.ensureAdmin();
		SystemSettingsDAO systemSettingsDAO = new SystemSettingsDAO();
		systemSettingsDAO
				.setIntValue(SystemSettingsDAO.EVENT_PURGE_PERIOD_TYPE,
						eventPurgePeriodType);
		systemSettingsDAO.setIntValue(SystemSettingsDAO.EVENT_PURGE_PERIODS,
				eventPurgePeriods);
		systemSettingsDAO.setIntValue(
				SystemSettingsDAO.REPORT_PURGE_PERIOD_TYPE,
				reportPurgePeriodType);
		systemSettingsDAO.setIntValue(SystemSettingsDAO.REPORT_PURGE_PERIODS,
				reportPurgePeriods);
		systemSettingsDAO.setBooleanValue(SystemSettingsDAO.GROVE_LOGGING,
				groveLogging);
		systemSettingsDAO.setIntValue(
				SystemSettingsDAO.FUTURE_DATE_LIMIT_PERIOD_TYPE,
				futureDateLimitPeriodType);
		systemSettingsDAO.setIntValue(
				SystemSettingsDAO.FUTURE_DATE_LIMIT_PERIODS,
				futureDateLimitPeriods);
	}

	@MethodFilter
	public DwrResponseI18n saveColourSettings(String chartBackgroundColour,
			String plotBackgroundColour, String plotGridlineColour) {
		Permissions.ensureAdmin();

		DwrResponseI18n response = new DwrResponseI18n();

		try {
			ColorUtils.toColor(chartBackgroundColour);
		} catch (InvalidArgumentException e) {
			response.addContextualMessage(
					SystemSettingsDAO.CHART_BACKGROUND_COLOUR,
					"systemSettings.validation.invalidColour");
		}

		try {
			ColorUtils.toColor(plotBackgroundColour);
		} catch (InvalidArgumentException e) {
			response.addContextualMessage(
					SystemSettingsDAO.PLOT_BACKGROUND_COLOUR,
					"systemSettings.validation.invalidColour");
		}

		try {
			ColorUtils.toColor(plotGridlineColour);
		} catch (InvalidArgumentException e) {
			response.addContextualMessage(
					SystemSettingsDAO.PLOT_GRIDLINE_COLOUR,
					"systemSettings.validation.invalidColour");
		}

		if (!response.getHasMessages()) {
			SystemSettingsDAO SystemSettingsDAO = new SystemSettingsDAO();
			SystemSettingsDAO.setValue(
					SystemSettingsDAO.CHART_BACKGROUND_COLOUR,
					chartBackgroundColour);
			SystemSettingsDAO.setValue(
					SystemSettingsDAO.PLOT_BACKGROUND_COLOUR,
					plotBackgroundColour);
			SystemSettingsDAO.setValue(SystemSettingsDAO.PLOT_GRIDLINE_COLOUR,
					plotGridlineColour);
		}

		return response;
	}

	@MethodFilter
	public void saveInfoSettings(String newVersionNotificationLevel,
			String instanceDescription) {
		Permissions.ensureAdmin();
		SystemSettingsDAO systemSettingsDAO = new SystemSettingsDAO();
		systemSettingsDAO.setValue(
				SystemSettingsDAO.NEW_VERSION_NOTIFICATION_LEVEL,
				newVersionNotificationLevel);
		systemSettingsDAO.setValue(SystemSettingsDAO.INSTANCE_DESCRIPTION,
				instanceDescription);
	}

	@MethodFilter
	public String newVersionCheck(String newVersionNotificationLevel) {
		Permissions.ensureAdmin();
		try {
			return getMessage(VersionCheck
					.newVersionCheck(newVersionNotificationLevel));
		} catch (SocketTimeoutException e) {
			return getMessage("systemSettings.versionCheck1");
		} catch (Exception e) {
			return getMessage(new LocalizableMessage(
					"systemSettings.versionCheck2", e.getClass().getName(),
					e.getMessage()));
		}
	}

	@MethodFilter
	public void saveLanguageSettings(String language) {
		Permissions.ensureAdmin();
		SystemSettingsDAO SystemSettingsDAO = new SystemSettingsDAO();
		SystemSettingsDAO.setValue(SystemSettingsDAO.LANGUAGE, language);
		Common.setSystemLanguage(language);
	}

	@MethodFilter
	public void purgeNow() {
		Permissions.ensureAdmin();
		DataPurge dataPurge = new DataPurge();
		dataPurge.execute(System.currentTimeMillis());
	}

	@MethodFilter
	public LocalizableMessage purgeAllData() {
		Permissions.ensureAdmin();
		long cnt = Common.ctx.getRuntimeManager().purgeDataPointValues();
		return new LocalizableMessage("systemSettings.purgeDataComplete", cnt);
	}

	@MethodFilter
	public void useDerbyDB() {
		Permissions.ensureAdmin();
		ConfigurationDB.useDerbyDB();
	}

	@MethodFilter
	public void useMysqlDB() {
		Permissions.ensureAdmin();
		ConfigurationDB.useMysqlDB();
	}

	@MethodFilter
	public void useMssqlDB() {
		Permissions.ensureAdmin();
		ConfigurationDB.useMssqlDB();
	}

	@MethodFilter
	public String checkTypeDB() {
		return Common.getEnvironmentProfile().getString("db.type", "derby");
	}

	@MethodFilter
	public String getAppServer() {
		return Common.ctx.getServletContext().getServerInfo();
	}
}
