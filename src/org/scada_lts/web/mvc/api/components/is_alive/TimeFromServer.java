package org.scada_lts.web.mvc.api.components.is_alive;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

/**
 * @autor grzegorz.bylica@gmail.com on 24.09.2019
 */
@Controller
public class TimeFromServer {

    private static final Log LOG = LogFactory.getLog(TimeFromServer.class);

    @RequestMapping(value = "/api/is_alive/time", method = RequestMethod.GET)
    public ResponseEntity<Long> time(HttpServletRequest request) {
        LOG.info("/api/is_alive/time");

        try {
            User user = Common.getUser(request);

            if (user != null) {
                long unixTimestamp = Instant.now().getEpochSecond();
                return new ResponseEntity<>(unixTimestamp, HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(e);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
