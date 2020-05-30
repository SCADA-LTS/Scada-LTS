package org.scada_lts.dao.migration.mysql;
/*
 * (c) 2020 hyski.mateusz@gmail.com
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
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.vo.DataPointVO;
import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.scada_lts.dao.DataPointDAO;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * This additional column called plcAlarmLevel we need to discover with what level of
 * data point we have in meaning AL-1 as Alarms and ST-2 Storungs/Warnings. This property plcAlarmLevel is used
 * in trigger which appear on pointValues table.
 *
 * @author  hyski mateusz@gmail.com on 27.04.2020
 */
public class V2_2_0__AdditionalColumnPlcAlarmLevelInDataPointsTable implements SpringJdbcMigration {

    public void migrate(JdbcTemplate jdbcTmp) throws Exception {

        try {
            //this additional column will have defined level of alarm as a 0-8 steps.
            jdbcTmp.execute(
                    new AlterTable().AlterTableWithSpecification(
                            new StringBuilder("dataPoints"),
                            new StringBuilder("plcAlarmLevel"),
                            AlterTable.Fields.TINYINT,
                            8,
                            -1,
                            true)
                    );
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
            //that step updates plcAlarmLevel column depending on data point name asap
            //this step is used only once

            //we can update of course plcAlarmLevel and pointName by add or update data point properties at runtime
            //to do it is another solution-implementation in another branche

            // #1259 Storungs_And_Alarms_Filling_Additional_Column_PointName_During_AddUpdate_DataPoint <- filling column pointName in dataPoints - by code - java flow/level , and
            // #1238 Storungs_And_Alarms_Validator_For_New_NamesSyntaxDataPoints <- update dataPoint table - plcAlarmLevel by update dataPoint Name
            // These above branches work on java and gui level - during typical scada work.


            //that's safer way because all dataPoint properties are collected in blob in database and an operation,
            // to, only get datapoint name from blob on sql level(procedure etc), is expensive (execute sql work) but ,also,
            // from sql level, get this property from blob (unfortunately that's a binary context) is impossible.
            //From an architecture , properties of data point in present blob field is safer.
        try {
            for(DataPointVO dataPointVO:new DataPointDAO().getDataPoints()) {
                jdbcTmp.execute("update dataPoints set plcAlarmLevel="
                        + (dataPointVO.getName().contains(" AL ")
                        ? 1 :
                        dataPointVO.getName().contains(" ST ")
                        ? 2 : 0) + " where id=" + dataPointVO.getId());
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }
}
