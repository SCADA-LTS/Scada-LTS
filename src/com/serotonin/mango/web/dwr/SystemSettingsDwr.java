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
import com.serotonin.mango.web.email.IMsgSubjectContent;
import com.serotonin.mango.web.mvc.controller.ScadaLocaleUtils;
import org.scada_lts.dao.SystemSettingsDAO;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.rt.event.type.SystemEventType;
import com.serotonin.mango.rt.maint.DataPurge;
import com.serotonin.mango.rt.maint.VersionCheck;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.bean.PointHistoryCount;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.mango.web.dwr.beans.IntegerPair;
import com.serotonin.util.DirectoryInfo;
import com.serotonin.util.DirectoryUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.I18NUtils;
import com.serotonin.web.i18n.LocalizableMessage;
import org.scada_lts.mango.adapter.MangoEvent;
import org.scada_lts.mango.service.EventService;
import org.scada_lts.mango.service.SystemSettingsService;
import org.scada_lts.utils.ColorUtils;
import org.scada_lts.web.mvc.api.json.JsonSettingsHttp;
import org.scada_lts.serorepl.utils.StringUtils;

import java.io.File;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static com.serotonin.mango.util.LoggingUtils.userInfo;
import static com.serotonin.mango.util.SendUtils.sendMsgTestSync;


public class SystemSettingsDwr extends BaseDwr {
	
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

		settings.put(SystemSettingsDAO.HTTP_RESPONSE_HEADERS, systemSettingsService.getHttpResponseHeaders());

