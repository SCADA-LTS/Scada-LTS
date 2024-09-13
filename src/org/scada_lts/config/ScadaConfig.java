/*
 * (c) 2015 Abil'I.T. http://abilit.eu/
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.scada_lts.config;

import com.serotonin.mango.Common;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.SystemSettingsService;
import org.scada_lts.web.mvc.api.css.CssStyle;
import org.scada_lts.web.mvc.api.css.CssUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;


/** 
 * Class responsible for configuration scada LTS
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * person supporting and coreecting translation Jerzy Piejko
 */
public class ScadaConfig {
	
	/**
	 * Replace alert enabled (=true) or disabled (=false)
	 */
	public static final String REPLACE_ALERT_ON_VIEW = "abilit.api.replace.alert.onview";
	
	/**
	 * Event cache enabled (=true) or disabled (=false)
	 */
	public static final String ENABLE_CACHE = "abilit.cacheEnable";

	private Optional<Boolean> cacheEnabled = Optional.empty() ;
	
	/**
	 * Period update unsilenced alarm level but can be delayed when system heavy loaded.
	 */
	public static final String MILLIS_SECONDS_PERIOD_UPDATE_UNSILENCED_ALARM_LEVEL = "abilit.MILLIS_SECONDS_PERIOD_UPDATE_UNSILENCED_ALARM_LEVEL";

	private Optional<Long> millisSecondsPeriodUpdateUnsilencedAlarmLevel = Optional.empty();

	/**
	 * Start update unsilenced alarm level.
	 */
	public static final String START_UPDATE_UNSILENCED_ALARM_LEVEL = "abilit.START_UPDATE_UNSILENCED_ALARM_LEVEL";

	private Optional<Long> startUpdateUnsilencedAlarmLevel = Optional.empty();

	/**
	 * Period update event detectors but can be delayed when system heavy loaded.
	 */
	public static final String MILLIS_SECONDS_PERIOD_UPDATE_EVENT_DETECTORS = "abilit.MILLIS_SECONDS_PERIOD_UPDATE_EVENT_DETECTORS";

	private Optional<Long> milisSecondsPeriodUpdateEventDetectors = Optional.empty();
	
	/**
	 * Start update event detectors.
	 */
	public static final String START_UPDATE_EVENT_DETECTORS = "abilit.START_UPDATE_EVENT_DETECTORS";

	private Optional<Long> startUpdateEventDetectors = Optional.empty();
	
	/**
	 * Period update pending events but can be delayed when system heavy loaded.
	 */
	public static final String MILLIS_SECONDS_PERIOD_UPDATE_PENDING_EVENTS = "abilit.MILLIS_SECONDS_PERIOD_UPDATE_PENDING_EVENTS";

	private Optional<Long> milisSecondsPeriodUpdatePendingEvents = Optional.empty();

	
	/**
	 * Start update pending events.
	 */
	public static final String START_UPDATE_PENDING_EVENTS = "abilit.START_UPDATE_PENDING_EVENTS";

	private Optional<Long> startUpdatePendingEvents = Optional.empty();
	
	/**
	 * Period update point hierarchy but can be delayed when system heavy loaded. For example cron 0 15 1 ? * *.
	 */
	public static final String CRONE_UPDATE_CACHE_POINT_HIERARCHY = "abilit.CRONE_UPDATE_CACHE_POINT_HIERARCHY";

	private Optional<String> croneUpdateCachePointHierarchy = Optional.empty();

	/**
	* Period update data sources points. For example cron 0 15 1 ? * * the after start
	*/
 	public static final String CRONE_UPDATE_CACHE_DATA_SOURCES_POINTS = "abilit.CRONE_UPDATE_DATA_SOURCES_POINTS";

 	private Optional<String> croneUpdateCacheDataSourcePoints = Optional.empty();

	/**
    * Use Cache data sources points when the system is ready
    */
	public static final String USE_CACHE_DATA_SOURCES_POINTS_WHEN_THE_SYSTEM_IS_READY = "abilit.USE_CACHE_DATA_SOURCES_POINTS_WHEN_THE_SYSTEM_IS_READY";

	private Optional<Boolean> useCacheDataSourcesPointsWhenTheSystemIsReady = Optional.empty();

	public static final String USE_ACL = "abilit.USE_ACL";

	//TODO unfinished implementation and you have to remove it
	private Optional<Boolean> useACL = Optional.empty();

	//TODO unfinished implementation and you have to remove it
	public static final String ACL_SERVER = "abilit.ACL_SERVER";

	private Optional<String> aclServer = Optional.empty();

	//TODO replaced by the extension to the configuration of one of the datasources
	public static final String HTTP_RETRIVER_SLEEP_CHECK_TO_REACTIVATION_WHEN_START = "abilit.HTTP_RETRIVER_SLEEP_CHECK_TO_REACTIVATION_WHEN_START";

