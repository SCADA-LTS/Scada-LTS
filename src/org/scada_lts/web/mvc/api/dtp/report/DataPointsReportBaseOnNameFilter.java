package org.scada_lts.web.mvc.api.dtp.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serotonin.mango.Common;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.model.point.PointValue;
import org.scada_lts.mango.service.DataPointService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @autor grzegorz.bylica@gmail.com on 16.03.2020
 */

@Controller
public class DataPointsReportBaseOnNameFilter {

    private static final Log LOG = LogFactory.getLog(DataPointsReportBaseOnNameFilter.class);

    DataPointService dataPointService = new DataPointService();

    @RequestMapping(value = "/api/dtp/report/get/{partOfNameDS}/{partOfNamePoint}/{startTime}/{endTime}", method = RequestMethod.GET)
    public ResponseEntity<String> get(
            @PathVariable("partOfNameDS") String partOfNameDS,
            @PathVariable("partOfNamePoint") String partOfNamePoint,
            @PathVariable("startTime") String startTime,
            @PathVariable("endTime") String endTime,
            HttpServletRequest request) {

        LOG.info( String.format(
                "/api/dtp/report/get/ partOfName: %s, " +
                   "partOfNameDS: %s, " +
                   "startTime: %s " +
                   "endTime: %s ", partOfNamePoint, partOfNameDS, startTime, endTime) );

        try {
            // valid data insert
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            Date startTimeDT = simpleDateFormat.parse(startTime);
            Date endTimeDT = simpleDateFormat.parse(endTime);
            try {
                if (partOfNamePoint == null || partOfNamePoint.length() == 0) {
                    throw new Exception("partOfName paramter is not correct");
                } else if (partOfNameDS == null || partOfNameDS.length() == 0) {
                    throw new Exception("partOfNameDS paramter is not correct");
                }
            } catch (Exception e) {
                LOG.error(e);
                return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
            }


            User user = Common.getUser(request);

            if (user != null && user.isAdmin()) {


                Map<DataPointVO, List<PointValue>> pointsAndValuesFromRangeTime = dataPointService.getDataPoints(partOfNameDS,partOfNamePoint, startTimeDT, endTimeDT);
                List result = dataPointService.valuesPointBooleanBaseOnNameFilter2DTO(pointsAndValuesFromRangeTime);
                String json = null;
                ObjectMapper mapper = new ObjectMapper();
                json = mapper.writeValueAsString(result);

                return new ResponseEntity<String>("{\"data\":"+json+"}", HttpStatus.OK);
            }

            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }
}
