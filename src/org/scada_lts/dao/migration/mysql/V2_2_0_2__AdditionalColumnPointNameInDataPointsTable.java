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
import com.serotonin.mango.vo.DataPointVO;
import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.DataPointDAO;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * additional column "pointName" in dataPoint table will contain ONLY
 * data point name which trigger "onlyForStorungsAndAlarmValues" on pointValues table needs
 *
 * @author  hyski mateusz@gmail.com on 26.05.2020
 */
public class V2_2_0_2__AdditionalColumnPointNameInDataPointsTable implements SpringJdbcMigration {

    public void migrate(JdbcTemplate jdbcTmp) throws Exception {

        try {


            //this additional column will contain ONLY data point name which trigger needs
            jdbcTmp.execute(
                    new AlterTable().AlterTableWithSpecification(
                            new StringBuilder("dataPoints"),
                            new StringBuilder("pointName"),
                            AlterTable.Fields.VARCHAR,
                            250,
                            -1,
                            true)
                    );
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
            //filling pointName column by proper data here because of it must be done from java flow,
            // on sql flow is not possible because data point name exist in blob column.
            //so update this column occurs here asap.

            //We can update of course column/property of datapoint at runtime of scada.
            //Solution for to do it by gui is implementation in other branche:

            // #1259 Storungs_And_Alarms_Filling_Additional_Column_PointName_During_AddUpdate_DataPoint <- filling
            // column pointName in dataPoints - by code - java flow/level

        try {
            for(DataPointVO dataPointVOS : new DataPointDAO().getDataPoints()){
                DAO.getInstance().getJdbcTemp().update("update dataPoints set "
                        + "pointName=? "
                        + "where "
                        + "id=? ", new Object[] {
                        dataPointVOS.getName(),
                        dataPointVOS.getId()
                });
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }
}
