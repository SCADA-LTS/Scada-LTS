package org.scada_lts.web.mvc.api;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface CrudService<T> {
    T create(HttpServletRequest request, T object);
    T read(HttpServletRequest request, String xid, Integer id);
    List<T> readAll(HttpServletRequest request);
    T update(HttpServletRequest request, T object);
    T delete(HttpServletRequest request, String xid, Integer id);
}
