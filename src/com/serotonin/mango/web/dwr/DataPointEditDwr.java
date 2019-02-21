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

import java.util.List;

import com.serotonin.mango.Common;
import com.serotonin.mango.ScriptSessionAndUsers;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.view.chart.ChartRenderer;
import com.serotonin.mango.view.chart.ImageChartRenderer;
import com.serotonin.mango.view.chart.ImageFlipbookRenderer;
import com.serotonin.mango.view.chart.StatisticsChartRenderer;
import com.serotonin.mango.view.chart.TableChartRenderer;
import com.serotonin.mango.view.text.AnalogRenderer;
import com.serotonin.mango.view.text.BinaryTextRenderer;
import com.serotonin.mango.view.text.MultistateRenderer;
import com.serotonin.mango.view.text.MultistateValue;
import com.serotonin.mango.view.text.NoneRenderer;
import com.serotonin.mango.view.text.PlainRenderer;
import com.serotonin.mango.view.text.RangeRenderer;
import com.serotonin.mango.view.text.RangeValue;
import com.serotonin.mango.view.text.TextRenderer;
import com.serotonin.mango.view.text.TimeRenderer;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import com.serotonin.mango.vo.permission.Permissions;
import org.directwebremoting.WebContextFactory;

public class DataPointEditDwr extends BaseDwr {

    /**
     * if you have possibility,please use
     * @see DataPointEditDwr#getDataPointByDwrScriptSessionId
     *
     * @return DataPointVO
     */
    private DataPointVO getDataPoint() {
        // The user can also end up with this point in their session in the point details page, which only requires
        // read access. So, ensure that any access here is allowed with edit permission.
        User user = Common.getUser();
        DataPointVO dataPoint = user.getEditPoint();
        Permissions.ensureDataSourcePermission(user, dataPoint.getDataSourceId());
        return dataPoint;
    }

    /**
     * @since we have possibility to operate on many tabs on Points,EventDetectors
     *
     * @param dwrScriptSessionId
     * @return DataPointVO
     */
    private DataPointVO getDataPointByDwrScriptSessionId(String dwrScriptSessionId) {
        // The user can also end up with this point in their session in the point details page, which only requires
        // read access. So, ensure that any access here is allowed with edit permission.
        User user = Common.getUser();
        user.setEditPoint((DataPointVO) ScriptSessionAndUsers.getObjectVsScriptSession(
                WebContextFactory.get().getSession().getId(),
                dwrScriptSessionId)
        );
        DataPointVO dataPoint = user.getEditPoint();
        Permissions.ensureDataSourcePermission(user, dataPoint.getDataSourceId());
        return dataPoint;
    }

    //
    // Set text renderer
    //
    public void setAnalogTextRenderer(String format, String suffix) {
        setTextRenderer(new AnalogRenderer(format, suffix));
    }

    public void setBinaryTextRenderer(String zeroLabel, String zeroColour, String oneLabel, String oneColour) {
        setTextRenderer(new BinaryTextRenderer(zeroLabel, zeroColour, oneLabel, oneColour));
    }

    public void setMultistateRenderer(List<MultistateValue> values) {
        MultistateRenderer r = new MultistateRenderer();
        for (MultistateValue v : values)
            r.addMultistateValue(v.getKey(), v.getText(), v.getColour());
        setTextRenderer(r);
    }

    public void setNoneRenderer() {
        setTextRenderer(new NoneRenderer());
    }

    public void setPlainRenderer(String suffix) {
        setTextRenderer(new PlainRenderer(suffix));
    }

    public void setRangeRenderer(String format, List<RangeValue> values) {
        RangeRenderer r = new RangeRenderer(format);
        for (RangeValue v : values)
            r.addRangeValues(v.getFrom(), v.getTo(), v.getText(), v.getColour());
        setTextRenderer(r);
    }

    public void setTimeTextRenderer(String format, int conversionExponent) {
        setTextRenderer(new TimeRenderer(format, conversionExponent));
    }

    private void setTextRenderer(TextRenderer renderer) {
        getDataPoint().setTextRenderer(renderer);
    }

    //
    // Set chart renderer
    //
    public void setNoneChartRenderer() {
        setChartRenderer(null);
    }

    public void setTableChartRenderer(int limit) {
        setChartRenderer(new TableChartRenderer(limit));
    }

    public void setImageChartRenderer(int timePeriod, int numberOfPeriods) {
        setChartRenderer(new ImageChartRenderer(timePeriod, numberOfPeriods));
    }

