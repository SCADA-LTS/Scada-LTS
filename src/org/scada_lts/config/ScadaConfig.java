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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.mango.Common;


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
	
	/**
	 * Period update unsilenced alarm level but can be delayed when system heavy loaded.
	 */
	public static final String MILLIS_SECONDS_PERIOD_UPDATE_UNSILENCED_ALARM_LEVEL = "abilit.MILLIS_SECONDS_PERIOD_UPDATE_UNSILENCED_ALARM_LEVEL";
	
	/**
	 * Start update unsilenced alarm level.
	 */
	public static final String START_UPDATE_UNSILENCED_ALARM_LEVEL = "abilit.START_UPDATE_UNSILENCED_ALARM_LEVEL";
	
	/**
	 * Period update event detectors but can be delayed when system heavy loaded.
	 */
	public static final String MILLIS_SECONDS_PERIOD_UPDATE_EVENT_DETECTORS = "abilit.MILLIS_SECONDS_PERIOD_UPDATE_EVENT_DETECTORS";
	
	/**
	 * Start update event detectors.
	 */
	public static final String START_UPDATE_EVENT_DETECTORS = "abilit.START_UPDATE_EVENT_DETECTORS";
	
	/**
	 * Period update pending events but can be delayed when system heavy loaded.
	 */
	public static final String MILLIS_SECONDS_PERIOD_UPDATE_PENDING_EVENTS = "abilit.MILLIS_SECONDS_PERIOD_UPDATE_PENDING_EVENTS";
	
	/**
	 * Start update pending events.
	 */
	public static final String START_UPDATE_PENDING_EVENTS = "abilit.START_UPDATE_PENDING_EVENTS";
	
	/**
	 * Period update point hierarchy but can be delayed when system heavy loaded. For example cron 0 15 1 ? * *.
	 */
	public static final String CRONE_UPDATE_CACHE_POINT_HIERARCHY = "abilit.CRONE_UPDATE_CACHE_POINT_HIERARCHY";
	
	private static final Log LOG = LogFactory.getLog(ScadaConfig.class);
	private static final String FILE_NAME_LOGO="logo.png";
	private static final String FILE_NAME_PROPERTIES="env.properties";
	private static final String FILE_NAME_CUSTOM_CSS="common.css";
	private static final String DIR_NAME_CUSTOM_CONFIG="assets";
	
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
		return conf.getProperty(propertyName);
	}
	
	/**
	 * Get property value of type boolean with default value
	 * @param propertyName
	 * @param defaultValues
	 * @return
	 */
	public Boolean getBoolean(String propertyName, boolean defaultValues) {
		Boolean result = defaultValues;
		try {
			String propertyValue = getProperty(propertyName);
			result = Boolean.parseBoolean(propertyValue);
			LOG.trace("propertyName:"+propertyName+" value:"+result);
		} catch (Exception e) {
			LOG.trace("propertyName:"+propertyName+" value:"+defaultValues);
			result = defaultValues;
		}
		return result;
	}
	
	/**
	 * Get property value of type long with default value
	 * @param propertyName
	 * @param defaultValues
	 * @return
	 */
	public Long getLong(String propertyName, int defaultValues) {
		Long result = new Long(defaultValues);
		try {
			String propertyValue = getProperty(propertyName);
			result = Long.parseLong(propertyValue);
		} catch (Exception e) {
			result = new Long(defaultValues);
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
			Files.copy(Paths.get(getPathConfigFile() + System.getProperty("file.separator") + "env.properties"), Paths.get(getPathCustomConfig()+FILE_NAME_PROPERTIES));
		} catch (IOException e) {
			LOG.error(e);
		}
	}
	
	private ScadaConfig() {
		try {
		  conf = new Properties();
		  FileInputStream fis = null;
		  fis = new FileInputStream(getPathConfigFile() + System.getProperty("file.separator") + "env.properties");
		  conf.load(fis);
		} catch (IOException e) {
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

	
}
