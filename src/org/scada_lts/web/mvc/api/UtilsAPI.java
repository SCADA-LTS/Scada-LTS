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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.service.UtilsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


/**
 * Controller for API watchList
 * 
 * @author Grzesiek Bylica grzegorz.bylica@gmail.com
 */
@Controller 
public class UtilsAPI {
	
	private static final Log LOG = LogFactory.getLog(UtilsAPI.class);

	private final UtilsService utilsService;

	public UtilsAPI(UtilsService utilsService) {
		this.utilsService = utilsService;
	}

	@RequestMapping(value = "/api/utils/getTs", method = RequestMethod.GET)
	public ResponseEntity<String> getTs(HttpServletRequest request) {
		LOG.info("/api/utils/getTs");
		try {
			long ts = utilsService.getTS();
			return new ResponseEntity<>(String.valueOf(ts),HttpStatus.OK);

		} catch (Exception e) {
			LOG.error(e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
}
