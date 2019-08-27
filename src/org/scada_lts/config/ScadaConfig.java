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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
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
	@Deprecated
	public static final String REPLACE_ALERT_ON_VIEW = "abilit.api.replace.alert.onview";

	/**
	 * Event cache enabled (=true) or disabled (=false)
	 */
	@Deprecated
	public static final String ENABLE_CACHE = "abilit.cacheEnable";

	/**
	 * Period update unsilenced alarm level but can be delayed when system heavy loaded.
	 */
	@Deprecated
	public static final String MILLIS_SECONDS_PERIOD_UPDATE_UNSILENCED_ALARM_LEVEL = "abilit.MILLIS_SECONDS_PERIOD_UPDATE_UNSILENCED_ALARM_LEVEL";

	/**
	 * Start update unsilenced alarm level.
	 */
	@Deprecated
	public static final String START_UPDATE_UNSILENCED_ALARM_LEVEL = "abilit.START_UPDATE_UNSILENCED_ALARM_LEVEL";

	/**
	 * Period update event detectors but can be delayed when system heavy loaded.
	 */
	@Deprecated
	public static final String MILLIS_SECONDS_PERIOD_UPDATE_EVENT_DETECTORS = "abilit.MILLIS_SECONDS_PERIOD_UPDATE_EVENT_DETECTORS";

	/**
	 * Start update event detectors.
	 */
	@Deprecated
	public static final String START_UPDATE_EVENT_DETECTORS = "abilit.START_UPDATE_EVENT_DETECTORS";

	/**
	 * Period update pending events but can be delayed when system heavy loaded.
	 */
	@Deprecated
	public static final String MILLIS_SECONDS_PERIOD_UPDATE_PENDING_EVENTS = "abilit.MILLIS_SECONDS_PERIOD_UPDATE_PENDING_EVENTS";

	/**
	 * Start update pending events.
	 */
	@Deprecated
	public static final String START_UPDATE_PENDING_EVENTS = "abilit.START_UPDATE_PENDING_EVENTS";

	/**
	 * Period update point hierarchy but can be delayed when system heavy loaded. For example cron 0 15 1 ? * *.
	 */
	@Deprecated
	public static final String CRONE_UPDATE_CACHE_POINT_HIERARCHY = "abilit.CRONE_UPDATE_CACHE_POINT_HIERARCHY";

	/**
	* Period update data sources points. For example cron 0 15 1 ? * * the after start
	*/
	@Deprecated
 	public static final String CRONE_UPDATE_CACHE_DATA_SOURCES_POINTS = "abilit.CRONE_UPDATE_DATA_SOURCES_POINTS";

	/**
    * Use Cache data sources points when the system is ready
    */
	@Deprecated
	public static final String USE_CACHE_DATA_SOURCES_POINTS_WHEN_THE_SYSTEM_IS_READY = "abilit.USE_CACHE_DATA_SOURCES_POINTS_WHEN_THE_SYSTEM_IS_READY";

	@Deprecated
	public static final String USE_ACL = "abilit.USE_ACL";

	@Deprecated
	public static final String ACL_SERVER = "abilit.ACL_SERVER";

	@Deprecated
	public static final String HTTP_RETRIVER_SLEEP_CHECK_TO_REACTIVATION_WHEN_START = "abilit.HTTP_RETRIVER_SLEEP_CHECK_TO_REACTIVATION_WHEN_START";

	@Deprecated
	public static final String HTTP_RETRIVER_DO_NOT_ALLOW_ENABLE_REACTIVATION = "abilit.HTTP_RETRIVER_DO_NOT_ALLOW_ENABLE_REACTIVATION";

	private static final Log LOG = LogFactory.getLog(ScadaConfig.class);
	private static final String FILE_NAME_LOGO="logo.png";
	private static final String FILE_NAME_PROPERTIES="env.properties";
	private static final String FILE_NAME_CUSTOM_CSS="common.css";
	private static final String DIR_NAME_CUSTOM_CONFIG="assets";
	
	private static ScadaConfig instance = null;
	
	private Properties config;

    private ScadaConfig(File fileProperties) {
        _init(fileProperties);
    }

    private ScadaConfig(Properties confTest) {
		this.config = confTest;
	}

	@Deprecated
	public static ScadaConfig getInstance() throws IOException {
        return getConfig();
	}

	public static ScadaConfig getConfig() {
        return _doubleCheckedLocking(_envPropertiesFile());
	}

	public static ScadaConfig getConfigOnlyTest(File fileProperties) {
        return _doubleCheckedLocking(fileProperties);
    }

    public static ScadaConfig getInstanceTest(Properties confTest) {
		instance = new ScadaConfig(confTest);
		return instance;
	}

    public int size() {
        return config.size();
    }
	
	/**
	 * Get property value
	 * @param propertyName
	 * @return
	 */
	@Deprecated
	public String getProperty(String propertyName) {
		return config.getProperty(propertyName);
	}

	@Deprecated
	public String getString(String propertyName, String defaultValue) {
		return _getString(propertyName, defaultValue);
	}

	public String getString(ScadaConfigKey scadaConfigKey, String defaultValue) {
		return _getString(scadaConfigKey.getKey(), defaultValue);
	}

	/**
	 * Get property value of type boolean with default value
	 * @param propertyName
	 * @param defaultValue
	 * @return
	 */
	@Deprecated
	public boolean getBoolean(String propertyName, boolean defaultValue) {
		return _getBoolean(propertyName, defaultValue);
	}

	public boolean getBoolean(ScadaConfigKey scadaConfigKey, boolean defaultValue) {
		return _getBoolean(scadaConfigKey.getKey(), defaultValue);
	}
	
	/**
	 * Get property value of type long with default value
	 * @param propertyName
	 * @param defaultValues
	 * @return
	 */
	@Deprecated
	public long getLong(String propertyName, long defaultValues) {
		return _getLong(propertyName, defaultValues);
	}

	public long getLong(ScadaConfigKey scadaConfigKey, long defaultValue) {
		return _getLong(scadaConfigKey.getKey(), defaultValue);
	}

	@Deprecated
	public int getInt(String propertyName, int defaultValue) {
		return _getInt(propertyName, defaultValue);
	}

	public int getInt(ScadaConfigKey scadaConfigKey, int defaultValue) {
		return _getInt(scadaConfigKey.getKey(), defaultValue);
	}

	public static boolean isExistCustomLogo() {
		File f = new File(_getPathCustomConfig()+FILE_NAME_LOGO);
		return (f.exists()) && (!f.isDirectory()); 
	}
	
	public static boolean isExistCustomCSS() {
		File f = new File(_getPathCustomConfig()+FILE_NAME_CUSTOM_CSS);
		return (f.exists()) && (!f.isDirectory());
	}
	
	public static boolean isExistCustomEnvProperties() {
		File f = new File(_getPathCustomConfig()+FILE_NAME_PROPERTIES);
		return (f.exists()) && (!f.isDirectory());
	}
	
	public static void copyLogo() {
		try {
			Files.copy(Paths.get(_getPathExistingLogo()), Paths.get(_getPathCustomConfig()+FILE_NAME_LOGO));
		} catch (IOException e) {
			LOG.error(e);
		}
	}
	
	public static void copyCSS() {
		try {
			Files.copy(Paths.get(_getPathExistingCSS()), Paths.get(_getPathCustomConfig()+FILE_NAME_CUSTOM_CSS));
		} catch (IOException e) {
			LOG.error(e);
		}
	}
	
	public static void copyConfig() {
		try {
			Files.copy(Paths.get(_getPathConfigFile() + System.getProperty("file.separator") + "env.properties"), Paths.get(_getPathCustomConfig()+FILE_NAME_PROPERTIES));
		} catch (IOException e) {
			LOG.error(e);
		}
	}

	public boolean isEmpty() {
		return config == null || config.isEmpty();
	}

	private static String _getPathConfigFile() {
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
	
	private static String _getPathExistingLogo() {
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
	
	private static String _getPathExistingCSS() {
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
	
	private static String _getPathCustomConfig() {
		
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

	private String _getString(String propertyName, String defaultValue) {
		try{
			return config.getProperty(propertyName, defaultValue);
		} catch (Throwable e) {
			LOG.error(e);
			return defaultValue;
		}
	}

	private int _getInt(String propertyName, int defaultValue) {
		try {
			String propertyValue = _getString(propertyName, String.valueOf(defaultValue));
			return Integer.parseInt(propertyValue);
		} catch (NumberFormatException e) {
			LOG.error(e);
			return defaultValue;
		}
	}

	private long _getLong(String propertyName, long defaultValue) {
		try {
			String propertyValue = _getString(propertyName, String.valueOf(defaultValue));
			return Long.parseLong(propertyValue);
		} catch (Throwable e) {
			LOG.error(e);
			return defaultValue;
		}
	}

	private boolean _getBoolean(String propertyName, boolean defaultValue) {
		try {
			String propertyValue = _getString(propertyName, String.valueOf(defaultValue));
			boolean result = Boolean.valueOf(propertyValue);
			LOG.trace("propertyName:"+propertyName+" value:"+result);
			return result;
		} catch (Throwable e) {
			LOG.trace("propertyName:"+propertyName+" value:"+defaultValue);
			LOG.error(e);
			return defaultValue;
		}
	}

    private void _init(File fileProperties) {
        config = new Properties();
        try(InputStream is = _newInputStream(fileProperties)
                .orElse(null)) {
            if(is == null)
                throw new IOException("File not found.");
            config.load(is);
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    private Optional<InputStream> _newInputStream(File fileProperties) {
        try {
            return Optional.of(new FileInputStream(fileProperties));
        } catch (Throwable e) {
            LOG.error(e);
            return _getSystemResourceAsStream(fileProperties);
        }
    }

    private Optional<InputStream> _getSystemResourceAsStream(File fileProperties) {
        try {
            return Optional.ofNullable(ClassLoader
                    .getSystemResourceAsStream(fileProperties.getPath()));
        } catch (Throwable e) {
            LOG.error(e);
            return Optional.empty();
        }
    }

    private static File _envPropertiesFile() {
        String path = MessageFormat.format("{0}{1}env.properties", _getPathConfigFile(), File.separator);
        return new File(path);
    }

    private static ScadaConfig _doubleCheckedLocking(File fileProperties) {
        if (instance == null) {
            synchronized (ScadaConfig.class) {
                if (instance == null)
                    instance = new ScadaConfig(fileProperties);
            }
        }
        return instance;
    }
}
