package org.scada_lts.dao.storungsAndAlarms;
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
import org.json.JSONObject;
import org.scada_lts.dao.PointValuesStorungsAndAlarms;
import org.springframework.dao.DataAccessException;

import java.util.Collections;
import java.util.List;

/**
 * Create by at Mateusz Hyski
 *
 * @author hyski.mateusz@gmail.com
 */

public class StorungsAndAlarms implements PointValuesStorungsAndAlarms {

    private static final Log LOG = LogFactory.getLog(StorungsAndAlarms.class);

    @Override
    public List<ApiAlarmsHistory> getHistoryAlarmsByDateDayAndFilter(String dayDate, String dataPointNameRegexFilter, int offset, int limit) {
        try
        {
            return DAOs.getPointValuesStorungsAndAlarms().getHistoryAlarmsByDateDayAndFilter(dayDate, dataPointNameRegexFilter,offset,limit);
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
    public List<ApiAlarmsLive> getLiveAlarms(int offset, int limit) {

        try
        {
            return DAOs.getPointValuesStorungsAndAlarms().getLiveAlarms(offset,limit);
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
    public AcknowledgeResponse acknowledge(int id) {

        String error = "";
        boolean result = false;
        try
        {
            result = (DAOs.getPointValuesStorungsAndAlarms().setAcknowledge(id)==1)?true:false;
        }
        catch (DataAccessException e) {
            error = "Exception on DataBase level.Please debug.";
        }
        return createAcknowledgeResponse(error, result, id);
    }

    /**
     * method use as a helper to build a jsonobject by values when
     * error occur on DataBase level or another
     * or if everything is OK
     *
     * @param errorMessage
     * @param result
     * @param id
     * @return
     */
    private AcknowledgeResponse createAcknowledgeResponse(String errorMessage, boolean result, int id) {

        AcknowledgeResponse acknowledgeResponse = new AcknowledgeResponse();
        acknowledgeResponse.setId(id);
        if(!result){
            acknowledgeResponse.setError("Object with id="+id+" do not exist");
            acknowledgeResponse.setRequest("FAULT");
        }
        else {
            if( (errorMessage.length()!=0) ){
                acknowledgeResponse.setError(errorMessage);
                acknowledgeResponse.setRequest("FAULT");
            }
            else {
                acknowledgeResponse.setError("none");
                acknowledgeResponse.setRequest("OK");
            }
        }

        return acknowledgeResponse;
    }
}