	//TODO replaced by the extension to the configuration of one of the datasources, to see why we use
	public static final String HTTP_RETRIVER_DO_NOT_ALLOW_ENABLE_REACTIVATION = "abilit.HTTP_RETRIVER_DO_NOT_ALLOW_ENABLE_REACTIVATION";

	private Optional<Boolean> httpRetriverDoNotAllowEnableReactivation = Optional.empty();

	public static final String OPTIMIZATION_LEVEL_JS = "js.optimizationlevel";

	private Optional<Integer> optimizationLevelJs = Optional.empty();

	public static final String DO_NOT_CREATE_EVENTS_FOR_EMAIL_ERROR = "abilit.DO_NOT_CREATE_EVENTS_FOR_EMAIL_ERROR";

	private Optional<Boolean> doNotCreateEventsForEmailError = Optional.empty();


	private static final Log LOG = LogFactory.getLog(ScadaConfig.class);
	private static final String FILE_NAME_LOGO="logo.png";
	private static final String FILE_NAME_PROPERTIES="env.properties";
	private static final String FILE_NAME_CUSTOM_CSS="common.css";
	private static final String DIR_NAME_CUSTOM_CONFIG="assets";
    private static final String FILE_NAME_USER_STYLESHEET="user_styles.css";
	
	private static ScadaConfig instance = null;
	
	private Properties conf;
	
	public static ScadaConfig getInstance() throws IOException {
		if (instance == null) {
			instance = new ScadaConfig();
		}
		return instance;
	}
	
	public static ScadaConfig getInstanceTest(Properties confTest) {
		instance = new ScadaConfig(confTest);
		return instance;
	}
	
	/**
	 *  Get configuration
	 * @return
	 */
	public Properties getConf() {
		return conf;
	}
	
	/**
	 * Get property value
	 * @param propertyName
	 * @return
	 */
	public String getProperty(String propertyName) {
		String result = "";
		try {
			if (CRONE_UPDATE_CACHE_POINT_HIERARCHY.equals(propertyName) && croneUpdateCachePointHierarchy.isPresent()) {
				return croneUpdateCachePointHierarchy.get();
			} else if (CRONE_UPDATE_CACHE_DATA_SOURCES_POINTS.equals(propertyName) && croneUpdateCacheDataSourcePoints.isPresent()) {
				return croneUpdateCacheDataSourcePoints.get();
			} else if (ACL_SERVER.equals(propertyName) && aclServer.isPresent()) {
				return aclServer.get();
			} else {
				result = conf.getProperty(propertyName);
				if (CRONE_UPDATE_CACHE_POINT_HIERARCHY.equals(propertyName)) {
					croneUpdateCachePointHierarchy = Optional.of(result);
				} else if (CRONE_UPDATE_CACHE_DATA_SOURCES_POINTS.equals(propertyName)) {
					croneUpdateCacheDataSourcePoints = Optional.of(result);
				} else if (ACL_SERVER.equals(propertyName)) {
					aclServer = Optional.of(result);
				}
			}
		} catch (Exception e) {
			LOG.trace("propertyName:"+propertyName +" e:"+e.getMessage());
		}
		return result;
	}
	
	/**
	 * Get property value of type boolean with default value
	 * @param propertyName
	 * @param defaultValue
	 * @return
	 */
	public Boolean getBoolean(String propertyName, boolean defaultValue) {
		Boolean result = Boolean.valueOf(defaultValue);
		try {
			if (ENABLE_CACHE.equals(propertyName) && cacheEnabled.isPresent()) {
				return cacheEnabled.get();
			} if (USE_CACHE_DATA_SOURCES_POINTS_WHEN_THE_SYSTEM_IS_READY.equals(propertyName) && useCacheDataSourcesPointsWhenTheSystemIsReady.isPresent()) {
				return useCacheDataSourcesPointsWhenTheSystemIsReady.get();
			} else if (USE_ACL.equals(propertyName) && useACL.isPresent()) {
				return useACL.get();
			} else if (HTTP_RETRIVER_DO_NOT_ALLOW_ENABLE_REACTIVATION.equals(propertyName) && httpRetriverDoNotAllowEnableReactivation.isPresent()) {
				return httpRetriverDoNotAllowEnableReactivation.get();
			} else if (DO_NOT_CREATE_EVENTS_FOR_EMAIL_ERROR.equals(propertyName) && doNotCreateEventsForEmailError.isPresent()) {
				return doNotCreateEventsForEmailError.get();
			} else {
				String propertyValue = getProperty(propertyName);
				result = (Boolean) Boolean.parseBoolean(propertyValue);
				LOG.trace("propertyName:" + propertyName + " value:" + result);
				if (ENABLE_CACHE.equals(propertyName)) {
					cacheEnabled = Optional.of(result);
				} else if (USE_CACHE_DATA_SOURCES_POINTS_WHEN_THE_SYSTEM_IS_READY.equals(propertyName)) {
					useCacheDataSourcesPointsWhenTheSystemIsReady = Optional.of(result);
				} else if (USE_ACL.equals(propertyName)) {
					useACL = Optional.of(result);
				} else if (HTTP_RETRIVER_DO_NOT_ALLOW_ENABLE_REACTIVATION.equals(propertyName)) {
					httpRetriverDoNotAllowEnableReactivation = Optional.of(result);
				} else if (DO_NOT_CREATE_EVENTS_FOR_EMAIL_ERROR.equals(propertyName)) {
					doNotCreateEventsForEmailError = Optional.of(result);
				}
			}
		} catch (Exception e) {
			LOG.trace("propertyName:"+propertyName+" value:"+defaultValue+" e:"+e.getMessage());
			result = defaultValue;
		}
		return result;
	}
	
