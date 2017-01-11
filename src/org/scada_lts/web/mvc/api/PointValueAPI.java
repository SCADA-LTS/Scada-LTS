package org.scada_lts.web.mvc.api;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.PointValueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.AlphanumericValue;
import com.serotonin.mango.rt.dataImage.types.BinaryValue;
import com.serotonin.mango.rt.dataImage.types.ImageValue;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataImage.types.MultistateValue;
import com.serotonin.mango.rt.dataImage.types.NumericValue;
import com.serotonin.mango.view.text.TextRenderer;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;


class ValueToJSON implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String value;
	private Long ts;
	private String name;
	private String xid;
	private String type;
	private TextRenderer textRenderer;
	private String chartColour;

	void set(PointValueTime pvt, DataPointVO dpvo) {
		setValue(pvt.getValue());
		setTs(pvt.getTime());
		setName(dpvo.getName());
		setXid(dpvo.getXid());
		setTextRenderer(dpvo.getTextRenderer());
		setChartColour(dpvo.getChartColour());
	}

	public String getValue() {
		return value;
	}

	public void setValue(MangoValue value) {
		if (value instanceof AlphanumericValue) {
			this.value = ((AlphanumericValue) value).getStringValue();
			setType("AlphanumericValue");
		} else if (value instanceof BinaryValue) {
			this.value = String.valueOf(((BinaryValue) value).getBooleanValue());
			setType("BinaryValue");
		} else if (value instanceof ImageValue) {
			this.value = ((ImageValue) value).getFilename();
			setType("ImageValue");
		} else if (value instanceof MultistateValue) {
			this.value = String.valueOf(((MultistateValue) value).getIntegerValue());
			setType("MultistateValue");
		} else if (value instanceof NumericValue) {
			this.value = String.valueOf(((NumericValue) value).getFloatValue());
			setType("NumericValue");
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the textRenderer
	 */
	public TextRenderer getTextRenderer() {
		return textRenderer;
	}

	/**
	 * @param textRenderer the textRenderer to set
	 */
	public void setTextRenderer(TextRenderer textRenderer) {
		this.textRenderer = textRenderer;
	}

	/**
	 * @return the chartColour
	 */
	public String getChartColour() {
		return chartColour;
	}

	/**
	 * @param chartColour the chartColour to set
	 */
	public void setChartColour(String chartColour) {
		this.chartColour = chartColour;
	}
}


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
	 * @param xid
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/api/point_value/getValue/{xid}", method = RequestMethod.GET)
	public ResponseEntity<String> getValue(@PathVariable("xid") String xid, HttpServletRequest request) {
		LOG.info("/api/point_value/getValue/{xid} id:"+xid);
		
		try {
			User user = Common.getUser(request);
			DataPointVO dpvo = dataPointService.getDataPoint(xid);
			if (user != null) {
				PointValueTime pvt = pointValueService.getLatestPointValue(dpvo.getId());
				String json = null;
				ObjectMapper mapper = new ObjectMapper();
				
				ValueToJSON v = new ValueToJSON();
				v.set(pvt, dpvo);

				json = mapper.writeValueAsString(v);
				
				return new ResponseEntity<String>(json,HttpStatus.OK);
			}
			
			return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
			
		} catch (Exception e) {
			LOG.error(e);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}
	
}

