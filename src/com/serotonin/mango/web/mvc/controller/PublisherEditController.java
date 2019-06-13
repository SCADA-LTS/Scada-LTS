/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.web.mvc.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.EventDao;
import com.serotonin.mango.db.dao.PublisherDao;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.mango.vo.publish.PublishedPointVO;
import com.serotonin.mango.vo.publish.PublisherVO;
import com.serotonin.mango.web.dwr.beans.EventInstanceBean;
import com.serotonin.web.taglib.DateFunctions;

/**
 * @author Matthew Lohbihler
 */
public class PublisherEditController extends ParameterizableViewController {
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        User user = Common.getUser(request);
        Permissions.ensureAdmin(user);

        PublisherVO<? extends PublishedPointVO> publisherVO;

        // Get the id.
        String idStr = request.getParameter("pid");
        if (idStr == null) {
            // Adding a new data source? Get the type id.
            int typeId = Integer.parseInt(request.getParameter("typeId"));

            // A new publisher
            publisherVO = PublisherVO.createPublisherVO(typeId);
            publisherVO.setXid(new PublisherDao().generateUniqueXid());
        }
        else {
            // An existing configuration.
            int id = Integer.parseInt(idStr);

            publisherVO = Common.ctx.getRuntimeManager().getPublisher(id);
            if (publisherVO == null)
                throw new ShouldNeverHappenException("Publisher not found with id " + id);
        }

        // Set the id of the data source in the user object for the DWR.
        user.setEditPublisher(publisherVO);

        // Create the model.
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("publisher", publisherVO);
        if (publisherVO.getId() != Common.NEW_ID) {
            List<EventInstance> events = new EventDao().getPendingEventsForPublisher(publisherVO.getId(), user.getId());
            List<EventInstanceBean> beans = new ArrayList<EventInstanceBean>();
            if (events != null) {
                ResourceBundle bundle = ControllerUtils.getResourceBundle(request);
                for (EventInstance event : events)
                    beans.add(new EventInstanceBean(event.isActive(), event.getAlarmLevel(), DateFunctions
                            .getTime(event.getActiveTimestamp()), event.getMessage().getLocalizedMessage(bundle)));
            }
            model.put("publisherEvents", beans);
        }

        return new ModelAndView(getViewName(), model);
    }
}
