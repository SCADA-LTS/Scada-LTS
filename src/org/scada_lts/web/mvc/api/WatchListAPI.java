package org.scada_lts.web.mvc.api;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.WatchListService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
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
	
	private static final Log LOG = LogFactory.getLog(WatchListAPI.class);
	
	@Resource
	private WatchListService watchListService;

	@RequestMapping(value = "/api/watchlist/getNames", method = RequestMethod.GET)
	public @ResponseBody String getWatchListNames(HttpServletRequest request) {
		LOG.info("/api/watchlist/getNames");
		
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
	
	/**
	 * 
	 * @param xid
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/api/watchlist/getPoints/{xid}", method = RequestMethod.GET)
	public @ResponseBody String getWatchListNames(@PathVariable("xid") String xid, HttpServletRequest request) {
		LOG.info("/api/watchlist/getPoints/{xid} xid:"+xid);
		
		// check may use watch list
		//User user = Common.getUser(request);
		
		WatchList wl = watchListService.getWatchList(xid);
		watchListService.populateWatchlistData(wl);		
		String json = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			json = mapper.writeValueAsString(wl);
		} catch (JsonProcessingException e) {
			LOG.error(e);
		}
		return json;
	}

}
