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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;

import java.util.Collections;
import java.util.List;

/**
 * Create by at Mateusz Hyski
 *
 * @author hyski.mateusz@gmail.com
 * @update kamil.jarmusik@gmail.com
 *
 */

class PlcAlarmsService implements AlarmsService {

    private static final Log LOG = LogFactory.getLog(PlcAlarmsService.class);

    private final PlcAlarmsDAO plcAlarmsDAO;

    public PlcAlarmsService(PlcAlarmsDAO plcAlarmsDAO) {
        this.plcAlarmsDAO = plcAlarmsDAO;
    }

    @Override
    public List<HistoryAlarm> getHistoryAlarms(String dayDate, String dataPointNameFilter, int offset, int limit) {
        try
        {
            String regex = dataPointNameFilter == null || "EMPTY".equals(dataPointNameFilter) ? "(.*)" : dataPointNameFilter;
            return plcAlarmsDAO.getHistoryAlarms(dayDate, regex, offset, limit);
        }
        catch (DataAccessException dataAccessException){
            LOG.trace("Exception on DataBase level.Please debug.");
        }
        catch(Exception exception)
        {
            LOG.trace(exception.getMessage());
        }

        return Collections.emptyList();
    }

    @Override
    public List<LiveAlarm> getLiveAlarms(int offset, int limit) {

        try
        {
            return plcAlarmsDAO.getLiveAlarms(offset, limit);
        }
        catch (DataAccessException dataAccessException){
            LOG.trace("Exception on DataBase level.Please debug.");
        }
        catch (Exception e)
        {
            LOG.trace(e.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public AlarmAcknowledge acknowledge(int id) {
        try
        {
            int uniquenessToken = plcAlarmsDAO.getUniquenessToken(id).orElse(-1);
            if(uniquenessToken == -1)
                return createAcknowledgeResponse("Unknow error", false, id);
            if(uniquenessToken == 0)
                return createAcknowledgeResponse("Alarm or Fault is active!", false, id);
            int result = plcAlarmsDAO.setAcknowledgeTime(id);
            return createAcknowledgeResponse("", result == 1, id);
        }
        catch (DataAccessException e) {
            return createAcknowledgeResponse("Exception on DataBase level.Please debug.", false, id);
        }
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
