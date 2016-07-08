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
package com.serotonin.mango.db.upgrade;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.mango.Common;
import com.serotonin.mango.db.DatabaseAccess;
import com.serotonin.mango.db.dao.BaseDao;
import com.serotonin.mango.db.dao.SystemSettingsDao;
import com.serotonin.util.StringUtils;

/**
 * Base class for instances that perform database upgrades. The naming of
 * subclasses follows the convention 'Upgrade[maj]_[min]_[mic]', where
 * '[maj]_[min]_[mic]' is the version that the class upgrades from. The subclass
 * must be in this package.
 * 
 * @author Matthew Lohbihler
 */
abstract public class DBUpgrade extends BaseDao {
	private static final Log LOG = LogFactory.getLog(DBUpgrade.class);
	protected static final String DEFAULT_DATABASE_TYPE = "*";

	public static void checkUpgrade() {
		// If this is a very old version of the system, there may be multiple
		// upgrades to run, so start a loop.
		while (true) {
			// Get the current schema version.
			String schemaVersion = SystemSettingsDao
					.getValue(SystemSettingsDao.DATABASE_SCHEMA_VERSION);

			// Convert the schema version to the class name convention. This
			// simply means replacing dots with
			// underscores and prefixing 'Upgrade' and this package.
			String upgradeClassname = DBUpgrade.class.getPackage().getName()
					+ ".Upgrade" + schemaVersion.replace('.', '_');

			// See if there is a class with this name.
			Class<?> clazz = null;
			DBUpgrade upgrade = null;
			try {
				clazz = Class.forName(upgradeClassname);
			} catch (ClassNotFoundException e) {
				// no op
			}

			if (clazz != null) {
				try {
					upgrade = (DBUpgrade) clazz.newInstance();
				} catch (Exception e) {
					// Should never happen so wrap in a runtime and rethrow.
					throw new ShouldNeverHappenException(e);
				}
			}

			if (upgrade == null) {
				LOG.info("Starting instance with version " + schemaVersion);
				break;
			}

			try {
				LOG.warn("Upgrading instance from " + schemaVersion + " to "
						+ upgrade.getNewSchemaVersion());
				upgrade.upgrade();
				new SystemSettingsDao().setValue(
						SystemSettingsDao.DATABASE_SCHEMA_VERSION,
						upgrade.getNewSchemaVersion());
			} catch (Exception e) {
				throw new ShouldNeverHappenException(e);
			}
		}
	}

	abstract protected void upgrade() throws Exception;

	abstract protected String getNewSchemaVersion();

	/**
	 * Convenience method for subclasses
	 * 
	 * @param script
	 *            the array of script lines to run
	 * @param out
	 *            the stream to which to direct output from running the script
	 * @throws Exception
	 *             if something bad happens
	 */
	protected void runScript(String[] script, OutputStream out)
			throws Exception {
		Common.ctx.getDatabaseAccess().runScript(script, out);
	}

	protected void runScript(Map<String, String[]> scripts,
			final OutputStream out) throws Exception {
		DatabaseAccess da = Common.ctx.getDatabaseAccess();
		String[] script = scripts.get(da.getType().name());
		if (script == null)
			script = scripts.get(DEFAULT_DATABASE_TYPE);
		runScript(script, out);
	}

	protected OutputStream createUpdateLogOutputStream(String version) {
		String dir = Common.getEnvironmentProfile().getString(
				"db.update.log.dir", "");
		dir = StringUtils.replaceMacros(dir, System.getProperties());

		File logDir = new File(dir);
		File logFile = new File(logDir, "Update" + version + ".log");
		LOG.info("Writing upgrade log to " + logFile.getAbsolutePath());

		try {
			if (logDir.isDirectory() && logDir.canWrite())
				return new FileOutputStream(logFile);
		} catch (Exception e) {
			LOG.error("Failed to create database upgrade log file.", e);
		}

		LOG.warn("Failing over to console for printing database upgrade messages");
		return System.out;
	}

	public static boolean isUpgradeNeeded(String schemaVersion) {
		String upgradeClassname = DBUpgrade.class.getPackage().getName()
				+ ".Upgrade" + schemaVersion.replace('.', '_');

		Class<?> clazz = null;
		try {
			clazz = Class.forName(upgradeClassname);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
}
