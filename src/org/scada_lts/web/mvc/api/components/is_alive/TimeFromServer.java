package org.scada_lts.web.mvc.api.components.is_alive;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.Instant;
import java.util.Date;

/**
 * @autor grzegorz.bylica@gmail.com on 24.09.2019
 */
@Controller
@RequestMapping(value = "/api/is_alive")
public class TimeFromServer {

    private static final Log LOG = LogFactory.getLog(TimeFromServer.class);

    @GetMapping(value = "/time")
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

    @GetMapping(value = "/time2")
    public ResponseEntity<Long> getServerTime(HttpServletRequest request) {
        LOG.info("/api/is_alive/time2");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                long unixTimestamp = System.currentTimeMillis();
                return new ResponseEntity<>(unixTimestamp, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/watchdog")
    public ResponseEntity<String> notifyWatchdog(@RequestBody WatchDogConfig config, HttpServletRequest request) {
        String rootKey = "watchdog.response";
        try {
            User user = Common.getUser(request);
            if(config == null) { return new ResponseEntity<>(rootKey + ".badRequest", HttpStatus.BAD_REQUEST); }
            if(user == null) { return new ResponseEntity<>(rootKey + ".unauthorized", HttpStatus.UNAUTHORIZED); }
            sendWatchdogMessage(config);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IOException e) {
            LOG.warn(e.getMessage());
            return new ResponseEntity<>(rootKey + ".unavailable", HttpStatus.SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(rootKey + ".unexpected",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void sendWatchdogMessage(WatchDogConfig config) throws IOException {
        Socket socket = new Socket(config.getHost(), config.getPort());
        DataOutputStream messageStream = new DataOutputStream(socket.getOutputStream());
        messageStream.writeBytes("ping");
        socket.close();
        messageStream.close();
    }
}

class WatchDogConfig {
    private String host;
    private int port;

    public WatchDogConfig() {
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}