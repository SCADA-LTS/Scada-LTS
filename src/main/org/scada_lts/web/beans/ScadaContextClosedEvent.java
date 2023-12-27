package org.scada_lts.web.beans;

import com.serotonin.mango.MangoContextListener;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContextEvent;

@Component
public class ScadaContextClosedEvent implements ApplicationListener<ContextClosedEvent> {

    private final MangoContextListener mangoContextListener;

    public ScadaContextClosedEvent(MangoContextListener mangoContextListener) {
        this.mangoContextListener = mangoContextListener;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        WebApplicationContext webApplicationContext = (WebApplicationContext)contextClosedEvent.getSource();
        ServletContextEvent servletContextEvent = new ServletContextEvent(webApplicationContext.getServletContext());
        mangoContextListener.contextDestroyed(servletContextEvent);
    }
}
