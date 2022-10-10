package org.scada_lts.web.mvc.api;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface ObjectApiService<T, I> {

    Map<String, Object> isUniqueXid(HttpServletRequest request, String xid, Integer id);
    String generateUniqueXid(HttpServletRequest request);
    T create(HttpServletRequest request, T datapoint);
    T update(HttpServletRequest request, T datapoint);
    T delete(HttpServletRequest request, String xid, Integer id);
    List<I> getIdentifiers(HttpServletRequest request);
}
