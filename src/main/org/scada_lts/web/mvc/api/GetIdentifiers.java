package org.scada_lts.web.mvc.api;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface GetIdentifiers<I> {
    List<I> getIdentifiers(HttpServletRequest request);
}