	/**
	 * Get property value of type long with default value
	 * @param propertyName
	 * @param defaultValue
	 * @return
	 */
	public Long getLong(String propertyName, int defaultValue) {
		Long result = Long.valueOf(defaultValue);
		try {
			if (MILLIS_SECONDS_PERIOD_UPDATE_UNSILENCED_ALARM_LEVEL.equals(propertyName) && millisSecondsPeriodUpdateUnsilencedAlarmLevel.isPresent()) {
				return millisSecondsPeriodUpdateUnsilencedAlarmLevel.get();
			} else if (START_UPDATE_UNSILENCED_ALARM_LEVEL.equals(propertyName) && startUpdateUnsilencedAlarmLevel.isPresent() ) {
			    return startUpdateUnsilencedAlarmLevel.get();
			} else if (MILLIS_SECONDS_PERIOD_UPDATE_EVENT_DETECTORS.equals(propertyName) && milisSecondsPeriodUpdateEventDetectors.isPresent()) {
				return milisSecondsPeriodUpdateEventDetectors.get();
			} else if (START_UPDATE_EVENT_DETECTORS.equals(propertyName) && startUpdateEventDetectors.isPresent()) {
				return startUpdateEventDetectors.get();
			} else if (MILLIS_SECONDS_PERIOD_UPDATE_PENDING_EVENTS.equals(propertyName) && milisSecondsPeriodUpdatePendingEvents.isPresent()) {
				return milisSecondsPeriodUpdatePendingEvents.get();
			} else if (START_UPDATE_PENDING_EVENTS.equals(propertyName) && startUpdatePendingEvents.isPresent()) {
				return startUpdatePendingEvents.get();
			} else {
				String propertyValue = getProperty(propertyName);
				result = Long.parseLong(propertyValue);
				if(MILLIS_SECONDS_PERIOD_UPDATE_UNSILENCED_ALARM_LEVEL.equals(propertyName)) {
					millisSecondsPeriodUpdateUnsilencedAlarmLevel = Optional.of(result);
				} else if (START_UPDATE_UNSILENCED_ALARM_LEVEL.equals(propertyName)) {
					startUpdateUnsilencedAlarmLevel = Optional.of(result);
				} else if (MILLIS_SECONDS_PERIOD_UPDATE_EVENT_DETECTORS.equals(propertyName)) {
					milisSecondsPeriodUpdateEventDetectors = Optional.of(result);
				} else if (START_UPDATE_EVENT_DETECTORS.equals(propertyName)) {
					startUpdateEventDetectors = Optional.of(result);
				} else if (MILLIS_SECONDS_PERIOD_UPDATE_PENDING_EVENTS.equals(propertyName) ) {
					milisSecondsPeriodUpdatePendingEvents = Optional.of(result);
				} else if (START_UPDATE_PENDING_EVENTS.equals(propertyName) && startUpdatePendingEvents.isPresent()) {
					startUpdatePendingEvents = Optional.of(result);
				}
			}
		} catch (Exception e) {
			LOG.trace("propertyName:"+propertyName+" value:"+defaultValue+" e:"+e.getMessage());
			result = Long.valueOf(defaultValue);
		}
		return result;
	}

	public Integer getInt(String propertyName, int defaultValue) {
		Integer result = Integer.valueOf(defaultValue);
		try {
			if (OPTIMIZATION_LEVEL_JS.equals(propertyName) && optimizationLevelJs.isPresent()) {
				return optimizationLevelJs.get();
			}
			String propertyValue = getProperty(propertyName);
			result = Integer.parseInt(propertyValue);
			if (OPTIMIZATION_LEVEL_JS.equals(propertyName)) {
				optimizationLevelJs = Optional.of(result);
			}
		} catch (Exception e) {
			LOG.trace("propertyName:"+propertyName+" value:"+defaultValue+" e:"+e.getMessage());
			result = Integer.valueOf(defaultValue);
		}
		return result;
	}
	