		settings.put(SystemSettingsDAO.VIEW_HIDE_SHORTCUT_DISABLE_FULL_SCREEN,
				systemSettingsService.getMiscSettings().isViewHideShortcutDisableFullScreenEnabled());
		settings.put(SystemSettingsDAO.VIEW_FORCE_FULL_SCREEN_MODE,
				systemSettingsService.getMiscSettings().isViewForceFullScreenEnabled());
		settings.put(SystemSettingsDAO.EVENT_PENDING_LIMIT,
				systemSettingsService.getMiscSettings().getEventPendingLimit());
		settings.put(SystemSettingsDAO.EVENT_PENDING_CACHE_ENABLED,
				systemSettingsService.getMiscSettings().isEventPendingCacheEnabled());
		settings.put(SystemSettingsDAO.WORK_ITEMS_REPORTING_ENABLED,
				systemSettingsService.getMiscSettings().isWorkItemsReportingEnabled());
		settings.put(SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_ENABLED,
				systemSettingsService.getMiscSettings().isWorkItemsReportingItemsPerSecondEnabled());
		settings.put(SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_LIMIT,
				systemSettingsService.getMiscSettings().getWorkItemsReportingItemsPerSecondLimit());
		settings.put(SystemSettingsDAO.THREADS_NAME_ADDITIONAL_LENGTH,
				systemSettingsService.getMiscSettings().getThreadsNameAdditionalLength());
		settings.put(SystemSettingsDAO.WEB_RESOURCE_GRAPHICS_PATH,
				systemSettingsService.getMiscSettings().getWebResourceGraphicsPath());
		settings.put(SystemSettingsDAO.WEB_RESOURCE_UPLOADS_PATH,
				systemSettingsService.getMiscSettings().getWebResourceUploadsPath());
		settings.put(SystemSettingsDAO.EVENT_ASSIGN_ENABLED,
				systemSettingsService.getMiscSettings().isEventAssignEnabled());
		settings.put(SystemSettingsDAO.TOP_DESCRIPTION,
				systemSettingsService.getSystemInfoSettings().getTopDescription());
		settings.put(SystemSettingsDAO.TOP_DESCRIPTION_PREFIX,
				systemSettingsService.getSystemInfoSettings().getTopDescriptionPrefix());
		settings.put(SystemSettingsDAO.DATA_POINT_EXTENDED_NAME_LENGTH_IN_REPORTS_LIMIT,
				systemSettingsService.getMiscSettings().getDataPointExtendedNameLengthInReportsLimit());
		return settings;
	}

	
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
		MangoEvent eventService = new EventService();
		data.put("eventCount", eventService.getEventCount());

		return data;
	}

	
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
			IMsgSubjectContent cnt = IMsgSubjectContent.newInstance("testEmail", model,
					bundle, I18NUtils.getMessage(bundle, "ftl.testEmail"),
					Common.UTF8);
			sendMsgTestSync(user.getEmail(), cnt, result, () -> "sendTestEmail from: " + this.getClass().getName() +
					", " + userInfo(user));

		} catch (Exception e) {
			result.put("exception", e.getMessage());
		}
		return result;
	}

	
	public void saveSystemEventAlarmLevels(List<IntegerPair> eventAlarmLevels) {
		Permissions.ensureAdmin();
		for (IntegerPair eventAlarmLevel : eventAlarmLevels)
			SystemEventType.setEventTypeAlarmLevel(eventAlarmLevel.getI1(),
					eventAlarmLevel.getI2());
	}

	
	public void saveAuditEventAlarmLevels(List<IntegerPair> eventAlarmLevels) {
		Permissions.ensureAdmin();
		for (IntegerPair eventAlarmLevel : eventAlarmLevels)
			AuditEventType.setEventTypeAlarmLevel(eventAlarmLevel.getI1(),
					eventAlarmLevel.getI2());
	}

	
	public DwrResponseI18n saveHttpSettings(boolean useProxy, String host, int port,
			String username, String password, String httpStaticHeaders) {
		Permissions.ensureAdmin();

		JsonSettingsHttp jsonSettingsHttp = new JsonSettingsHttp();
		jsonSettingsHttp.setUseProxy(useProxy);
		jsonSettingsHttp.setHost(host);
		jsonSettingsHttp.setPort(port);
		jsonSettingsHttp.setUsername(username);
		jsonSettingsHttp.setPassword(password);
		jsonSettingsHttp.setHttpResponseHeaders(httpStaticHeaders);

		SystemSettingsService systemSettingsService = new SystemSettingsService();
		DwrResponseI18n response = new DwrResponseI18n();
		try {
			systemSettingsService.saveHttpSettings(jsonSettingsHttp);
		} catch (Exception ex) {
			response.addContextualMessage("httpMessage", "validate.invalidValue");
		}
		return response;
	}

	
	public DwrResponseI18n saveMiscSettings(int uiPerformance, String dataPointRtValueSynchronized,
											boolean viewEnableFullScreen, boolean viewHideShortcutDisableFullScreen,
											int eventPendingLimit, boolean eventPendingCacheEnabled,
											boolean workItemsReportingEnabled, boolean workItemsReportingItemsPerSecondEnabled,
											int workItemsReportingItemsPerSecondLimit, int threadsNameAdditionalLength,
											String webResourceGraphicsPath, String webResourceUploadsPath,
											boolean eventAssignEnabled, int pointExtendedNameLengthInReportsLimit) {
		Permissions.ensureAdmin();
		SystemSettingsDAO systemSettingsDAO = new SystemSettingsDAO();
		SystemSettingsService systemSettingsService = new SystemSettingsService();
        DwrResponseI18n response = new DwrResponseI18n();
        if(uiPerformance < 0) {
            response.addContextualMessage(SystemSettingsDAO.UI_PERFORMANCE, "validate.invalidValue");
            return response;
        } else {
            systemSettingsDAO.setIntValue(SystemSettingsDAO.UI_PERFORMANCE, uiPerformance);
        }
		systemSettingsDAO.setValue(SystemSettingsDAO.DATAPOINT_RUNTIME_VALUE_SYNCHRONIZED,
				String.valueOf(dataPointRtValueSynchronized));

		systemSettingsDAO.setBooleanValue(SystemSettingsDAO.VIEW_FORCE_FULL_SCREEN_MODE, viewEnableFullScreen);
		systemSettingsDAO.setBooleanValue(SystemSettingsDAO.VIEW_HIDE_SHORTCUT_DISABLE_FULL_SCREEN, viewHideShortcutDisableFullScreen);
		if(eventPendingLimit < 0) {
			response.addContextualMessage(SystemSettingsDAO.EVENT_PENDING_LIMIT, "validate.invalidValue");
		} else {
			systemSettingsDAO.setIntValue(SystemSettingsDAO.EVENT_PENDING_LIMIT, eventPendingLimit);
		}
		systemSettingsDAO.setBooleanValue(SystemSettingsDAO.EVENT_PENDING_CACHE_ENABLED, eventPendingCacheEnabled);
		if(eventPendingLimit < 0) {
			response.addContextualMessage(SystemSettingsDAO.THREADS_NAME_ADDITIONAL_LENGTH, "validate.invalidValue");
		} else {
			systemSettingsDAO.setIntValue(SystemSettingsDAO.THREADS_NAME_ADDITIONAL_LENGTH, threadsNameAdditionalLength);
		}
		systemSettingsDAO.setBooleanValue(SystemSettingsDAO.WORK_ITEMS_REPORTING_ENABLED, workItemsReportingEnabled);
		if(workItemsReportingEnabled) {
			systemSettingsDAO.setBooleanValue(SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_ENABLED, workItemsReportingItemsPerSecondEnabled);
			if(workItemsReportingItemsPerSecondEnabled) {
				if (workItemsReportingItemsPerSecondLimit < 0) {
					response.addContextualMessage(SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_LIMIT, "validate.invalidValue");
				} else {
					systemSettingsDAO.setIntValue(SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_LIMIT, workItemsReportingItemsPerSecondLimit);
				}
			} else {
				systemSettingsDAO.setIntValue(SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_LIMIT, 0);
			}
		} else {
			systemSettingsDAO.setBooleanValue(SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_ENABLED, false);
			systemSettingsDAO.setIntValue(SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_LIMIT, 0);
		}
		if (webResourceGraphicsPath != null && (StringUtils.isEmpty(webResourceGraphicsPath)
				|| (webResourceGraphicsPath.endsWith("graphics")
				|| webResourceGraphicsPath.endsWith("graphics" + File.separator)))) {
			systemSettingsDAO.setValue(SystemSettingsDAO.WEB_RESOURCE_GRAPHICS_PATH, webResourceGraphicsPath);
		} else {
			response.addContextualMessage(SystemSettingsDAO.WEB_RESOURCE_GRAPHICS_PATH, "systemsettings.webresource.graphics.path.wrong", File.separator);
		}
		if (webResourceUploadsPath != null && (StringUtils.isEmpty(webResourceUploadsPath)
				|| (webResourceUploadsPath.endsWith("uploads")
				|| webResourceUploadsPath.endsWith("uploads" + File.separator)))) {
			systemSettingsDAO.setValue(SystemSettingsDAO.WEB_RESOURCE_UPLOADS_PATH, webResourceUploadsPath);
		} else {
			response.addContextualMessage(SystemSettingsDAO.WEB_RESOURCE_UPLOADS_PATH, "systemsettings.webresource.uploads.path.wrong", File.separator);
		}
		if(pointExtendedNameLengthInReportsLimit < 0) {
			response.addContextualMessage(SystemSettingsDAO.DATA_POINT_EXTENDED_NAME_LENGTH_IN_REPORTS_LIMIT, "validate.invalidValue");
		} else {
			systemSettingsService.setDataPointExtendedNameLengthInReportsLimit(pointExtendedNameLengthInReportsLimit);
		}
		systemSettingsService.saveEventAssignEnabled(eventAssignEnabled);
		return response;
	}

	
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

	
	public void saveInfoSettings(String newVersionNotificationLevel,
			String instanceDescription,String topDescriptionPrefix, String topDescription) {
		Permissions.ensureAdmin();
		SystemSettingsDAO systemSettingsDAO = new SystemSettingsDAO();
		systemSettingsDAO.setValue(
				SystemSettingsDAO.NEW_VERSION_NOTIFICATION_LEVEL,
				newVersionNotificationLevel);
		systemSettingsDAO.setValue(SystemSettingsDAO.INSTANCE_DESCRIPTION,
				instanceDescription);
		systemSettingsDAO.setValue(SystemSettingsDAO.TOP_DESCRIPTION_PREFIX, topDescriptionPrefix);
		systemSettingsDAO.setValue(SystemSettingsDAO.TOP_DESCRIPTION, topDescription);
	}

	
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

	
	public void saveLanguageSettings(String language) {
		Permissions.ensureAdmin();
		ScadaLocaleUtils.setLocale(language);
	}

	
	public void purgeNow() {
		Permissions.ensureAdmin();
		DataPurge dataPurge = new DataPurge();
		dataPurge.execute(System.currentTimeMillis());
	}

	
	public LocalizableMessage purgeAllData() {
		Permissions.ensureAdmin();
		long cnt = Common.ctx.getRuntimeManager().purgeDataPointValues();
		return new LocalizableMessage("systemSettings.purgeDataComplete", cnt);
	}

	
	public void useDerbyDB() {
		Permissions.ensureAdmin();
		ConfigurationDB.useDerbyDB();
	}

	
	public void useMysqlDB() {
		Permissions.ensureAdmin();
		ConfigurationDB.useMysqlDB();
	}

	
	public void useMssqlDB() {
		Permissions.ensureAdmin();
		ConfigurationDB.useMssqlDB();
	}

	
	public String checkTypeDB() {
		return Common.getEnvironmentProfile().getString("db.type", "derby");
	}

	
	public String getAppServer() {
		return Common.ctx.getServletContext().getServerInfo();
	}
}
