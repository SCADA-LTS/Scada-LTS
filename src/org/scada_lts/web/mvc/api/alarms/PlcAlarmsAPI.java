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
package org.scada_lts.web.mvc.api.alarms;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.alarms.AlarmAcknowledge;
import org.scada_lts.dao.alarms.HistoryAlarm;
import org.scada_lts.dao.alarms.AlarmsService;
import org.scada_lts.dao.alarms.LiveAlarm;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.scada_lts.web.mvc.api.alarms.Validation.validateBetweenZeroTo9999;
import static org.scada_lts.web.mvc.api.alarms.Validation.validateDateFormat;
import static org.scada_lts.web.mvc.api.alarms.Validation.validateNumberFormat;

/**
 * Create by at Mateusz Hyski
 *
 * @author hyski.mateusz@gmail.com
 * @update kamil.jarmusik@gmail.com
 *
 */

@RestController
public class PlcAlarmsAPI {

    private static final Log LOG = LogFactory.getLog(PlcAlarmsAPI.class);
    private final AlarmsService alarmsService;

    public PlcAlarmsAPI(AlarmsService alarmsService) {
        this.alarmsService = alarmsService;
    }

    /**
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
    public ResponseEntity<?> postAcknowledgeById(
            @PathVariable("id") int id,
            HttpServletRequest request
    )
    {
        LOG.info(request.getRequestURL());

        User user = Common.getUser(request);
        if (user != null && user.isAdmin()) {
            AlarmAcknowledge response = alarmsService.acknowledge(id);
            return new ResponseEntity<>(response , HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
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
    public ResponseEntity<?> getLiveAlarms(
            @PathVariable("offset") String offset,
            @PathVariable("limit") String limit,
            HttpServletRequest request
    )
    {
        LOG.info(request.getRequestURL());

        User user = Common.getUser(request);
        if (user != null) {
            String value = validateBetweenZeroTo9999("limit", limit);
            if (!value.isEmpty()) {
                return new ResponseEntity<>(value, HttpStatus.BAD_REQUEST);
            }
            List<LiveAlarm> result = alarmsService.getLiveAlarms(Integer.parseInt(offset),Integer.parseInt(limit));
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
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
     * @param dataPointNameFilterRegex
     * @param offset
     * @param limit
     * @param request
     * @return String
     */
    @RequestMapping(value = "/api/alarms/history/{day_date}/{data_point_name_filter_regex}/{offset}/{limit}", method = RequestMethod.GET)
    public ResponseEntity<?> getHistoryAlarms(
            @PathVariable("day_date") String dayDate,
            @PathVariable("data_point_name_filter_regex") String dataPointNameFilterRegex,
            @PathVariable("offset") String offset,
            @PathVariable("limit") String limit,
            HttpServletRequest request
    )
    {
        LOG.info(request.getRequestURL());

        User user = Common.getUser(request);
        if (user != null) {
            String value = validateDateFormat(dayDate) +
                    validateNumberFormat("offset", offset) +
                    validateBetweenZeroTo9999("limit", limit);
            if (!value.isEmpty()) {
                return new ResponseEntity<>(value, HttpStatus.BAD_REQUEST);
            }
            List<HistoryAlarm> result = alarmsService.getHistoryAlarms(dayDate, dataPointNameFilterRegex,
                    Integer.parseInt(offset), Integer.parseInt(limit));
            return new ResponseEntity<>(result , HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

}
