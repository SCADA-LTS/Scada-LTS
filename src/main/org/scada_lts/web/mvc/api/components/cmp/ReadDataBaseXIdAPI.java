package org.scada_lts.web.mvc.api.components.cmp;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.PointValueService;
import org.scada_lts.web.mvc.api.components.cmp.model.ReadValuePointDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


/**
 * @author grzegorz.bylica@gmail.com on 17.01.19
 */
@Controller
public class ReadDataBaseXIdAPI {

    private static final Log LOG = LogFactory.getLog(ReadDataBaseXIdAPI.class);

    private DataPointService dataPointService = new DataPointService();
    @Resource
    private PointValueService pointValueService;

    @RequestMapping(value = "/api/cmp/get/{xIDs}", method = RequestMethod.GET)
    public ResponseEntity<List<ReadValuePointDTO>> get(@PathVariable(name = "xIDs") String[] xIDs, HttpServletRequest request) {
        LOG.info("/api/cmp/get xIDs:" + xIDs);

        try {
            User user = Common.getUser(request);

            if (user != null) {
                List<ReadValuePointDTO> results = new ArrayList<>();
                for (int i=0; i<xIDs.length; i++) {
                    ReadValuePointDTO v = new ReadValuePointDTO(xIDs[i]);
                    try {
                        DataPointVO dpvo = dataPointService.getDataPoint(xIDs[i]);
                        PointValueTime pvt = pointValueService.getLatestPointValue(dpvo.getId());
                        v.set(pvt, dpvo);
                        results.add(v);
                    } catch (Exception e) {
                        v.setError(e.getMessage());
                        results.add(v);
                    }
                }

                return new ResponseEntity<List<ReadValuePointDTO>>(results, HttpStatus.OK);
            }

            return new ResponseEntity<List<ReadValuePointDTO>>(HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(e);
            }
            return new ResponseEntity<List<ReadValuePointDTO>>(HttpStatus.BAD_REQUEST);
        }
    }

}
