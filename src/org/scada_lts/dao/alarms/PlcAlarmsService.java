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

import java.util.List;

/**
 * Create by at Mateusz Hyski
 *
 * @author hyski.mateusz@gmail.com
 * @update kamil.jarmusik@gmail.com
 *
 */

class PlcAlarmsService implements AlarmsService {

    private final AlarmsDAO alarmsDAO;

    public PlcAlarmsService(AlarmsDAO alarmsDAO) {
        this.alarmsDAO = alarmsDAO;
    }

    @Override
    public List<HistoryAlarm> getHistoryAlarms(String dayDate, String dataPointNameFilter, int offset, int limit) {
        String regex = dataPointNameFilter == null || "EMPTY".equals(dataPointNameFilter) ? "(.*)" : dataPointNameFilter;
        return alarmsDAO.getHistoryAlarms(dayDate, regex, offset, limit);
    }

    @Override
    public List<LiveAlarm> getLiveAlarms(int offset, int limit) {
        return alarmsDAO.getLiveAlarms(offset, limit);
    }

    @Override
    public AlarmAcknowledge acknowledge(int id) {
        long inactiveTime = alarmsDAO.getInactiveTimeMs(id).orElse(-1L);
        if(inactiveTime == -1L)
            return createAcknowledgeResponse("Unknow error.", false, id);
        if(inactiveTime == 0L)
            return createAcknowledgeResponse("Alarm or Fault is active!", false, id);
        boolean result = alarmsDAO.setAcknowledgeTime(id);
        return createAcknowledgeResponse("", result, id);
    }

    /**
     * method use as a helper to build a AlarmAcknowledge by values when
     * error occur on DataBase level or another
     * or if everything is OK
     *
     * @param errorMessage
     * @param result
     * @param id
     * @return
     */
    private AlarmAcknowledge createAcknowledgeResponse(String errorMessage, boolean result, int id) {
        return result ? AlarmAcknowledge.requestOk(id) : AlarmAcknowledge.requestFault(id, errorMessage);
    }
}
