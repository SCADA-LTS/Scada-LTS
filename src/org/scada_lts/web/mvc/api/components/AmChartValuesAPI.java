package org.scada_lts.web.mvc.api.components;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.pointvalues.PointValueAmChartDAO;
import org.scada_lts.mango.service.DataPointService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping(path = "/api/amcharts")
public class AmChartValuesAPI {
    private static final Log LOG = LogFactory.getLog(AmChartValuesAPI.class);

    private static final PointValueAmChartDAO dao = new PointValueAmChartDAO();

    private static final DataPointService dpService = new DataPointService();

    @GetMapping("/")
    public ResponseEntity<List<Map<String, Double>>> getValuesFromTimeRange(
            @RequestParam String ids,
            @RequestParam long startTs,
            @RequestParam long endTs,
            @RequestParam Optional<Integer> xid,
            HttpServletRequest request
    ) {
        try {
            User user = Common.getUser(request);
            if(user != null) {
                if(xid.isPresent() && xid.get() == 1) {
                    return new ResponseEntity<>(dpService.getPointValuesFromRangeXid(ids, startTs, endTs), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(dpService.getPointValuesFromRangeId(ids, startTs, endTs), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
