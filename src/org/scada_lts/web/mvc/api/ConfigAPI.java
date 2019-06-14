package org.scada_lts.web.mvc.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.config.ScadaConfig;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
			ObjectMapper mapper = new ObjectMapper();
			String json = null;
			try {
				boolean disableAlert = ScadaConfig.getInstance().getBoolean(ScadaConfig.REPLACE_ALERT_ON_VIEW, false);
				json = mapper.writeValueAsString(disableAlert);	
			} catch (JsonProcessingException e) {
				LOG.error(e);
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			} catch (IOException e) {
				LOG.error(e);
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<String>(json,HttpStatus.OK);
		}
		
		return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
		
	}
	

}
