/*
 * (c) 2016 Abil'I.T. http://abilit.eu/
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
package org.scada_lts.dao;

import org.junit.Test;
import org.scada_lts.dao.impl.DAO;
import org.scada_lts.dao.report.ReportInstanceDAO;

import static org.junit.Assert.assertTrue;

/**
 * Test SystemSettingsDAO
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class SystemSettingsDaoTest extends TestDAO {

	private static final SystemSettingsDAO systemSettingsDAO = new SystemSettingsDAO();

	private static final String SETTING_NAME = "settingName";
	private static final String SETTING_VALUE = "/ScadaLTS";
	private static final String DEFAULT_VALUE = "defaultValue";
	private static final String INCORRECT_SETTING_NAME = "systemEventAlarmLevel1";

	@Test
	public void valueTest() {
		//Add value
		systemSettingsDAO.setValue(SETTING_NAME, SETTING_VALUE);

		//Get value
		String result = SystemSettingsDAO.getValue(SETTING_NAME, null);
		assertTrue(result.equals(SETTING_VALUE));

		result = SystemSettingsDAO.getValue(SETTING_NAME, DEFAULT_VALUE);
		assertTrue(result.equals(SETTING_VALUE));

		result = SystemSettingsDAO.getValue(INCORRECT_SETTING_NAME, null);
		assertTrue(result == null);

		result = SystemSettingsDAO.getValue(INCORRECT_SETTING_NAME, DEFAULT_VALUE);
		assertTrue(result.equals(DEFAULT_VALUE));

		//Delete value
		systemSettingsDAO.removeValue(SETTING_NAME);
		result = SystemSettingsDAO.getValue(SETTING_NAME, null);
		assertTrue(result == null);

		result = SystemSettingsDAO.getValue(SETTING_NAME, DEFAULT_VALUE);
		assertTrue(result.equals(DEFAULT_VALUE));
	}

	@Test
	public void databaseTest() {

		double emptySize = systemSettingsDAO.getDataBaseSize();
		assertTrue(emptySize > 0);

		DAO.getInstance().getJdbcTemp().update("INSERT INTO datasources (`xid`,`name`,`dataSourceType`,`data`) "
				+ "VALUES ('DS_01','DS_TEST1', 1,'')");
		DAO.getInstance().getJdbcTemp().update("INSERT INTO datasources (`xid`,`name`,`dataSourceType`,`data`) "
				+ "VALUES ('DS_02','DS_TEST2', 2,'')");
		DAO.getInstance().getJdbcTemp().update("INSERT INTO datasources (`xid`,`name`,`dataSourceType`,`data`) "
				+ "VALUES ('DS_03','DS_TEST3', 3,'')");
		DAO.getInstance().getJdbcTemp().update("INSERT INTO datasources (`xid`,`name`,`dataSourceType`,`data`) "
				+ "VALUES ('DS_04','DS_TEST4', 4,'')");
		DAO.getInstance().getJdbcTemp().update("INSERT INTO datasources (`xid`,`name`,`dataSourceType`,`data`) "
				+ "VALUES ('DS_05','DS_TEST5', 5,'')");
		DAO.getInstance().getJdbcTemp().update("INSERT INTO datasources (`xid`,`name`,`dataSourceType`,`data`) "
				+ "VALUES ('DS_06','DS_TEST6', 6,'')");
		DAO.getInstance().getJdbcTemp().update("INSERT INTO datasources (`xid`,`name`,`dataSourceType`,`data`) "
				+ "VALUES ('DS_07','DS_TEST7', 7,'')");
		DAO.getInstance().getJdbcTemp().update("INSERT INTO datasources (`xid`,`name`,`dataSourceType`,`data`) "
				+ "VALUES ('DS_08','DS_TEST8', 8,'')");
		DAO.getInstance().getJdbcTemp().update("INSERT INTO datasources (`xid`,`name`,`dataSourceType`,`data`) "
				+ "VALUES ('DS_09','DS_TEST9', 9,'')");
		DAO.getInstance().getJdbcTemp().update("INSERT INTO datasources (`xid`,`name`,`dataSourceType`,`data`) "
				+ "VALUES ('DS_10','DS_TEST10', 10,'')");
		DAO.getInstance().getJdbcTemp().update("INSERT INTO datasources (`xid`,`name`,`dataSourceType`,`data`) "
				+ "VALUES ('DS_11','DS_TEST11', 11,'')");
		DAO.getInstance().getJdbcTemp().update("INSERT INTO datasources (`xid`,`name`,`dataSourceType`,`data`) "
				+ "VALUES ('DS_12','DS_TEST12', 12,'')");
		DAO.getInstance().getJdbcTemp().update("INSERT INTO datasources (`xid`,`name`,`dataSourceType`,`data`) "
				+ "VALUES ('DS_13','DS_TEST13', 13,'')");
		DAO.getInstance().getJdbcTemp().update("INSERT INTO datasources (`xid`,`name`,`dataSourceType`,`data`) "
				+ "VALUES ('DS_14','DS_TEST14', 14,'')");
		DAO.getInstance().getJdbcTemp().update("INSERT INTO datasources (`xid`,`name`,`dataSourceType`,`data`) "
				+ "VALUES ('DS_15','DS_TEST15', 15,'')");

		DAO.getInstance().getJdbcTemp().update("INSERT INTO datapoints (`xid`,`dataSourceId`,`data`) "
				+ "VALUES ('T_01',1,'')");
		DAO.getInstance().getJdbcTemp().update("INSERT INTO pointvalues (dataPointId, dataType, pointValue, ts) "
				+ "VALUES(1,1,2.0,123)");
		DAO.getInstance().getJdbcTemp().update("INSERT INTO users (`username`, `password`, `email`, `phone`, `admin`, `disabled`, `receiveAlarmEmails`, `receiveOwnAuditEvents`) VALUES ('uUsername', 'uPassword', 'uEmail', '24656789', '0', '0', '2', '0');");
		DAO.getInstance().getJdbcTemp().update("INSERT INTO reportInstances "
				+ "(userId, name, includeEvents, includeUserComments, reportStartTime, reportEndTime, runStartTime, "
				+ "runEndTime, recordCount, preventPurge) "
				+ "VALUES (1, 'fName', 1, false, 2, 542145, 321, 123456, 32, true)");

		double sizeWithRecords = systemSettingsDAO.getDataBaseSize();
		assertTrue(sizeWithRecords > 0);
		assertTrue(sizeWithRecords >= emptySize);


		ReportInstanceDAO reportInstanceDAO = new ReportInstanceDAO();
		reportInstanceDAO.getReportInstance(1);

		systemSettingsDAO.resetDataBase();

		final int ID_FOR_CHECK_RESULT = 2;
		assertTrue(reportInstanceDAO.getReportInstance(ID_FOR_CHECK_RESULT) == null);
	}
}
