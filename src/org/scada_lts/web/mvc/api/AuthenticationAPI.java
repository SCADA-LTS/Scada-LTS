package org.scada_lts.web.mvc.api;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.vo.User;

/**
 * Controller for data point edition
 * 
 * @author Grzesiek Bylica grzegorz.bylica@gmail.com
 */
@Controller 
public class AuthenticationAPI {
	
	private static final Log LOG = LogFactory.getLog(AuthenticationAPI.class);

	@RequestMapping(value = "/api/auth/{username}/{password}", method = RequestMethod.GET)
	public @ResponseBody String setAuthentication(@PathVariable("username") String username, @PathVariable("password") String password, HttpServletRequest request) {
		LOG.info("/api/auth/{username}/{password} username:" + username);
		
		UserDao userDAO = new UserDao();
		User user = userDAO.getUser(username);
		
		Boolean ok = null;

		if (user == null) {
			ok =  false;
		}

		if (!user.getPassword().equals(Common.encrypt(password))) {
			ok = false;
		}
		
		if (ok == null) {
			// Update the last login time.
	        userDAO.recordLogin(user.getId());

	        // Add the user object to the session. This indicates to the rest
	        // of the application whether the user is logged in or not.
	        Common.setUser(request, user);
	        if (LOG.isDebugEnabled()) {
	        	LOG.debug("User object added to session");
	        }
	        ok = new Boolean(true);
		}

		ObjectMapper mapper = new ObjectMapper();
		String json = null;
		try {
			json = mapper.writeValueAsString(ok);
		} catch (JsonProcessingException e) {
			LOG.error(e);
		}
		return json;
		
	}
}
