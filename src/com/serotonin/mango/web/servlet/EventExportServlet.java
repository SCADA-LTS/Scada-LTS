package com.serotonin.mango.web.servlet;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.EventDao;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.report.EventCsvStreamer;
import com.serotonin.mango.web.dwr.beans.EventExportDefinition;

public class EventExportServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = Common.getUser(request);
        if (user == null)
            return;

        EventExportDefinition def = user.getEventExportDefinition();
        if (def == null)
            return;

        final ResourceBundle bundle = Common.getBundle();
        List<EventInstance> events = new EventDao().search(def.getEventId(), def.getEventSourceType(), def.getStatus(),
                def.getAlarmLevel(), def.getKeywords(), def.getDateFrom(), def.getDateTo(), user.getId(), bundle, 0,
                Integer.MAX_VALUE, null);

        // Stream the content.
        response.setContentType("text/csv");
        new EventCsvStreamer(response.getWriter(), events, bundle);
    }
}
