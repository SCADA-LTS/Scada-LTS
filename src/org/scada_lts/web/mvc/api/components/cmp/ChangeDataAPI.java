package org.scada_lts.web.mvc.api.components.cmp;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.model.point.PointValueTypeOfREST;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.PointValueService;
import org.scada_lts.web.mvc.api.components.cmp.model.SetValuePointDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;



/**
 * @autor grzegorz.bylica@gmail.com on 17.01.19
 */
@Controller
public class ChangeDataAPI {

    private static final Log LOG = LogFactory.getLog(ChangeDataAPI.class);

    private DataPointService dataPointService = new DataPointService();

    @Resource
    private PointValueService pointValueService;


    @RequestMapping(value = "/api/cmp/set/{xIDsValues}/", method = RequestMethod.POST)
    public ResponseEntity<SetValuePointDTO[]> get(@PathVariable(name = "xIDsValues") SetValuePointDTO[] xIDsValues, HttpServletRequest request) {
        LOG.info("/api/cmp/set xIDSsValues:" + xIDsValues);

        try {
            User user = Common.getUser(request);

            if (user != null) {

                for (SetValuePointDTO sv : xIDsValues) {
                    try {
                        dataPointService.save(sv.getXid(), String.valueOf(sv.isValue()), PointValueTypeOfREST.TYPE_BINARY);
                    } catch (Exception e) {
                        sv.setError(e.getMessage());
                    }
                }

                return new ResponseEntity<SetValuePointDTO[]>(xIDsValues, HttpStatus.OK);
            }

            return new ResponseEntity<SetValuePointDTO[]>(HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(e);
            }
            return new ResponseEntity<SetValuePointDTO[]>(HttpStatus.BAD_REQUEST);
        }
    }


}
