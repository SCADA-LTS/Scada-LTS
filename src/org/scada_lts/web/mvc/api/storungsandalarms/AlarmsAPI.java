package org.scada_lts.web.mvc.api.storungsandalarms;

/*
 * (c) 2018 hyski.mateusz@gmail.com
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

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.scada_lts.dao.PointValuesStorungsAndAlarms;
import org.scada_lts.dao.storungsAndAlarms.StorungsAndAlarms;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Create by at Mateusz Hyski
 *
 * @author hyski.mateusz@gmail.com
 */
@Controller
public class AlarmsAPI extends Validation{

    private static final Log LOG = LogFactory.getLog(AlarmsAPI.class);
    private static PointValuesStorungsAndAlarms pointValuesStorungsAndAlarms =new StorungsAndAlarms();
    private boolean validate(String paramName,String param){
        if( !param.equals("0") || param.equals("1")){
            LOG.info(paramName+" is empty."+paramName+" can't be empty");
            return false;
        }
        return true;
    }
    @RequestMapping(value = "/api/acknowledge/{id}", method = RequestMethod.POST)
    public ResponseEntity<String> acknowledgeById(
            @PathVariable("id") String id,
            HttpServletRequest request
    )
    {
        LOG.info("/api/acknowledge/{id}");
        if ( !validate("id",id) ){
            new ResponseEntity<String>("Value id is empty", HttpStatus.OK);
        }
        try {
                User user = Common.getUser(request);
                if (user != null && user.isAdmin()) {
                    JSONObject result=new JSONObject();
                    pointValuesStorungsAndAlarms.setAcknowledge(Integer.valueOf(id),result);
                    return new ResponseEntity<String>( result.toString() , HttpStatus.OK);
                } else {
                    return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
                }
        } catch (Exception e) {
            return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value = "/alarms/history/{date_day}/{filter_with_mysqlrlike}/{offset}/{limit}", method = RequestMethod.GET)
    public ResponseEntity<String> getHistoryAlarms(
            @PathVariable("date_day") String date_day,
            @PathVariable("filter_with_mysqlrlike") String filter_with_mysqlrlike,
            @PathVariable("offset") String offset,
            @PathVariable("limit") String limit,
            HttpServletRequest request
    )
    {
        LOG.info("/alarms/history/{date_day}/{filter_with_mysqlrlike}/{offset}/{limit}");
        String value = "";
        if ( ( value = doGivenParameterHaveCorrectDateFormat(date_day)) != null ){
            return new ResponseEntity<String>("Value date_day is not correct."+value, HttpStatus.OK);
        }
        if ( !validate("filter_with_mysqlrlike",filter_with_mysqlrlike) ){
            return new ResponseEntity<String>("Value filter_with_mysqlrlike is empty", HttpStatus.OK);
        }
        int offsetParam = Integer.parseInt(offset);
        int limitParam = Integer.parseInt(limit);
        try {
            User user = Common.getUser(request);
            if (user != null && user.isAdmin()) {
                JSONArray jsonArrayResult = pointValuesStorungsAndAlarms.getHistoryAlarmsByDateDayAndFilterOnlySinceOffsetAndLimit(date_day, filter_with_mysqlrlike, offsetParam, limitParam);
                return new ResponseEntity<String>( jsonArrayResult.toString() , HttpStatus.OK);
            } else {
                return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
