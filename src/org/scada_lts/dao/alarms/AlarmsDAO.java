/*
 * (c) 2020 hyski.mateusz@gmail.com, kamil.jarmusik@gmail.com
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

package org.scada_lts.dao.alarms;

import org.scada_lts.dao.DAO;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Optional;

/**
 * Create by at Mateusz Hyski
 *
 * @author hyski.mateusz@gmail.com
 * @update kamil.jarmusik@gmail.com
 *
 */

public interface AlarmsDAO {

    List<LiveAlarm> getLiveAlarms(int offset, int limit) throws DataAccessException;
    List<HistoryAlarm> getHistoryAlarms(String dayDate, String regex, int offset, int limit) throws DataAccessException;
    boolean setAcknowledgeTime(int id) throws DataAccessException;
    Optional<Long> getInactiveTimeMs(int id) throws DataAccessException;

    static AlarmsDAO getInstance() {
        return new PlcAlarmsDAO();
    }
}
