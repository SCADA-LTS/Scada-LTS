package org.scada_lts.web.beans;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

class GetApplicationContext implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        GetApplicationContext.applicationContext = applicationContext;
    }

    static ApplicationContext context() {
        return applicationContext;
    }
}
