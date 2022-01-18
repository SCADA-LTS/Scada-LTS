package org.scada_lts.web.mvc.api;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.UserService;
import org.scada_lts.web.mvc.api.json.JsonUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.web.integration.CrowdUtils;

/**
 * 
 * Controller for data point edition 
 * 
 * @author Grzesiek Bylica grzegorz.bylica@gmail.com
 */
@Controller 
public class AuthenticationAPI {
	
	private static final Log LOG = LogFactory.getLog(AuthenticationAPI.class);
	
	private UserService userService = new UserService();
	
	@RequestMapping(value = "/api/auth/{username}/{password}", method = RequestMethod.GET)
	public ResponseEntity<String> setAuthentication(@PathVariable("username") String username, @PathVariable("password") String password, HttpServletRequest request) {
		LOG.info("/api/auth/{username}/{password} username:" + username);
		
		User user = userService.getUser(username);
		
		Boolean ok = null;

		if (user == null) {
			ok =  false;
		}

		if ( (user != null) && 
			 (!user.getPassword().equals(Common.encrypt(password)))
		   ) {
			ok = false;
		}
		
		if ( user != null &&
			 ok == null) {
			// Update the last login time.
	        userService.recordLogin(user.getId());

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
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<String>(json,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/api/auth/isLogged/{username}", method = RequestMethod.GET)
	public ResponseEntity<String> checkIsLogged(@PathVariable("username") String username, HttpServletRequest request) {
		
		LOG.info("/api/auth/isLogged/{username} username:"+username);
		
		User user = userService.getUser(username);
		
		Boolean ok = null;
		
		User userInServer = Common.getUser(request);
		
		if ( 
				(user != null && userInServer != null) &&
				(user.getUsername().equals(userInServer.getUsername()))
		) {
			ok = true;
		} else {
			ok = false;
		}
		
		ObjectMapper mapper = new ObjectMapper();
		String json = null;
		try {
			json = mapper.writeValueAsString(ok);
		} catch (JsonProcessingException e) {
			LOG.error(e);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<String>(json,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/api/auth/logout/{username}", method = RequestMethod.GET)
	public ResponseEntity<String> setLogout(@PathVariable("username") String username, HttpServletRequest request) {
		LOG.info("/api/auth/logout/{username} username:" + username);
		
		User user = userService.getUser(username);
		
		if (user != null) {
            // The user is in fact logged in. Invalidate the session.
            request.getSession().invalidate();

            if (CrowdUtils.isCrowdEnabled())
                CrowdUtils.logout(request, null);
        }

		ObjectMapper mapper = new ObjectMapper();
		String json = null;
		try {
			json = mapper.writeValueAsString(true);
		} catch (JsonProcessingException e) {
			LOG.error(e);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<String>(json,HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/api/auth/isRoleAdmin", method = RequestMethod.GET)
	public ResponseEntity<String> isRoleAdmin(HttpServletRequest request) {
		LOG.info("/api/auth/isRoleAdmin");
		
		User user = Common.getUser(request);
		
		if (user != null) {
			ObjectMapper mapper = new ObjectMapper();
			String json = null;
			try {
				json = mapper.writeValueAsString(user.isAdmin());
			} catch (JsonProcessingException e) {
				LOG.error(e);
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<String>(json,HttpStatus.OK);
		}
		
		return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		
	}
	
	@RequestMapping(value = "/api/auth/isRoleUser", method = RequestMethod.GET)
	public ResponseEntity<String> isRoleUser(HttpServletRequest request) {
		LOG.info("/api/auth/isRoleUser");
		
		User user = Common.getUser(request);
		
		if (user != null) {
			ObjectMapper mapper = new ObjectMapper();
			String json = null;
			try {
				json = mapper.writeValueAsString(!user.isAdmin());
			} catch (JsonProcessingException e) {
				LOG.error(e);
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<String>(json,HttpStatus.OK);
		}
		
		return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		
	}

	@GetMapping(value = "/api/auth/user")
	public ResponseEntity<JsonUser> getUserInfo(HttpServletRequest request) {
		User user = Common.getUser(request);

		if(user != null) {
			try {
				JsonUser jsonUser = new JsonUser(
						user.getId(), user.getUsername(), user.getFirstName(), user.getLastName(), user.getEmail(),
						user.getPhone(), user.isAdmin(), user.isDisabled(),
						user.getHomeUrl(), user.getLastLogin()
				);
				return new ResponseEntity<>(jsonUser, HttpStatus.OK);
			} catch (Exception e) {
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
	}
	
	
}
