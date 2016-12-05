package org.scada_lts.web.mvc.api;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.WebContextFactory;
import org.scada_lts.mango.service.WatchListService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.WatchList;

/**
 * Controller for API watchList
 * 
 * @author Grzesiek Bylica grzegorz.bylica@gmail.com
 */
@Controller 
public class WatchListAPI {
	
	private WatchListService watchListService = new WatchListService();
	
	private static final Log LOG = LogFactory.getLog(WatchListAPI.class);

	@RequestMapping(value = "/api/watchlist/names", method = RequestMethod.GET)
	public @ResponseBody String getWatchListNames(HttpServletRequest request) {
		LOG.info("/api/watchlist/names");
		
		User user = Common.getUser(request);
		
		int userId = user.getId();
		int profileId = user.getUserProfile();
		List<WatchList> lst = watchListService.getWatchLists(userId, profileId);
		
		String json = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			json = mapper.writeValueAsString(lst);
		} catch (JsonProcessingException e) {
			LOG.error(e);
		}
		return json;
	}


}
