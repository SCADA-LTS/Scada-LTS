package org.scada_lts.web.mvc.api;

import javax.servlet.http.HttpServletRequest;

public interface GeneratorXid {
    boolean isUniqueXid(HttpServletRequest request, String object, Integer id);
    String generateUniqueXid(HttpServletRequest request);
}