	public static boolean isExistCustomLogo() {
		File f = new File(getPathCustomConfig()+FILE_NAME_LOGO);
		return (f.exists()) && (!f.isDirectory()); 
	}
	
	public static boolean isExistCustomCSS() {
		File f = new File(getPathCustomConfig()+FILE_NAME_CUSTOM_CSS);
		return (f.exists()) && (!f.isDirectory());
	}
	
	public static boolean isExistCustomEnvProperties() {
		File f = new File(getPathCustomConfig()+FILE_NAME_PROPERTIES);
		return (f.exists()) && (!f.isDirectory());
	}

    public static boolean isExistUserCSS() {
        File f = new File(getPathCustomConfig()+FILE_NAME_USER_STYLESHEET);
        return (f.exists()) && (!f.isDirectory());
    }
	
	public static void copyLogo() {
		try {
			Files.copy(Paths.get(getPathExistingLogo()), Paths.get(getPathCustomConfig()+FILE_NAME_LOGO));
		} catch (IOException e) {
			LOG.error(e);
		}
	}
	
	public static void copyCSS() {
		try {
			Files.copy(Paths.get(getPathExistingCSS()), Paths.get(getPathCustomConfig()+FILE_NAME_CUSTOM_CSS));
		} catch (IOException e) {
			LOG.error(e);
		}
	}
	
	public static void copyConfig() {
		try {
			Files.copy(Paths.get(getPathConfigFile("env.properties")), Paths.get(getPathCustomConfig()+FILE_NAME_PROPERTIES));
		} catch (IOException e) {
			LOG.error(e);
		}
	}

    public static void copyUserCSS() {
        SystemSettingsService systemSettingsService = new SystemSettingsService();
        CssStyle cssContent = systemSettingsService.getCustomCss();
        CssUtils.saveToFile(cssContent);
    }
	
	private ScadaConfig() {
		try {
		  conf = new Properties();
		  try(FileInputStream fis = new FileInputStream(getPathConfigFile("env.properties"))) {
			  conf.load(fis);
		  }
		} catch (Exception e) {
			LOG.error(e);
		}
	}
		
	private ScadaConfig(Properties confTest) {
		this.conf = confTest;
	}

	private static String getPathConfigFile() {
		String fileSeparator = System.getProperty("file.separator");
		String path = Common.ctx.getServletContext().getRealPath("");

		if (fileSeparator.equals("\\")) {
			path = path + "\\" + "WEB-INF" + "\\" + "classes" + "\\";
		}
		if (fileSeparator.equals("/")) {
			path = path + "/" + "WEB-INF" + "/" + "classes" + "/";
		}
		return path;
	}

	private static String getPathConfigFile(String fileName) {
		try {
			return URLDecoder.decode(ScadaConfig.class.getClassLoader().getResources(fileName).nextElement().getFile(), StandardCharsets.UTF_8);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return "";
		}
	}
	
	private static String getPathExistingLogo() {
		String fileSeparator = System.getProperty("file.separator");
		String path = Common.ctx.getServletContext().getRealPath("");

		if (fileSeparator.equals("\\")) {
			path = path + "\\builder\\assets\\images\\logos\\SCADA-LTS.png";
		}
		if (fileSeparator.equals("/")) {
			path = path + "/builder/assets/images/logos/SCADA-LTS.png";
		}
		return path;
	}
	
	private static String getPathExistingCSS() {
		String fileSeparator = System.getProperty("file.separator");
		String path = Common.ctx.getServletContext().getRealPath("");

		if (fileSeparator.equals("\\")) {
			path = path + "\\resources\\common.css";
		}
		if (fileSeparator.equals("/")) {
			path = path + "/resources/common.css";
		}
		return path;
	}
	
	private static String getPathCustomConfig() {
		
		String fileSeparator = System.getProperty("file.separator");
		String path = Common.ctx.getServletContext().getRealPath("");

		if (fileSeparator.equals("\\")) {
			path = path + "\\" + DIR_NAME_CUSTOM_CONFIG + "\\";
		}
		if (fileSeparator.equals("/")) {
			path = path + "/" + DIR_NAME_CUSTOM_CONFIG + "/";
		}
		return path;
		
	}

    private static String getPathExistingUserCSS() {
        String fileSeparator = System.getProperty("file.separator");
        String path = Common.ctx.getServletContext().getRealPath("");

        if (fileSeparator.equals("\\")) {
            path = path + "\\assets\\user_styles.css";
        }
        if (fileSeparator.equals("/")) {
            path = path + "/assets/user_styles.css";
        }
        return path;
    }
	
}