    public void setStatisticsChartRenderer(int timePeriod, int numberOfPeriods, boolean includeSum) {
        setChartRenderer(new StatisticsChartRenderer(timePeriod, numberOfPeriods, includeSum));
    }

    public void setImageFlipbookRenderer(int limit) {
        setChartRenderer(new ImageFlipbookRenderer(limit));
    }

    private void setChartRenderer(ChartRenderer renderer) {
        getDataPoint().setChartRenderer(renderer);
    }

    //
    // Data purge
    //
    public long purgeNow(int purgeType, int purgePeriod, boolean allData) {
        DataPointVO point = getDataPoint();
        RuntimeManager rm = Common.ctx.getRuntimeManager();
        Long count;
        if (allData)
            count = rm.purgeDataPointValues(point.getId());
        else
            count = rm.purgeDataPointValues(point.getId(), purgeType, purgePeriod);
        return count;
    }

    //
    // Clear point cache
    //
    public void clearPointCache() {
        DataPointVO point = getDataPoint();
        DataPointRT rt = Common.ctx.getRuntimeManager().getDataPoint(point.getId());
        if (rt != null)
            rt.resetValues();
    }

    //
    // Event detectors TODO: This section can be cleaned up since PointEventDetectorVO is now a single class.
    //
    public List<PointEventDetectorVO> getEventDetectors(String dwrScriptSessionid) {
        return getDataPointByDwrScriptSessionId(dwrScriptSessionid)/*getDataPoint()*/.getEventDetectors();
    }

    public PointEventDetectorVO addEventDetector(String dwrScriptSessionid, int typeId) {
        DataPointVO dp = getDataPointByDwrScriptSessionId(dwrScriptSessionid);
        PointEventDetectorVO ped = new PointEventDetectorVO();
        ped.setXid(new DataPointDao().generateEventDetectorUniqueXid(dp.getId()));
        ped.setAlias("");
        ped.setDetectorType(typeId);

        if (typeId == PointEventDetectorVO.TYPE_STATE_CHANGE_COUNT) {
            ped.setChangeCount(2);
            ped.setDuration(1);
        }
        else if (typeId == PointEventDetectorVO.TYPE_NO_CHANGE)
            ped.setDuration(1);
        else if (typeId == PointEventDetectorVO.TYPE_NO_UPDATE)
            ped.setDuration(1);

        int id = -1;
        synchronized (dp) {
            // Get a unique negative id as an indicator that this is new.
            for (PointEventDetectorVO d : dp.getEventDetectors()) {
                if (d.getId() <= id)
                    id = d.getId() - 1;
            }
            ped.setId(id);
            ped.njbSetDataPoint(dp);
            dp.getEventDetectors().add(ped);
        }
        return ped;
    }

    public void deleteEventDetector(String dwrScriptSessionid, int pedId) {
        DataPointVO dp = getDataPointByDwrScriptSessionId(dwrScriptSessionid);//getDataPoint();
        synchronized (dp) {
            dp.getEventDetectors().remove(getEventDetector(pedId));
        }
    }

    public void updateHighLimitDetector(String dwrScriptSessionid,int pedId, String xid, String alias, double limit, int duration,
            int durationType, int alarmLevel) {
        PointEventDetectorVO ped = getEventDetectorByDwrScriptSessionId(dwrScriptSessionid,pedId);
        ped.setXid(xid);
        ped.setAlias(alias);
        ped.setLimit(limit);
        ped.setDuration(duration);
        ped.setDurationType(durationType);
        ped.setAlarmLevel(alarmLevel);
    }

    public void updateLowLimitDetector(String dwrScriptSessionid,int pedId, String xid, String alias, double limit, int duration,
            int durationType, int alarmLevel) {
        PointEventDetectorVO ped = getEventDetectorByDwrScriptSessionId(dwrScriptSessionid,pedId);
        ped.setXid(xid);
        ped.setAlias(alias);
        ped.setLimit(limit);
        ped.setDuration(duration);
        ped.setDurationType(durationType);
        ped.setAlarmLevel(alarmLevel);
    }

    public void updateBinaryStateDetector(String dwrScriptSessionid,int pedId, String xid, String alias, boolean state, int duration,
            int durationType, int alarmLevel) {
        PointEventDetectorVO ped = getEventDetectorByDwrScriptSessionId(dwrScriptSessionid,pedId);
        ped.setXid(xid);
        ped.setAlias(alias);
        ped.setBinaryState(state);
        ped.setDuration(duration);
        ped.setDurationType(durationType);
        ped.setAlarmLevel(alarmLevel);
    }

