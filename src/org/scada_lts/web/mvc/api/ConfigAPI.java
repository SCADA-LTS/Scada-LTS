package org.scada_lts.web.mvc.api;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.config.ScadaConfig;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;

/**
 * 
 * Controller for config API
 * 
 * @author Grzesiek Bylica grzegorz.bylica@gmail.com
 */
@Controller 
public class ConfigAPI {
	
	private static final Log LOG = LogFactory.getLog(ConfigAPI.class);
	
	@RequestMapping(value = "/api/config/replacealert", method = RequestMethod.GET)
	public ResponseEntity<String> getConfig(HttpServletRequest request) {
		LOG.info("/api/config/replacealert");
		
		User user = Common.getUser(request);
		
		if (user != null) {
			try {
				boolean disableAlert = ScadaConfig.getInstance().getBoolean(ScadaConfig.REPLACE_ALERT_ON_VIEW, false);
				return new ResponseEntity<>(String.valueOf(disableAlert),HttpStatus.OK);
			} catch (Exception e) {
				LOG.error(e);
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		}
		
		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		
	}
	

}
