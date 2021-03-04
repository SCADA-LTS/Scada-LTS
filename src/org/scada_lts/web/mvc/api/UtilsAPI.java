/*
 * (c) 2017 Abil'I.T. http://abilit.eu/
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.scada_lts.web.mvc.api;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.UserComment;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.UserCommentService;
import org.scada_lts.service.UtilsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Controller for API watchList
 * 
 * @author Grzesiek Bylica grzegorz.bylica@gmail.com
 */
@Controller 
public class UtilsAPI {
	
	private static final Log LOG = LogFactory.getLog(UtilsAPI.class);

	@Resource
	private UtilsService utilsService;
	@Resource
	private UserCommentService userCommentService;
	

	@RequestMapping(value = "/api/utils/getTs", method = RequestMethod.GET)
	public ResponseEntity<String> getTs(HttpServletRequest request) {
		LOG.info("/api/utils/getTs");
		try {
			String json = null;
			ObjectMapper mapper = new ObjectMapper();
			json = mapper.writeValueAsString(utilsService.getTS());
				
			return new ResponseEntity<String>(json,HttpStatus.OK);				

		} catch (Exception e) {
			LOG.error(e);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Create User Comment
	 *
	 * @param request HTTP request
	 * @param body UserComment object
	 * @param typeId UserComment type (1 - Event or 2 - Point)
	 * @param refId Reference ID of the object
	 *
	 * @return Status
	 */
	@PostMapping(value = "/api/utils/userComment/{typeId}/{refId}")
	public ResponseEntity<String> createUserComment(HttpServletRequest request, @RequestBody UserComment body, @PathVariable("typeId") int typeId, @PathVariable("refId") int refId) {
		try {
			User user = Common.getUser(request);
			if(user != null) {
				int result = userCommentService.setUserComment(body, typeId, refId);
				if(result != 0) {
					return new ResponseEntity<>(String.valueOf(result), HttpStatus.CREATED);
				} else {
					return new ResponseEntity<>(String.valueOf(result), HttpStatus.BAD_REQUEST);
				}
			} else {
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Delete User Comment
	 *
	 * @param request HTTP request
	 * @param typeId UserComment type (1 - Event or 2 - Point)
	 * @param refId Id of the object
	 * @param userId Author User Id
	 * @param ts Timestamp
	 *
	 * @return Status
	 */
	@DeleteMapping(value = "/api/utils/userComment/{typeId}/{refId}/{userId}/{ts}")
	public ResponseEntity<String> createUserComment(HttpServletRequest request, @PathVariable("typeId") int typeId, @PathVariable("refId") int refId, @PathVariable("userId") int userId , @PathVariable("ts") long ts)  {
		try {
			User user = Common.getUser(request);
			if(user != null) {
				int result = userCommentService.deleteUserComment(userId, typeId, refId, ts);
				if(result != 0) {
					return new ResponseEntity<>(String.valueOf(result), HttpStatus.OK);
				} else {
					return new ResponseEntity<>(String.valueOf(result), HttpStatus.BAD_REQUEST);
				}
			} else {
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
