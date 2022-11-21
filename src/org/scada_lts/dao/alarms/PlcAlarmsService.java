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

import com.serotonin.mango.vo.DataPointVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.DataPointService;

import java.util.List;

/**
 * Create by at Mateusz Hyski
 *
 * @author hyski.mateusz@gmail.com
 * @update kamil.jarmusik@gmail.com
 *
 */

class PlcAlarmsService implements AlarmsService {

    private static final Log LOG = LogFactory.getLog(AlarmsService.class);

    private final AlarmsDAO alarmsDAO;
    private DataPointService dataPointService = new DataPointService();

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
        List<LiveAlarm> liveAlarms = alarmsDAO.getLiveAlarms(offset, limit);
        for (int i=0; i< liveAlarms.size(); i++) {
            try {
                DataPointVO dataPointVO = dataPointService.getDataPoint(liveAlarms.get(i).getDataPointId());
                liveAlarms.get(i).setDescription(dataPointVO.getDescription());
                boolean value = (liveAlarms.get(i).getInactivationTime().trim().equals(""));
                String eventTextRender = dataPointVO.getEventTextRenderer().getText(value);
                liveAlarms.get(i).setEventTextRender(eventTextRender);
            } catch (Exception e) {
                LOG.error(new Exception("Problem with alarms live id"+liveAlarms.get(i).toString()+" problem:"+e.getMessage()));
            }
        }
        return liveAlarms;
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
