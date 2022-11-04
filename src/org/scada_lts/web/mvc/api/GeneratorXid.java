package org.scada_lts.web.mvc.api;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface GeneratorXid {
    Map<String, Object> isUniqueXid(HttpServletRequest request, String object, Integer id);
    String generateUniqueXid(HttpServletRequest request);
}
