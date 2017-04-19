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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.WebUtils;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.view.chart.BaseChartRenderer;
import com.serotonin.mango.view.text.BaseTextRenderer;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.DataPointSaveHandler;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.mango.web.mvc.controller.ControllerUtils;
import com.serotonin.propertyEditor.DecimalFormatEditor;
import com.serotonin.propertyEditor.IntegerFormatEditor;
import com.serotonin.util.StringUtils;

/**
 * Controller for data point edition
 * Based on DataPointEditController from Mango by Matthew Lohbihler
 * 
 * @author Marcin Gołda
 */
@Controller
@RequestMapping("/data_point_edit.shtm") 
public class DataPointEditController {
	private static final Log LOG = LogFactory.getLog(LoginController.class);
	
	DataPointDao dataPointDao;
	
    public static final String SUBMIT_SAVE = "save";
    public static final String SUBMIT_DISABLE = "disable";
    public static final String SUBMIT_ENABLE = "enable";
    public static final String SUBMIT_RESTART = "restart";
    
	@InitBinder("dataPointVO")
	protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Double.TYPE, "tolerance", new DecimalFormatEditor(new DecimalFormat("#.##"), false));
        binder.registerCustomEditor(Integer.TYPE, "purgePeriod", new IntegerFormatEditor(new DecimalFormat("#"), false));
        binder.registerCustomEditor(Double.TYPE, "discardLowLimit", new DecimalFormatEditor(new DecimalFormat("#.##"),
                false));
        binder.registerCustomEditor(Double.TYPE, "discardHighLimit", new DecimalFormatEditor(new DecimalFormat("#.##"),
                false));
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(HttpServletRequest request, Model model){
		LOG.trace("/data_point_edit.shtm");
		
        User user = Common.getUser(request);
        dataPointDao = new DataPointDao();
        int id;
        String idStr = request.getParameter("dpid");
        if (idStr == null) {
            String pedStr = request.getParameter("pedid");
            if (pedStr == null)
                throw new ShouldNeverHappenException("dpid or pedid must be provided for this page");

            int pedid = Integer.parseInt(pedStr);
            id = dataPointDao.getDataPointIdFromDetectorId(pedid);
        }
        else
            id = Integer.parseInt(idStr);

        DataPointVO dataPoint = dataPointDao.getDataPoint(id);
        user.setEditPoint(dataPoint);
        
        Permissions.ensureDataSourcePermission(user, dataPoint.getDataSourceId());
        ControllerUtils.addPointListDataToModel(user, id, model);
        model.addAttribute("form", dataPoint);
		model.addAttribute("dataSource", Common.ctx.getRuntimeManager().getDataSource(dataPoint.getDataSourceId()));
		model.addAttribute("textRenderers", BaseTextRenderer.getImplementation(dataPoint.getPointLocator().getDataTypeId()));
		model.addAttribute("chartRenderers", BaseChartRenderer.getImplementations(dataPoint.getPointLocator().getDataTypeId()));
		model.addAttribute("eventDetectors", PointEventDetectorVO.getImplementations(dataPoint.getPointLocator().getDataTypeId()));
		return "dataPointEdit";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String saveDataPoint(HttpServletRequest request, Model model){
		LOG.trace("/data_point_edit.shtm");
		
        User user = Common.getUser(request);
        DataPointVO dataPoint = user.getEditPoint();
        dataPoint.setDiscardExtremeValues(false); // Checkbox
        
        Permissions.ensureDataSourcePermission(user, dataPoint.getDataSourceId());
        
        ServletRequestDataBinder binder = new ServletRequestDataBinder(dataPoint);
        binder.bind(request);
        Map<String, String> errors = new HashMap<String, String>();
        validate(dataPoint, errors);
        
        if (errors.isEmpty()) {
        	executeUpdate(request, dataPoint, errors);
        }
        
        ControllerUtils.addPointListDataToModel(user, dataPoint.getId(), model);
        model.addAttribute("form", dataPoint);
        model.addAttribute("error", errors);
		model.addAttribute("dataSource", Common.ctx.getRuntimeManager().getDataSource(dataPoint.getDataSourceId()));
		model.addAttribute("textRenderers", BaseTextRenderer.getImplementation(dataPoint.getPointLocator().getDataTypeId()));
		model.addAttribute("chartRenderers", BaseChartRenderer.getImplementations(dataPoint.getPointLocator().getDataTypeId()));
		model.addAttribute("eventDetectors", PointEventDetectorVO.getImplementations(dataPoint.getPointLocator().getDataTypeId()));
		return "dataPointEdit";
	}
	
    private void executeUpdate(HttpServletRequest request, DataPointVO point, Map<String, String> errors) {
    	synchronized(point){
            RuntimeManager rtm = Common.ctx.getRuntimeManager();

            if (WebUtils.hasSubmitParameter(request, SUBMIT_DISABLE)) {
                point.setEnabled(false);
                errors.put("status", "confirmation.pointDisabled");
            }
            else if (WebUtils.hasSubmitParameter(request, SUBMIT_ENABLE)) {
                point.setEnabled(true);
                errors.put("status", "confirmation.pointEnabled");
            }
            else if (WebUtils.hasSubmitParameter(request, SUBMIT_RESTART)) {
                point.setEnabled(false);
                rtm.saveDataPoint(point);
                point.setEnabled(true);
                errors.put("status", "confirmation.pointRestarted");
            }
            else if (WebUtils.hasSubmitParameter(request, SUBMIT_SAVE)) {
                DataPointSaveHandler saveHandler = point.getPointLocator().getDataPointSaveHandler();
                if (saveHandler != null)
                    saveHandler.handleSave(point);

                errors.put("status", "confirmation.pointSaved");
            }
            else
                throw new ShouldNeverHappenException("Submission task name type not provided");

            rtm.saveDataPoint(point);
    	}
    }
    
    private void validate(DataPointVO point, Map<String, String> errors){
        if (StringUtils.isEmpty(point.getName()))
            errors.put("name", "validate.required");

        // Logging properties validation
        if (point.getLoggingType() != DataPointVO.LoggingTypes.ON_CHANGE
                && point.getLoggingType() != DataPointVO.LoggingTypes.ALL
                && point.getLoggingType() != DataPointVO.LoggingTypes.NONE
                && point.getLoggingType() != DataPointVO.LoggingTypes.INTERVAL
                && point.getLoggingType() != DataPointVO.LoggingTypes.ON_TS_CHANGE)
        	errors.put("loggingType", "validate.required");

        if (point.getLoggingType() == DataPointVO.LoggingTypes.INTERVAL) {
            if (point.getIntervalLoggingPeriod() <= 0)
            	errors.put("intervalLoggingPeriod", "validate.greaterThanZero");
        }

        if (point.getLoggingType() == DataPointVO.LoggingTypes.ON_CHANGE
                && point.getPointLocator().getDataTypeId() == DataTypes.NUMERIC) {
            if (point.getTolerance() < 0)
            	errors.put("tolerance", "validate.cannotBeNegative");
        }

        if (point.isDiscardExtremeValues() && point.getDiscardHighLimit() <= point.getDiscardLowLimit())
        	errors.put("discardHighLimit", "validate.greaterThanDiscardLow");

        if (point.getLoggingType() != DataPointVO.LoggingTypes.NONE) {
            if (point.getPurgeType() != DataPointVO.PurgeTypes.DAYS
                    && point.getPurgeType() != DataPointVO.PurgeTypes.WEEKS
                    && point.getPurgeType() != DataPointVO.PurgeTypes.MONTHS
                    && point.getPurgeType() != DataPointVO.PurgeTypes.YEARS)
            	errors.put("purgeType", "validate.required");

            if (point.getPurgePeriod() <= 0)
            	errors.put("purgePeriod", "validate.greaterThanZero");
        }

        if (point.getDefaultCacheSize() < 0)
        	errors.put("defaultCacheSize", "validate.cannotBeNegative");

        // Make sure that xids are unique
        List<String> xids = new ArrayList<String>();
        for (PointEventDetectorVO ped : point.getEventDetectors()) {
            if (StringUtils.isEmpty(ped.getXid())) {
            	errors.put("status", "validate.ped.xidMissing");
                break;
            }

            if (xids.contains(ped.getXid())) {
            	errors.put(ped.getXid(), "validate.ped.xidUsed");
                break;
            }
            xids.add(ped.getXid());
        }		
    }
}
