package org.scada_lts.web.mvc.api.alarms;

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
import org.scada_lts.dao.alarms.AlarmsService;
import org.scada_lts.dao.alarms.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Create by at Mateusz Hyski
 *
 * @author hyski.mateusz@gmail.com
 */
@RestController
public class PlcAlarmsAPI {

    private static final Log LOG = LogFactory.getLog(PlcAlarmsAPI.class);
    private AlarmsService alarmsService = new PlcAlarmsService(PlcAlarmsDAO.getInstance());

    /*
     * example of result:
     *
     * {
     *  "id": 111,
     *  "request": "OK",
     *  "error": "none"
     * }
     * @param id
     * @param request
     * @return String
    *
    */
    @RequestMapping(value = "/api/alarms/acknowledge/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> acknowledgeById(
            @PathVariable("id") String id,
            HttpServletRequest request
    )
    {
        LOG.info(request.getRequestURL());
        String value = "";
        if ( (value = Validation.validateDoParamIsIntegerAndBetween0And9999("id",id)) != null) {
            return new ResponseEntity<>(value, HttpStatus.OK);
        }
        try {
                User user = Common.getUser(request);
                if (user != null && user.isAdmin()) {
                    AcknowledgeAlarm response = alarmsService.acknowledge(Integer.valueOf(id));
                    return new ResponseEntity<>(response , HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
        } catch (Exception e) {
            return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /**
     *
     * example of result:
     *
     * JSONOArray with JSONObjects
     * [
     * {
     *   "id:": 111,
     *   "activation-time": "2020-03-10 07:13:34",
     *   "inactivation-time": "",
     *   "name": "Be ST ALG_StoAllg1.0 Durchflussmessung Drosselkammer Störung Steuersicherung ausgelöst",
     *   "level": "5",
     * },{
     *     ....
     * }
     * ]
     * @param offset
     * @param limit
     * @param request
     * @return String
     */
    @RequestMapping(value = "/api/alarms/live/{offset}/{limit}", method = RequestMethod.GET)
    public ResponseEntity<?> liveAlarms(
            @PathVariable("offset") String offset,
            @PathVariable("limit") String limit,
            HttpServletRequest request
    )
    {
        LOG.info(request.getRequestURL());
        String value = "";
        if ( (value = Validation.validateDoParamIsIntegerAndBetween0And9999("offset",offset)) !=null ){
            return new ResponseEntity<>(value, HttpStatus.OK);
        }
        value="";
        if ( (value = Validation.validateDoParamIsIntegerAndBetween0And9999("limit",limit)) !=null ){
            return new ResponseEntity<>(value, HttpStatus.OK);
        }
        try {
                User user = Common.getUser(request);
                if (user != null && user.isAdmin()) {
                    List<LiveAlarm> result = alarmsService.getLiveAlarms(Integer.parseInt(offset),Integer.parseInt(limit));
                    return new ResponseEntity<>( result, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
        } catch (Exception e) {
            return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /**
     * example of result:
     *
     *  [{
     *      “id”:111,
     *      *“activation-time”:”2020-03-10 -7:13:34”,
     *      “inactivation-time:””,
     *      “name”:”Be ST AL………..”,
     *      “level”:”5”
     *   },
     *   {….},
     *   {…}
     *  ]
     *
     * @param dayDate
     * @param dataPointNameRegexFilter
     * @param offset
     * @param limit
     * @param request
     * @return String
     */
    @RequestMapping(value = "/api/alarms/history/{date_day}/{data_point_name_regex_filter}/{offset}/{limit}", method = RequestMethod.GET)
    public ResponseEntity<?> getHistoryAlarms(
            @PathVariable("date_day") String dayDate,
            @PathVariable("data_point_name_regex_filter") String dataPointNameRegexFilter,
            @PathVariable("offset") String offset,
            @PathVariable("limit") String limit,
            HttpServletRequest request
    )
    {
        LOG.info(request.getRequestURL());
        String value = "";
        if ( ( value = Validation.doGivenParameterHaveCorrectDateFormat(dayDate)) != null ){
            return new ResponseEntity<>("Value date_day is not correct."+value, HttpStatus.OK);
        }
        int offsetParam = Integer.parseInt(offset);
        int limitParam = Integer.parseInt(limit);
        try {
            User user = Common.getUser(request);
            if (user != null && user.isAdmin()) {
                List<HistoryAlarm> result = LangUtil.translate(alarmsService.getHistoryAlarms(dayDate, dataPointNameRegexFilter, offsetParam, limitParam), request.getLocale());
                return new ResponseEntity<>(result , HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
