package org.scada_lts.web.beans;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

class GetServletBeans implements ApplicationContextAware {

    private static ApplicationContext servletContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        GetServletBeans.servletContext = applicationContext;
    }

    static ApplicationContext context() {
        return servletContext;
    }
}
