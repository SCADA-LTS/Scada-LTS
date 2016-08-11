/*
 * (c) 2016 Abil'I.T. http://abilit.eu/
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.scada_lts.web.mvc.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.web.mvc.comparators.DataSourceComparator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.vo.DataPointNameComparator;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.ListParent;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.mango.web.mvc.controller.ControllerUtils;
import com.serotonin.web.util.PaginatedData;
import com.serotonin.web.util.PagingDataForm;

/**
 * Controller for data source's list
 * Based on DataSourceListController from Mango by Matthew Lohbihler
 * 
 * @author Marcin Gołda
 */
@Controller
@RequestMapping("/data_sources.shtm")
public class DataSourceListController {
	private static final Log LOG = LogFactory.getLog(SqlController.class);

	@RequestMapping(method = RequestMethod.GET)
	protected ModelAndView showList(HttpServletRequest request){
		LOG.trace("/data_sources.shtm");
		
		PagingDataForm paging = new PagingDataForm();
		PaginatedData data = getData(request, paging);
        paging.setData(data.getData());
        paging.setNumberOfItems(data.getRowCount());
        
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("paging", paging);
		return new ModelAndView("dataSourceList", model);
	}
	
    protected PaginatedData getData(HttpServletRequest request, PagingDataForm paging) {
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
}