    public void updateMultistateStateDetector(String dwrScriptSessionid,int pedId, String xid, String alias, int state, int duration,
            int durationType, int alarmLevel) {
        PointEventDetectorVO ped = getEventDetectorByDwrScriptSessionId(dwrScriptSessionid,pedId);
        ped.setXid(xid);
        ped.setAlias(alias);
        ped.setMultistateState(state);
        ped.setDuration(duration);
        ped.setDurationType(durationType);
        ped.setAlarmLevel(alarmLevel);
    }

    public void updatePointChangeDetector(String dwrScriptSessionid,int pedId, String xid, String alias, int alarmLevel) {
        PointEventDetectorVO ped = getEventDetectorByDwrScriptSessionId(dwrScriptSessionid,pedId);
        ped.setXid(xid);
        ped.setAlias(alias);
        ped.setAlarmLevel(alarmLevel);
    }

    public void updateStateChangeCountDetector(String dwrScriptSessionid,int pedId, String xid, String alias, int count, int duration,
            int durationType, int alarmLevel) {
        PointEventDetectorVO ped = getEventDetectorByDwrScriptSessionId(dwrScriptSessionid,pedId);
        ped.setXid(xid);
        ped.setAlias(alias);
        ped.setChangeCount(count);
        ped.setDuration(duration);
        ped.setDurationType(durationType);
        ped.setAlarmLevel(alarmLevel);
    }

    public void updateNoChangeDetector(String dwrScriptSessionid,int pedId, String xid, String alias, int duration, int durationType,
            int alarmLevel) {
        PointEventDetectorVO ped = getEventDetectorByDwrScriptSessionId(dwrScriptSessionid,pedId);
        ped.setXid(xid);
        ped.setAlias(alias);
        ped.setDuration(duration);
        ped.setDurationType(durationType);
        ped.setAlarmLevel(alarmLevel);
    }

    public void updateNoUpdateDetector(String dwrScriptSessionid,int pedId, String xid, String alias, int duration, int durationType,
            int alarmLevel) {
        PointEventDetectorVO ped = getEventDetectorByDwrScriptSessionId(dwrScriptSessionid,pedId);
        ped.setXid(xid);
        ped.setAlias(alias);
        ped.setDuration(duration);
        ped.setDurationType(durationType);
        ped.setAlarmLevel(alarmLevel);
    }

    public void updateAlphanumericStateDetector(String dwrScriptSessionid,int pedId, String xid, String alias, String state, int duration,
            int durationType, int alarmLevel) {
        PointEventDetectorVO ped = getEventDetectorByDwrScriptSessionId(dwrScriptSessionid,pedId);
        ped.setXid(xid);
        ped.setAlias(alias);
        ped.setAlphanumericState(state);
        ped.setDuration(duration);
        ped.setDurationType(durationType);
        ped.setAlarmLevel(alarmLevel);
    }

    public void updatePositiveCusumDetector(String dwrScriptSessionid,int pedId, String xid, String alias, double limit, double weight,
            int duration, int durationType, int alarmLevel) {
        PointEventDetectorVO ped = getEventDetectorByDwrScriptSessionId(dwrScriptSessionid,pedId);
        ped.setXid(xid);
        ped.setAlias(alias);
        ped.setLimit(limit);
        ped.setWeight(weight);
        ped.setDuration(duration);
        ped.setDurationType(durationType);
        ped.setAlarmLevel(alarmLevel);
    }

    public void updateNegativeCusumDetector(String dwrScriptSessionid,int pedId, String xid, String alias, double limit, double weight,
            int duration, int durationType, int alarmLevel) {
        PointEventDetectorVO ped = getEventDetectorByDwrScriptSessionId(dwrScriptSessionid,pedId);
        ped.setXid(xid);
        ped.setAlias(alias);
        ped.setLimit(limit);
        ped.setWeight(weight);
        ped.setDuration(duration);
        ped.setDurationType(durationType);
        ped.setAlarmLevel(alarmLevel);
    }

    private PointEventDetectorVO getEventDetector(int pedId) {
        DataPointVO dp = getDataPoint();
        for (PointEventDetectorVO ped : dp.getEventDetectors()) {
            if (ped.getId() == pedId) {
                return ped;
            }
        }
        return null;
    }
    private PointEventDetectorVO getEventDetectorByDwrScriptSessionId(String dwrScriptSessionid,int pedId) {
        DataPointVO dp = getDataPointByDwrScriptSessionId(dwrScriptSessionid);
        for (PointEventDetectorVO ped : dp.getEventDetectors()) {
            if (ped.getId() == pedId) {
                return ped;
            }
        }
        return null;
    }
}
