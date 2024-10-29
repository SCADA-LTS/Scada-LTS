package org.scada_lts.web.mvc.api;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Deprecated(since = "2.8.0")
public class TranslateAPI {
	
	private static final Log LOG = LogFactory.getLog(TranslateAPI.class);
	
	@Resource
	MessageSource msgSource;
	
	@RequestMapping(value = "/api/translate/{name}", method = RequestMethod.GET)
	public ResponseEntity<String> createFolder(@PathVariable("name") String name, HttpServletRequest request) {
		
		LOG.info("translate:"+name);
		/*String json = null;
		ObjectMapper mapper = new ObjectMapper();
		TranslateJSON er = new ViewError();
		er.setMessage(error.getLocalizedMessage());
		json = mapper.writeValueAsString(er);
		return new ResponseEntity<String>(json,HttpStatus.BAD_REQUEST);
		*/
		return null;
		
	}

}
