package org.scada_lts.web.mvc.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.vo.permission.Permissions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.PointValueService;
import org.scada_lts.mango.service.WatchListService;
import org.scada_lts.permissions.service.GetWatchListsWithAccess;
import org.scada_lts.web.mvc.api.json.JsonDataPointOrder;
import org.scada_lts.web.mvc.api.json.JsonWatchList;
import org.scada_lts.web.mvc.api.json.JsonWatchListForUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
 * Updated by:
 * @author Radoslaw Jajko rjajko@softq.pl
 * @version 2.0.0
 */
@Controller
@RequestMapping(path = "/api/watch-lists")
public class WatchListAPI {
	
	private static final Log LOG = LogFactory.getLog(WatchListAPI.class);
	private static final String LOG_PREFIX = "/api/watch-lists";

	@Resource
	private WatchListService watchListService;
	
	@Resource
	private PointValueService pointValueService;
	
	@Resource
	private DataPointService dataPointService;

	/**
	 * Get WatchList brief list
	 *
	 * @param request User request
	 * @return List of ScadaObjectIdentifier
	 */
	@GetMapping(value = "")
	public ResponseEntity<List<ScadaObjectIdentifier>> getWatchLists(HttpServletRequest request) {
		LOG.info("GET:" + LOG_PREFIX);
		try {
			User user = Common.getUser(request);
			if(user != null) {
				if(user.isAdmin()) {
					List<WatchList> watchListList = watchListService.getWatchLists();
					List<ScadaObjectIdentifier> response = watchListList.stream()
							.map(watchList -> watchList.toIdentifier())
							.collect(Collectors.toList());
					return new ResponseEntity<>(response, HttpStatus.OK);
				} else {
					List<ScadaObjectIdentifier> response = new GetWatchListsWithAccess().getObjectIdentifiersWithAccess(user);
					return new ResponseEntity<>(response, HttpStatus.OK);
				}
			} else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
		} catch (Exception e) {
		    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}

	/**
	 * Get specific Watch List by ID
	 *
	 * @param id Id of the WatchList
	 * @param request User request
	 * @return JsonWatchList object
	 */
	@GetMapping(value = "/{id}")
    public ResponseEntity<JsonWatchListForUser> getWatchListById(@PathVariable("id") int id, HttpServletRequest request) {
        LOG.info("GET:" + LOG_PREFIX + "/" + id);
        try {
            User user = Common.getUser(request);
            if(user != null) {
				WatchList watchList = watchListService.getWatchList(id);
				if(watchList == null)
					return new ResponseEntity<>(HttpStatus.NOT_FOUND);
				watchListService.populateWatchlistData(watchList);
				if(watchList.getUserAccess(user) < ShareUser.ACCESS_READ)
					return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
				return new ResponseEntity<>(new JsonWatchListForUser(watchList, user), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

	@GetMapping(value = "/generateXid")
	public ResponseEntity<String> getUniqueXid(HttpServletRequest request) {
		try {
			User user = Common.getUser(request);
			if(user != null) {
				return new ResponseEntity<>(watchListService.generateUniqueXid(), HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/validate")
	public ResponseEntity<Map<String, Object>> isXidUnique(
			@RequestParam String xid,
			@RequestParam Integer id,
			HttpServletRequest request) {
		try {
			User user = Common.getUser(request);
			if(user != null) {
				Map<String, Object> response = new HashMap<>();
				response.put("unique", watchListService.isXidUnique(xid, id));
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/order/{id}")
	public ResponseEntity<Map<Integer, Integer>> getPointOrder(
			@PathVariable("id") int id,
			HttpServletRequest request) {
		try {
			User user = Common.getUser(request);
			if(user != null) {
				return new ResponseEntity<>(watchListService.getDataPointOrder(id).getPointIds(), HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping(value = "/order/")
	public ResponseEntity<String> getPointOrder(
			@RequestBody JsonDataPointOrder orderData,
			HttpServletRequest request) {
		try {
			User user = Common.getUser(request);
			if(user != null) {
				watchListService.setDataPointOrder(orderData);
				return new ResponseEntity<>(HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

    @PostMapping(value = "")
	public ResponseEntity<JsonWatchList> createWatchList(
			@RequestBody JsonWatchList jsonWatchList,
			HttpServletRequest request
	) {
		try {
			User user = Common.getUser(request);
			if(user != null) {
				WatchList wl = jsonWatchList.createWatchList();
				wl.setUserId(user.getId());
				watchListService.saveWatchList(wl);
				return new ResponseEntity<>(new JsonWatchList(wl), HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping(value = "")
	public ResponseEntity<JsonWatchList> updateWatchList(
			@RequestBody JsonWatchList jsonWatchList,
			HttpServletRequest request
	) {
		try {
			User user = Common.getUser(request);
			if(user != null) {
				WatchList fromBase = watchListService.getWatchList(jsonWatchList.getId());
				if(fromBase == null)
					return new ResponseEntity<>(HttpStatus.NOT_FOUND);
				watchListService.populateWatchlistData(fromBase);
				if(fromBase.getUserAccess(user) < ShareUser.ACCESS_READ)
					return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
				WatchList wl = jsonWatchList.createWatchList();
				watchListService.saveWatchList(wl);
				return new ResponseEntity<>(new JsonWatchList(wl), HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<String> deleteWatchList(
			@PathVariable(value = "id", required = true) Integer watchListId,
			HttpServletRequest request
	) {
		try {
			User user = Common.getUser(request);
			if(user != null) {
				WatchList fromBase = watchListService.getWatchList(watchListId);
				if(fromBase == null)
					return new ResponseEntity<>(HttpStatus.NOT_FOUND);
				watchListService.populateWatchlistData(fromBase);
				if(fromBase.getUserAccess(user) < ShareUser.ACCESS_SET)
					return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
				watchListService.deleteWatchList(fromBase.getId());
				return new ResponseEntity<>(HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Get specific Watch List by Export ID
	 *
	 * @param xid Id of the WatchList
	 * @param request User request
	 * @return JsonWatchList object
	 */
	@GetMapping(value = "/xid/{xid}")
	public ResponseEntity<JsonWatchList> getWatchListByXid(@PathVariable(value = "xid", required = true) String xid, HttpServletRequest request) {
		LOG.info("GET:" + LOG_PREFIX + "/xid/" + xid);
		try {
			User user = Common.getUser(request);
			if(user != null) {
				WatchList watchList = watchListService.getWatchList(xid);
				if(watchList == null)
					return new ResponseEntity<>(HttpStatus.NOT_FOUND);
				watchListService.populateWatchlistData(watchList);
				if(watchList.getUserAccess(user) < ShareUser.ACCESS_READ)
					return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
				return new ResponseEntity<>(getWatchList(watchList), HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

    private JsonWatchList getWatchList(WatchList watchList) {
		watchListService.populateWatchlistData(watchList);
		List<ScadaObjectIdentifier> pointList = new ArrayList<>();
		watchList.getPointList().forEach(p -> {
			pointList.add(new ScadaObjectIdentifier(p.getId(), p.getXid(), p.getName()));
		});
		return new JsonWatchList(
				watchList.getId(),
				watchList.getXid(),
				watchList.getName(),
				watchList.getUserId(),
				pointList,
				watchList.getWatchListUsers()
		);
	}

	@RequestMapping(value = "/api/watchlist/getNames", method = RequestMethod.GET)
	public ResponseEntity<String> getNames(HttpServletRequest request) {
		LOG.info("/api/watchlist/getNames");
		try {
			User user = Common.getUser(request);
		
			if (user != null) {
				
				class WatchListJSON implements Serializable{
					private String xid;
					private String name;
					WatchListJSON(String xid,String name) {
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

				List<WatchList> lstWL;
				if (user.isAdmin()) {
					lstWL = watchListService.getWatchLists();
				} else {
					lstWL = new GetWatchListsWithAccess().getObjectsWithAccess(user);
				}				
				
				List<WatchListJSON> lst = new ArrayList<WatchListJSON>();
				for (WatchList wl:lstWL) {
					WatchListJSON wlJ = new WatchListJSON(wl.getXid(), wl.getName());
					lst.add(wlJ);
				}
				
				String json = null;
				ObjectMapper mapper = new ObjectMapper();
				json = mapper.writeValueAsString(lst);
				
				return new ResponseEntity<String>(json,HttpStatus.OK);				
			} 
			
			return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
			
		} catch (Exception e) {
			LOG.error(e);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}
	
	
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

	/**
	 * 
	 * @param xid
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getPoints/{xid}", method = RequestMethod.GET)
	public ResponseEntity<String> getPoints(@PathVariable("xid") String xid, HttpServletRequest request) {
		
		LOG.info("/api/watchlist/getPoints/{xid} xid:"+xid);
		
		try {
			User user = Common.getUser(request);
			if (user != null) {
			
				WatchList watchList = watchListService.getWatchList(xid);
				if(watchList == null)
					return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
				watchListService.populateWatchlistData(watchList);
				if(watchList.getUserAccess(user) < ShareUser.ACCESS_READ)
					return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
				List<PointJSON> lst = new ArrayList<PointJSON>();
			
				for (DataPointVO dpvo : watchList.getPointList()){
					PointJSON p = new PointJSON(dpvo.getXid(), dpvo.getName());
					lst.add(p);
				}
				
				String json = null;
				ObjectMapper mapper = new ObjectMapper();
			
				json = mapper.writeValueAsString(lst);
				
			
				return new ResponseEntity<String>(json,HttpStatus.OK);
			}
			
			return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
			
		} catch (Exception e) {
			LOG.error(e);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/getChartData/{xid}/{fromData}/{toData}", method = RequestMethod.GET)
	public ResponseEntity<String> getChartData(@PathVariable("xid") String xid, @PathVariable("fromData") Long fromData, @PathVariable("toData") Long toData, HttpServletRequest request) {
		
		LOG.info("/api/watchlist/getChartData/{xid}/{fromData}/{toData} xid:"+xid+" fromData:"+fromData+" toData:"+toData);
		
		try {
			User user = Common.getUser(request);
			if (user != null) {
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
			
				DataPointVO dp = dataPointService.getDataPoint(xid);
				if(!Permissions.hasDataPointReadPermission(user, dp))
					return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
				List<PointValueTime> listValues = pointValueService.getPointValuesBetween(dp.getId(), fromData, toData);
				
				List<ValueToJSON> values = new ArrayList<ValueToJSON>();
				
				for (PointValueTime pvt : listValues) {
					ValueToJSON v = new ValueToJSON();
					v.set(pvt, dp);
					values.add(v);
				}
			
				DataChartJSON dataChartJSON = new DataChartJSON(xid, dp.getName(), values);
				
				String json = null;
				ObjectMapper mapper = new ObjectMapper();
				
				json = mapper.writeValueAsString(dataChartJSON);
				
				return new ResponseEntity<String>(json,HttpStatus.OK);
			}
			
			return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
		
		} catch (Exception e) {
			LOG.error(e);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
		
		
	}

}
