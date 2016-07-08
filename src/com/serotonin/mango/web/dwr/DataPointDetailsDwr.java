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
package com.serotonin.mango.web.dwr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.directwebremoting.WebContextFactory;
import org.joda.time.DateTime;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.rt.dataImage.AnnotatedPointValueTime;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueFacade;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.ImageValue;
import com.serotonin.mango.view.chart.StatisticsChartRenderer;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.bean.ImageValueBean;
import com.serotonin.mango.web.dwr.beans.DataExportDefinition;
import com.serotonin.mango.web.dwr.beans.RenderedPointValueTime;
import com.serotonin.mango.web.dwr.beans.WatchListState;
import com.serotonin.mango.web.servlet.ImageValueServlet;
import com.serotonin.mango.web.taglib.Functions;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.dwr.MethodFilter;
import com.serotonin.web.i18n.LocalizableMessage;
import com.serotonin.web.taglib.DateFunctions;

public class DataPointDetailsDwr extends BaseDwr {
	@MethodFilter
	public WatchListState getPointData() {
		// Get the point from the user's session. It should have been set by the
		// controller.
		HttpServletRequest request = WebContextFactory.get()
				.getHttpServletRequest();
		User user = Common.getUser(request);
		DataPointVO pointVO = user.getEditPoint();

		// Create the watch list state.
		RuntimeManager rtm = Common.ctx.getRuntimeManager();
		Map<String, Object> model = new HashMap<String, Object>();

		// Get the data point status from the data image.
		DataPointRT pointRT = rtm.getDataPoint(pointVO.getId());

		WatchListState state = new WatchListState();
		state.setId(Integer.toString(pointVO.getId()));

		PointValueTime pointValue = prepareBasePointState(
				Integer.toString(pointVO.getId()), state, pointVO, pointRT,
				model);
		setPrettyText(state, pointVO, model, pointValue);
		if (state.getValue() != null)
			setChange(pointVO, state, pointRT, request, model, user);

		setEvents(pointVO, user, model);
		setMessages(state, request, "watchListMessages", model);

		return state;
	}

	@MethodFilter
	public DwrResponseI18n getHistoryTableData(int limit) {
		DataPointVO pointVO = Common.getUser().getEditPoint();
		PointValueFacade facade = new PointValueFacade(pointVO.getId());

		List<PointValueTime> rawData = facade.getLatestPointValues(limit);
		List<RenderedPointValueTime> renderedData = new ArrayList<RenderedPointValueTime>(
				rawData.size());

		for (PointValueTime pvt : rawData) {
			RenderedPointValueTime rpvt = new RenderedPointValueTime();
			rpvt.setValue(Functions.getHtmlText(pointVO, pvt));
			rpvt.setTime(Functions.getTime(pvt));
			if (pvt.isAnnotated()) {
				AnnotatedPointValueTime apvt = (AnnotatedPointValueTime) pvt;
				rpvt.setAnnotation(apvt.getAnnotation(getResourceBundle()));
			}
			renderedData.add(rpvt);
		}

		DwrResponseI18n response = new DwrResponseI18n();
		response.addData("history", renderedData);
		addAsof(response);
		return response;
	}

	@MethodFilter
	public DwrResponseI18n getImageChartData(int fromYear, int fromMonth,
			int fromDay, int fromHour, int fromMinute, int fromSecond,
			boolean fromNone, int toYear, int toMonth, int toDay, int toHour,
			int toMinute, int toSecond, boolean toNone, int width, int height) {
		DateTime from = createDateTime(fromYear, fromMonth, fromDay, fromHour,
				fromMinute, fromSecond, fromNone);
		DateTime to = createDateTime(toYear, toMonth, toDay, toHour, toMinute,
				toSecond, toNone);

		StringBuilder htmlData = new StringBuilder();
		htmlData.append("<img src=\"chart/ft_");
		htmlData.append(System.currentTimeMillis());
		htmlData.append('_');
		htmlData.append(fromNone ? -1 : from.getMillis());
		htmlData.append('_');
		htmlData.append(toNone ? -1 : to.getMillis());
		htmlData.append('_');
		htmlData.append(getDataPointVO().getId());
		htmlData.append(".png?w=");
		htmlData.append(width);
		htmlData.append("&h=");
		htmlData.append(height);
		htmlData.append("\" alt=\"" + getMessage("common.imageChart") + "\"/>");

		DwrResponseI18n response = new DwrResponseI18n();
		response.addData("chart", htmlData.toString());
		addAsof(response);
		return response;
	}

	@MethodFilter
	public void getChartData(int fromYear, int fromMonth, int fromDay,
			int fromHour, int fromMinute, int fromSecond, boolean fromNone,
			int toYear, int toMonth, int toDay, int toHour, int toMinute,
			int toSecond, boolean toNone) {
		DateTime from = createDateTime(fromYear, fromMonth, fromDay, fromHour,
				fromMinute, fromSecond, fromNone);
		DateTime to = createDateTime(toYear, toMonth, toDay, toHour, toMinute,
				toSecond, toNone);
		DataExportDefinition def = new DataExportDefinition(
				new int[] { getDataPointVO().getId() }, from, to);
		Common.getUser().setDataExportDefinition(def);
	}

	@MethodFilter
	public DwrResponseI18n getStatsChartData(int periodType, int period,
			boolean includeSum) {
		HttpServletRequest request = WebContextFactory.get()
				.getHttpServletRequest();
		DataPointVO pointVO = Common.getUser(request).getEditPoint();

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("point", pointVO);
		StatisticsChartRenderer r = new StatisticsChartRenderer(periodType,
				period, includeSum);
		r.addDataToModel(model, pointVO);

		DwrResponseI18n response = new DwrResponseI18n();
		response.addData("stats",
				generateContent(request, "statsChart.jsp", model));
		addAsof(response);
		return response;
	}

	@MethodFilter
	private DataPointVO getDataPointVO() {
		return Common.getUser().getEditPoint();
	}

	@MethodFilter
	public DwrResponseI18n getFlipbookData(int limit) {
		HttpServletRequest request = WebContextFactory.get()
				.getHttpServletRequest();
		DataPointVO vo = Common.getUser(request).getEditPoint();
		PointValueFacade facade = new PointValueFacade(vo.getId());

		List<PointValueTime> values = facade.getLatestPointValues(limit);
		Collections.reverse(values);
		List<ImageValueBean> result = new ArrayList<ImageValueBean>();
		for (PointValueTime pvt : values) {
			ImageValue imageValue = (ImageValue) pvt.getValue();
			String uri = ImageValueServlet.servletPath
					+ ImageValueServlet.historyPrefix + pvt.getTime() + "_"
					+ vo.getId() + "." + imageValue.getTypeExtension();
			result.add(new ImageValueBean(Functions.getTime(pvt), uri));
		}

		DwrResponseI18n response = new DwrResponseI18n();
		response.addData("images", result);
		addAsof(response);
		return response;
	}

	private void addAsof(DwrResponseI18n response) {
		response.addData("asof", new LocalizableMessage("dsDetils.asof",
				DateFunctions.getFullSecondTime(System.currentTimeMillis())));
	}
}
