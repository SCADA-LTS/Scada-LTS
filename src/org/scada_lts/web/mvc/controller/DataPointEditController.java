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
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.web.mvc.form.LoginForm;
import org.scada_lts.web.mvc.form.SqlForm;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
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
	
    public static final String SUBMIT_SAVE = "save";
    public static final String SUBMIT_DISABLE = "disable";
    public static final String SUBMIT_ENABLE = "enable";
    public static final String SUBMIT_RESTART = "restart";
	
	@RequestMapping(method = RequestMethod.GET)
    public ModelAndView creatForm(HttpServletRequest request) {
		LOG.trace("/data_point_edit.shtm");
        DataPointVO dataPoint;
        User user = Common.getUser(request);
        int id;
        DataPointDao dataPointDao = new DataPointDao();

        // Get the id.
        String idStr = request.getParameter("dpid");
        if (idStr == null) {
            // Check for pedid (point event detector id)
            String pedStr = request.getParameter("pedid");
            if (pedStr == null)
                throw new ShouldNeverHappenException("dpid or pedid must be provided for this page");

            int pedid = Integer.parseInt(pedStr);
            id = dataPointDao.getDataPointIdFromDetectorId(pedid);
        }
        else
            id = Integer.parseInt(idStr);

        dataPoint = dataPointDao.getDataPoint(id);

        // Save the point in the user object so that DWR calls have access to it.
        user.setEditPoint(dataPoint);
        
        Permissions.ensureDataSourcePermission(user, dataPoint.getDataSourceId());
        
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("form", dataPoint);
		model.put("dataSource", Common.ctx.getRuntimeManager().getDataSource(dataPoint.getDataSourceId()));
		model.put("textRenderers", BaseTextRenderer.getImplementation(dataPoint.getPointLocator().getDataTypeId()));
		model.put("chartRenderers", BaseChartRenderer.getImplementations(dataPoint.getPointLocator().getDataTypeId()));
		model.put("eventDetectors", PointEventDetectorVO.getImplementations(dataPoint.getPointLocator().getDataTypeId()));
        ControllerUtils.addPointListDataToModel(Common.getUser(request), dataPoint.getId(), model);
		return new ModelAndView("dataPointEdit", model);
    }
	
	@RequestMapping(method = RequestMethod.POST)
    protected ModelAndView onSubmit(HttpServletRequest request) throws Exception {
		LOG.trace("/data_point_edit.shtm");
		
        User user = Common.getUser(request);
        DataPointVO dataPoint = user.getEditPoint();
        dataPoint.setDiscardExtremeValues(false); // Checkbox
        
        Permissions.ensureDataSourcePermission(user, dataPoint.getDataSourceId());
        
        ServletRequestDataBinder binder = new ServletRequestDataBinder(dataPoint);
        binder.bind(request);
        
        executeUpdate(request, dataPoint, new BindException(dataPoint, "dataPoint"));
        
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("form", dataPoint);
		model.put("dataSource", Common.ctx.getRuntimeManager().getDataSource(dataPoint.getDataSourceId()));
		model.put("textRenderers", BaseTextRenderer.getImplementation(dataPoint.getPointLocator().getDataTypeId()));
		model.put("chartRenderers", BaseChartRenderer.getImplementations(dataPoint.getPointLocator().getDataTypeId()));
		model.put("eventDetectors", PointEventDetectorVO.getImplementations(dataPoint.getPointLocator().getDataTypeId()));
        ControllerUtils.addPointListDataToModel(Common.getUser(request), dataPoint.getId(), model);
		return new ModelAndView("dataPointEdit", model);
	}
	
	@InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
        binder.registerCustomEditor(Double.TYPE, "tolerance", new DecimalFormatEditor(new DecimalFormat("#.##"), false));
        binder.registerCustomEditor(Integer.TYPE, "purgePeriod", new IntegerFormatEditor(new DecimalFormat("#"), false));
        binder.registerCustomEditor(Double.TYPE, "discardLowLimit", new DecimalFormatEditor(new DecimalFormat("#.##"),
                false));
        binder.registerCustomEditor(Double.TYPE, "discardHighLimit", new DecimalFormatEditor(new DecimalFormat("#.##"),
                false));
    }
	
    private void executeUpdate(HttpServletRequest request, DataPointVO point, Errors errors) {
        if (StringUtils.isEmpty(point.getName()))
            errors.rejectValue("name", "validate.required");

        // Logging properties validation
        if (point.getLoggingType() != DataPointVO.LoggingTypes.ON_CHANGE
                && point.getLoggingType() != DataPointVO.LoggingTypes.ALL
                && point.getLoggingType() != DataPointVO.LoggingTypes.NONE
                && point.getLoggingType() != DataPointVO.LoggingTypes.INTERVAL
                && point.getLoggingType() != DataPointVO.LoggingTypes.ON_TS_CHANGE)
        	errors.rejectValue("loggingType", "validate.required");

        if (point.getLoggingType() == DataPointVO.LoggingTypes.INTERVAL) {
            if (point.getIntervalLoggingPeriod() <= 0)
            	errors.rejectValue("intervalLoggingPeriod", "validate.greaterThanZero");
        }

        if (point.getLoggingType() == DataPointVO.LoggingTypes.ON_CHANGE
                && point.getPointLocator().getDataTypeId() == DataTypes.NUMERIC) {
            if (point.getTolerance() < 0)
            	errors.rejectValue("tolerance", "validate.cannotBeNegative");
        }

        if (point.isDiscardExtremeValues() && point.getDiscardHighLimit() <= point.getDiscardLowLimit())
        	errors.rejectValue("discardHighLimit", "validate.greaterThanDiscardLow");

        if (point.getLoggingType() != DataPointVO.LoggingTypes.NONE) {
            if (point.getPurgeType() != DataPointVO.PurgeTypes.DAYS
                    && point.getPurgeType() != DataPointVO.PurgeTypes.WEEKS
                    && point.getPurgeType() != DataPointVO.PurgeTypes.MONTHS
                    && point.getPurgeType() != DataPointVO.PurgeTypes.YEARS)
            	errors.rejectValue("purgeType", "validate.required");

            if (point.getPurgePeriod() <= 0)
            	errors.rejectValue("purgePeriod", "validate.greaterThanZero");
        }

        if (point.getDefaultCacheSize() < 0)
        	errors.rejectValue("defaultCacheSize", "validate.cannotBeNegative");

        // Make sure that xids are unique
        List<String> xids = new ArrayList<String>();
        for (PointEventDetectorVO ped : point.getEventDetectors()) {
            if (StringUtils.isEmpty(ped.getXid())) {
            	errors.reject("validate.ped.xidMissing");
                break;
            }

            if (xids.contains(ped.getXid())) {
            	errors.reject("validate.ped.xidUsed", ped.getXid());
                break;
            }
            xids.add(ped.getXid());
        }

        if (!errors.hasErrors()) {
            RuntimeManager rtm = Common.ctx.getRuntimeManager();

            if (WebUtils.hasSubmitParameter(request, SUBMIT_DISABLE)) {
                point.setEnabled(false);
                errors.reject("confirmation.pointDisabled");
            }
            else if (WebUtils.hasSubmitParameter(request, SUBMIT_ENABLE)) {
                point.setEnabled(true);
                errors.reject("confirmation.pointEnabled");
            }
            else if (WebUtils.hasSubmitParameter(request, SUBMIT_RESTART)) {
                point.setEnabled(false);
                rtm.saveDataPoint(point);
                point.setEnabled(true);
                errors.reject("confirmation.pointRestarted");
            }
            else if (WebUtils.hasSubmitParameter(request, SUBMIT_SAVE)) {
                DataPointSaveHandler saveHandler = point.getPointLocator().getDataPointSaveHandler();
                if (saveHandler != null)
                    saveHandler.handleSave(point);

                errors.reject("confirmation.pointSaved");
            }
            else
                throw new ShouldNeverHappenException("Submission task name type not provided");

            rtm.saveDataPoint(point);
        }
    }
}
