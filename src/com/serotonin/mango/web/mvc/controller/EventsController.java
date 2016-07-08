/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2009 Serotonin Software Technologies Inc.
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

import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.BindException;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.EventDao;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.web.comparators.EventInstanceComparator;
import com.serotonin.web.util.PaginatedData;
import com.serotonin.web.util.PaginatedListController;
import com.serotonin.web.util.PagingDataForm;

public class EventsController extends PaginatedListController {
    @SuppressWarnings("unchecked")
    @Override
    protected PaginatedData getData(HttpServletRequest request, PagingDataForm paging, BindException errors)
            throws Exception {
//        List<EventInstance> data;
        ResourceBundle bundle = ControllerUtils.getResourceBundle(request);
//        if (((EventsForm)paging).isInactive()) {
//            User user = Common.getUser(request);
//            List<EventInstance> allInactive = new EventDao().getInactiveEvents();
//            data = new ArrayList<EventInstance>();
//            for (EventInstance evt : allInactive) {
//                if (evt.isAlarm() && Permissions.hasEventTypePermission(user, evt.getEventType()))
//                    data.add(evt);
//            }
//        }
//        else
//            data = Common.ctx.getEventManager().getActiveEvents(Common.getUser(request));
        List<EventInstance> data = new EventDao().getPendingEvents(Common.getUser(request).getId());
        sortData(bundle, data, paging);
        return new PaginatedData<EventInstance>(data, data.size());
    }
    
    @SuppressWarnings("unchecked")
    private void sortData(ResourceBundle bundle, List<EventInstance> data, final PagingDataForm paging) {
        EventInstanceComparator comp = new EventInstanceComparator(bundle, paging.getSortField(), paging.getSortDesc());
        if (!comp.canSort())
            return;
        Collections.sort(data, comp);
    }
}
