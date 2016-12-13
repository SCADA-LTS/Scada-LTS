package org.scada_lts.web.mvc.api;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.PointValueService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.AlphanumericValue;
import com.serotonin.mango.rt.dataImage.types.BinaryValue;
import com.serotonin.mango.rt.dataImage.types.ImageValue;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataImage.types.MultistateValue;
import com.serotonin.mango.rt.dataImage.types.NumericValue;
import com.serotonin.mango.vo.DataPointVO;

/**
 * Controller for API pointValue
 * 
 * @author Grzesiek Bylica grzegorz.bylica@gmail.com
 */
@Controller 
public class PointValueAPI {
	
	private static final Log LOG = LogFactory.getLog(PointValueAPI.class);
	
	private DataPointService dataPointService = new DataPointService();
	
	@Resource
	private PointValueService pointValueService;
	
	
	
		
	/**
	 * 
	 * @param xid
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/api/points/getValue/{xid}", method = RequestMethod.GET)
	public @ResponseBody String getWatchListNames(@PathVariable("xid") String xid, HttpServletRequest request) {
		LOG.info("/api/points/getValue/{xid} id:"+xid);
		
		// check may use watch list
		//User user = Common.getUser(request);
		
		DataPointVO dpvo = dataPointService.getDataPoint(xid);
		
		
		PointValueTime pvt = pointValueService.getLatestPointValue(dpvo.getId());
		String json = null;
		ObjectMapper mapper = new ObjectMapper();
		
		class ValueToJson implements Serializable {
			
			private static final long serialVersionUID = 1L;
			
			private String value;
			private Long ts;
			private String name;
			private String xid;

  void set(PointValueTime pvt, DataPointVO dpvo) {
				setValue(pvt.getValue());
				setTs(pvt.getTime());
				setName(dpvo.getName());
				setXid(dpvo.getXid());
			}

			public String getValue() {
				return value;
			}

			public void setValue(MangoValue value) {
				if (value instanceof AlphanumericValue) {
					this.value = ((AlphanumericValue) value).getStringValue();
				} else if (value instanceof BinaryValue) {
					this.value = String.valueOf(((BinaryValue) value).getBooleanValue());
				} else if (value instanceof ImageValue) {
					this.value = ((ImageValue) value).getFilename();
				} else if (value instanceof MultistateValue) {
					this.value = String.valueOf(((MultistateValue) value).getIntegerValue());
				} else if (value instanceof NumericValue) {
					this.value = String.valueOf(((NumericValue) value).getFloatValue());
				}
				//error
			}

			public Long getTs() {
				return ts;
			}

			public void setTs(Long ts) {
				this.ts = ts;
			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}
			public String getXid() {
				return xid;
			}

			public void setXid(String xid) {
				this.xid = xid;
			}
		}

		ValueToJson v = new ValueToJson();
		v.set(pvt, dpvo);
		
		try {
			//TODO Checking that the all values types are casted to String.
			json = mapper.writeValueAsString(v);
		} catch (JsonProcessingException e) {
			LOG.error(e);
		}
		return json;
	}
}

