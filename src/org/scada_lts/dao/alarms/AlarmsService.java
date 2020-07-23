package org.scada_lts.dao.alarms;

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

import org.scada_lts.dao.alarms.AcknowledgeResponse;
import org.scada_lts.dao.alarms.ApiAlarmsHistory;
import org.scada_lts.dao.alarms.ApiAlarmsLive;

import java.util.List;

/**
 * Create by at Mateusz Hyski
 *
 * @author hyski.mateusz@gmail.com
 */
public interface AlarmsService {

    List<ApiAlarmsLive> getLiveAlarms(int offset, int limit);

    AcknowledgeResponse acknowledge(int id);

    List<ApiAlarmsHistory> getHistoryAlarms(String dayDate, String dataPointNameFilter, int offset, int limit );

}
