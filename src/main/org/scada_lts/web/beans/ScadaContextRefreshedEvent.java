package org.scada_lts.web.beans;

import com.serotonin.mango.MangoContextListener;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContextEvent;

@Component
public class ScadaContextRefreshedEvent implements ApplicationListener<ContextRefreshedEvent> {

    private final MangoContextListener mangoContextListener;

    public ScadaContextRefreshedEvent(MangoContextListener mangoContextListener) {
        this.mangoContextListener = mangoContextListener;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        WebApplicationContext webApplicationContext = (WebApplicationContext)contextRefreshedEvent.getSource();
        ServletContextEvent servletContextEvent = new ServletContextEvent(webApplicationContext.getServletContext());
        mangoContextListener.contextInitialized(servletContextEvent);
    }
}
