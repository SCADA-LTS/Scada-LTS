package org.scada_lts.web.mvc.api.dto.view.components;

import com.serotonin.mango.vo.User;

public interface ViewComponentCreate<T> {
    T createFromBody(User user);
}
