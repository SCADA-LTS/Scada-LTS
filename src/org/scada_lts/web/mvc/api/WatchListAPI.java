package org.scada_lts.web.mvc.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.PointValueService;
import org.scada_lts.mango.service.WatchListService;
import org.scada_lts.web.mvc.api.json.JsonDataPointOrder;
import org.scada_lts.web.mvc.api.json.JsonWatchList;
import org.scada_lts.web.mvc.api.json.JsonWatchListForUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.WatchList;

import static org.scada_lts.permissions.service.GetDataPointsWithAccess.filteringByAccess;
import static org.scada_lts.permissions.service.GetDataPointsWithAccess.hasDataPointReadPermission;
import static org.scada_lts.permissions.service.GetWatchListsWithAccess.*;
import static org.scada_lts.utils.WatchListApiUtils.*;

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

	private final WatchListService watchListService;
	private final PointValueService pointValueService;
	private final DataPointService dataPointService;

	public WatchListAPI() {
		this.watchListService = new WatchListService();
		this.pointValueService = new PointValueService();
		this.dataPointService = new DataPointService();
	}

	public WatchListAPI(WatchListService watchListService,
						PointValueService pointValueService,
						DataPointService dataPointService) {
		this.watchListService = watchListService;
		this.pointValueService = pointValueService;
		this.dataPointService = dataPointService;
	}

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
							.map(WatchList::toIdentifier)
							.collect(Collectors.toList());
					return new ResponseEntity<>(response, HttpStatus.OK);
				} else {
					List<ScadaObjectIdentifier> response = watchListService.getWatchListIdentifiersWithAccess(user);
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
				if(!hasWatchListReadPermission(user, watchList))
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
				WatchList watchList = watchListService.getWatchList(id);
				if(watchList == null)
					return new ResponseEntity<>(HttpStatus.NOT_FOUND);
				watchListService.populateWatchlistData(watchList);
				if(!hasWatchListReadPermission(user, watchList))
					return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
				Map<Integer, Integer> result = getPointIds(user, watchList);
				return new ResponseEntity<>(result, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping(value = "/order/")
	public ResponseEntity<String> updatePointOrder(
			@RequestBody JsonDataPointOrder orderData,
			HttpServletRequest request) {
		try {
			User user = Common.getUser(request);
			if(user != null) {
				WatchList watchList = watchListService.getWatchList(orderData.getWatchListId());
				if(watchList == null)
					return new ResponseEntity<>(HttpStatus.NOT_FOUND);
				watchListService.populateWatchlistData(watchList);
				if(!hasWatchListSetPermission(user, watchList))
					return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
				watchList.setPointList(toDataPointsOrdered(orderData, watchList));
				watchListService.addPointsForWatchList(watchList);
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
				WatchList watchList = jsonWatchList.createWatchList();
				watchList.setUserId(user.getId());
				watchListService.saveWatchList(watchList);
				return new ResponseEntity<>(new JsonWatchList(watchList), HttpStatus.OK);
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
				if(!hasWatchListSetPermission(user, fromBase))
					return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
				WatchList watchListToSave = getWatchListToSave(jsonWatchList, user, fromBase);
				watchListService.saveWatchList(watchListToSave);
				return new ResponseEntity<>(new JsonWatchList(getWatchListToRead(watchListToSave, user)), HttpStatus.OK);
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
				if(!hasWatchListOwnerPermission(user, fromBase))
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
				if(!hasWatchListReadPermission(user, watchList))
					return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
				return new ResponseEntity<>(toJsonWatchList(user, watchList), HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/api/watchlist/getNames")
	public ResponseEntity<List<ScadaObjectIdentifier>> getNames(HttpServletRequest request) {
		LOG.info("/api/watchlist/getNames");
		try {
			User user = Common.getUser(request);
		
			if (user != null) {
				List<ScadaObjectIdentifier> result = getWatchListIdentifiers(user, watchListService);
				return new ResponseEntity<>(result,HttpStatus.OK);
			} 
			
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			
		} catch (Exception e) {
			LOG.error(e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * 
	 * @param xid
	 * @param request
	 * @return
	 */
	@GetMapping(value = "/getPoints/{xid}")
	public ResponseEntity<List<ScadaObjectIdentifier>> getPoints(@PathVariable("xid") String xid, HttpServletRequest request) {
		
		LOG.info("/api/watchlist/getPoints/{xid} xid:"+xid);
		
		try {
			User user = Common.getUser(request);
			if (user != null) {
				WatchList watchList = watchListService.getWatchList(xid);
				if(watchList == null)
					return new ResponseEntity<>(HttpStatus.NOT_FOUND);
				watchListService.populateWatchlistData(watchList);
				if(!hasWatchListReadPermission(user, watchList))
					return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
				List<ScadaObjectIdentifier> result = filteringByAccess(user, watchList.getPointList())
						.stream()
						.map(DataPointVO::toIdentifier)
						.collect(Collectors.toList());
				return new ResponseEntity<>(result, HttpStatus.OK);
			}
			
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			
		} catch (Exception e) {
			LOG.error(e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping(value = "/getChartData/{xid}/{fromData}/{toData}")
	public ResponseEntity<DataChartJSON> getChartData(@PathVariable("xid") String xid, @PathVariable("fromData") Long fromData, @PathVariable("toData") Long toData, HttpServletRequest request) {
		
		LOG.info("/api/watchlist/getChartData/{xid}/{fromData}/{toData} xid:"+xid+" fromData:"+fromData+" toData:"+toData);
		
		try {
			User user = Common.getUser(request);
			if (user != null) {
				DataPointVO point = dataPointService.getDataPoint(xid);
				if(point == null)
					return new ResponseEntity<>(HttpStatus.NOT_FOUND);
				if(!hasDataPointReadPermission(user, point))
					return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
				List<ValueToJSON> values = pointValueService.getPointValuesBetween(point.getId(), fromData, toData)
						.stream()
						.map(value -> ValueToJSON.newInstance(value, point))
						.collect(Collectors.toList());
				return new ResponseEntity<>(new DataChartJSON(xid, point.getName(), values),HttpStatus.OK);
			}
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		
		} catch (Exception e) {
			LOG.error(e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	private static class DataChartJSON implements Serializable{
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
}
