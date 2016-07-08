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
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.BindException;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.vo.DataPointNameComparator;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.ListParent;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.mango.web.comparators.BaseComparator;
import com.serotonin.web.i18n.I18NUtils;
import com.serotonin.web.util.PaginatedData;
import com.serotonin.web.util.PaginatedListController;
import com.serotonin.web.util.PagingDataForm;

public class DataSourceListController extends PaginatedListController {
    @Override
    protected PaginatedData getData(HttpServletRequest request, PagingDataForm paging, BindException errors)
            throws Exception {
        User user = Common.getUser(request);
        DataPointDao dataPointDao = new DataPointDao();

        List<DataSourceVO<?>> data = Common.ctx.getRuntimeManager().getDataSources();
        List<ListParent<DataSourceVO<?>, DataPointVO>> dataSources = new ArrayList<ListParent<DataSourceVO<?>, DataPointVO>>();
        ListParent<DataSourceVO<?>, DataPointVO> listParent;
        for (DataSourceVO<?> ds : data) {
            if (Permissions.hasDataSourcePermission(user, ds.getId())) {
                listParent = new ListParent<DataSourceVO<?>, DataPointVO>();
                listParent.setParent(ds);
                listParent.setList(dataPointDao.getDataPoints(ds.getId(), DataPointNameComparator.instance));
                dataSources.add(listParent);
            }
        }

        sortData(ControllerUtils.getResourceBundle(request), dataSources, paging);

        return new PaginatedData<ListParent<DataSourceVO<?>, DataPointVO>>(dataSources, data.size());
    }

    private void sortData(ResourceBundle bundle, List<ListParent<DataSourceVO<?>, DataPointVO>> data,
            final PagingDataForm paging) {
        DataSourceComparator comp = new DataSourceComparator(bundle, paging.getSortField(), paging.getSortDesc());
        if (!comp.canSort())
            return;
        Collections.sort(data, comp);
    }

    @Override
    protected Object getCommand(HttpServletRequest request) {
        PagingDataForm form = new PagingDataForm();
        form.setItemsPerPage(20);
        form.setSortField("name");
        return form;
    }

    class DataSourceComparator extends BaseComparator<ListParent<DataSourceVO<?>, DataPointVO>> {
        private static final int SORT_NAME = 1;
        private static final int SORT_TYPE = 2;
        private static final int SORT_CONN = 3;
        private static final int SORT_ENABLED = 4;

        private final ResourceBundle bundle;

        public DataSourceComparator(ResourceBundle bundle, String sortField, boolean descending) {
            this.bundle = bundle;
            if ("name".equals(sortField))
                sortType = SORT_NAME;
            else if ("type".equals(sortField))
                sortType = SORT_TYPE;
            else if ("conn".equals(sortField))
                sortType = SORT_TYPE;
            else if ("enabled".equals(sortField))
                sortType = SORT_ENABLED;
            this.descending = descending;
        }

        public int compare(ListParent<DataSourceVO<?>, DataPointVO> o1, ListParent<DataSourceVO<?>, DataPointVO> o2) {
            DataSourceVO<?> ds1 = o1.getParent();
            DataSourceVO<?> ds2 = o2.getParent();

            int result = 0;
            if (sortType == SORT_NAME)
                result = ds1.getName().compareToIgnoreCase(ds2.getName());
            else if (sortType == SORT_TYPE) {
                String desc1 = I18NUtils.getMessage(bundle, ds1.getType().getKey());
                String desc2 = I18NUtils.getMessage(bundle, ds2.getType().getKey());
                result = desc1.compareToIgnoreCase(desc2);
            }
            else if (sortType == SORT_CONN) {
                String desc1 = ds1.getConnectionDescription().getLocalizedMessage(bundle);
                String desc2 = ds2.getConnectionDescription().getLocalizedMessage(bundle);
                result = desc1.compareToIgnoreCase(desc2);
            }
            else if (sortType == SORT_ENABLED)
                result = new Boolean(ds1.isEnabled()).compareTo(new Boolean(ds2.isEnabled()));

            if (descending)
                return -result;
            return result;
        }
    }

}
