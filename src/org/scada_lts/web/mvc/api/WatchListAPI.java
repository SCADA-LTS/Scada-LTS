package org.scada_lts.web.mvc.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.PointValueService;
import org.scada_lts.mango.service.UserService;
import org.scada_lts.mango.service.WatchListService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.WatchList;

/**
 * Controller for API watchList
 * 
 * @author Grzesiek Bylica grzegorz.bylica@gmail.com
 */
class PointJSON implements Serializable{
	private String xid;
	private String name;
	PointJSON(String xid,String name) {
		this.setXid(xid);
		this.setName(name);
	}
	public String getXid() {
		return xid;
	}
	public void setXid(String xid) {
		this.xid = xid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
class WatchListJSON implements Serializable{
	private long id;
	private String xid;
	private String name;
	WatchListJSON(long id,String xid,String name) {
		this.setId(id);
		this.setXid(xid);
		this.setName(name);
	}

	public long getId() { return id; }
	public void setId(long id) { this.id = id; }
	public String getXid() {
		return xid;
	}
	public void setXid(String xid) {
		this.xid = xid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
class WatchListJSONByNames implements Serializable{
	private String xid;
	private String name;
	WatchListJSONByNames(String xid, String name) {
		this.setXid(xid);
		this.setName(name);
	}
	public String getXid() {
		return xid;
	}
	public void setXid(String xid) {
		this.xid = xid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
class DataChartJSON implements Serializable{
	private String xid;
	private String name;
	private List<ValueToJSON> values;
	DataChartJSON(String xid, String name, List<ValueToJSON> values) {
		this.setXid(xid);
		this.setName(name);
		this.setValues(values);
	}
	public String getXid() {
		return xid;
	}
	public void setXid(String xid) {
		this.xid = xid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<ValueToJSON> getValues() {
		return values;
	}
	public void setValues(List<ValueToJSON> values) {
		this.values=values;
	}
}
class WatchListPermissionsJSON implements Serializable {
	private long watchListId;
	private int permission;

	public WatchListPermissionsJSON(long watchListId, int permission) {
		this.watchListId = watchListId;
		this.permission = permission;
	}

	public long getWatchListId() {
		return watchListId;
	}

	public void setWatchListId(long watchListId) {
		this.watchListId = watchListId;
	}

	public int getPermission() {
		return permission;
	}

	public void setPermission(int permission) {
		this.permission = permission;
	}
}

@Controller 
public class WatchListAPI {

	private static final Log LOG = LogFactory.getLog(WatchListAPI.class);
	
	@Resource
	private WatchListService watchListService;
	
	@Resource
	private PointValueService pointValueService;
	
	@Resource
	private DataPointService dataPointService;

	UserService userService = new UserService();

	@RequestMapping(value = "/api/watchlist/getAll", method = RequestMethod.GET)
	public ResponseEntity<String> getAll(HttpServletRequest request) {
		LOG.info("/api/watchlist/getAll");
		try {
			User user = Common.getUser(request);

			if (user != null) {

				List<WatchList> lstWL=(user.isAdmin())
					? watchListService.getWatchLists()
					: watchListService.getWatchLists(user.getId(), user.getUserProfile());

				List<WatchListJSON> lst = new ArrayList<WatchListJSON>();
				for (WatchList wl:lstWL) {
					WatchListJSON wlJ = new WatchListJSON(wl.getId(), wl.getXid(), wl.getName());
					lst.add(wlJ);
				}

				return new ResponseEntity<String>(new ObjectMapper().writeValueAsString(lst),HttpStatus.OK);
			}

			return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);

		} catch (Exception e) {
			LOG.error(e);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}
	

	@RequestMapping(value = "/api/watchlist/getNames", method = RequestMethod.GET)
	public ResponseEntity<String> getNames(HttpServletRequest request) {
		LOG.info("/api/watchlist/getNames");
		try {
			User user = Common.getUser(request);
		
			if (user != null) {
				List<WatchList> lstWL=(user.isAdmin())
					? watchListService.getWatchLists()
					: watchListService.getWatchLists(user.getId(), user.getUserProfile());
				
				List<WatchListJSONByNames> lst = new ArrayList<WatchListJSONByNames>();
				for (WatchList wl:lstWL) {
					WatchListJSONByNames wlJ = new WatchListJSONByNames(wl.getXid(), wl.getName());
					lst.add(wlJ);
				}
				
				return new ResponseEntity<String>(new ObjectMapper().writeValueAsString(lst),HttpStatus.OK);
			} 
			
			return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
			
		} catch (Exception e) {
			LOG.error(e);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * 
	 * @param xid
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/api/watchlist/getPoints/{xid}", method = RequestMethod.GET)
	public ResponseEntity<String> getPoints(@PathVariable("xid") String xid, HttpServletRequest request) {
		
		LOG.info("/api/watchlist/getPoints/{xid} xid:"+xid);
		
		try {
			User user = Common.getUser(request);
			if (user != null) {
			
				WatchList wl = watchListService.getWatchList(xid);
				watchListService.populateWatchlistData(wl);
				List<PointJSON> lst = new ArrayList<PointJSON>();
			
				for (DataPointVO dpvo : wl.getPointList()){
					PointJSON p = new PointJSON(dpvo.getXid(), dpvo.getName());
					lst.add(p);
				}
			
				return new ResponseEntity<String>(new ObjectMapper().writeValueAsString(lst),HttpStatus.OK);
			}
			
			return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
			
		} catch (Exception e) {
			LOG.error(e);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/api/watchlist/getChartData/{xid}/{fromData}/{toData}", method = RequestMethod.GET)
	public ResponseEntity<String> getChartData(@PathVariable("xid") String xid, @PathVariable("fromData") Long fromData, @PathVariable("toData") Long toData, HttpServletRequest request) {
		
		LOG.info("/api/watchlist/getChartData/{xid}/{fromData}/{toData} xid:"+xid+" fromData:"+fromData+" toData:"+toData);
		
		try {
			User user = Common.getUser(request);
			if (user != null) {
			
				DataPointVO dp = dataPointService.getDataPoint(xid);
				
				List<PointValueTime> listValues = pointValueService.getPointValuesBetween(dp.getId(), fromData, toData);
				
				List<ValueToJSON> values = new ArrayList<ValueToJSON>();
				
				for (PointValueTime pvt : listValues) {
					ValueToJSON v = new ValueToJSON();
					v.set(pvt, dp);
					values.add(v);
				}
			
				DataChartJSON dataChartJSON = new DataChartJSON(xid, dp.getName(), values);
				
				return new ResponseEntity<String>(new ObjectMapper().writeValueAsString(dataChartJSON),HttpStatus.OK);
			}
			
			return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
		
		} catch (Exception e) {
			LOG.error(e);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/api/watchlist/getAllPermissions/{userId}", method = RequestMethod.GET)
	public ResponseEntity<String> getAllPermissions(@PathVariable("userId") int userId, HttpServletRequest request) {
		LOG.info("/api/watchlist/getAll");

		try {
			User user = Common.getUser(request);

			if (user != null) {


				List<WatchListPermissionsJSON> watchlistPermissionsJSONS = new ArrayList<>();

				List<WatchList> watchLists = new ArrayList<>();
				watchLists = watchListService.getWatchLists();
				watchLists.stream().forEach(w -> {
					watchlistPermissionsJSONS.add(new WatchListPermissionsJSON(w.getId(), w.getUserAccess(userService.getUser(userId))));
				});

				return new ResponseEntity<String>(new ObjectMapper().writeValueAsString(watchlistPermissionsJSONS), HttpStatus.OK);
			}

			return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			LOG.error(e);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}

}
