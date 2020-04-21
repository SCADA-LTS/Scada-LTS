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
import org.scada_lts.service.ResourcesService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Create by at Mateusz Hyski
 *
 * @author hyski.mateusz@gmail.com
 */
@Controller
public class StorungsAndAlarmsAPI {

    private static final Log LOG = LogFactory.getLog(StorungsAndAlarmsAPI.class);

    @RequestMapping(value = "/api/alarms/{sortDataActivation}/{sortDataInactivation}/{sortPointName}/{sortStatus}", method = RequestMethod.GET)
    public ResponseEntity<String> getAlarms(
            @PathVariable("sortDataActivation") String sortDataActivation,
            @PathVariable("sortDataInactivation") String sortDataInactivation,
            @PathVariable("sortPointName") String sortPointName,
            @PathVariable("sortStatus") String sortStatus,
            HttpServletRequest request
    )
    {
        LOG.info("/api/alarms/{sortDataActivation}/{sortDataInactivation}/{sortPointName}/{sortStatus}");
        boolean allNeeddedValuesExist = true;
        if( sortDataActivation.isEmpty()){
            LOG.info("SortDataActivation is empty.SortDataActivation can't be empty");
            allNeeddedValuesExist=false;
        }
        if( sortDataInactivation.isEmpty()){
            LOG.info("SortDataInactivation is empty.SortDataInactivation can't be empty");
            allNeeddedValuesExist=false;
        }
        if( sortPointName.isEmpty()){
            LOG.info("SortPointName is empty.SortPointName can't be empty");
            allNeeddedValuesExist=false;
        }
        if( sortStatus.isEmpty()){
            LOG.info("SortStatus is empty.SortStatus can't be empty");
            allNeeddedValuesExist=false;
        }


        try {

            if( allNeeddedValuesExist ) {

                User user = Common.getUser(request);
                if (user != null && user.isAdmin()) {
                    System.out.println("get data from table");
                    return new ResponseEntity<String>(HttpStatus.OK);
                } else {
                    return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
                }
            }
            else {
                new ResponseEntity<String>("Some of value (sortDataActivation,sortDataInactivation,sortPointName,sortStatus) is empty", HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
