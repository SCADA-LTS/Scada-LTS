package com.serotonin.mango.rt;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataSource.DataSourceRT;

public class CooperationOnDataPointValue {

    //this method will be used to set point value when user will change value of point by GUI
    //void setDataPointValueDependsOnSource(int dataPointId, MangoValue value, SetPointSource source) {

        //PointValueTime pointValueTime = null;

        //if(source instanceof User){
        //PointValueTime pointValueTime = new PointValueTime(value, System.currentTimeMillis()/*,((User)source).getUsername()*/);
        //}
        //else
        //    pointValueTime = new PointValueTime(value, System.currentTimeMillis());

        //setDataPointValue(dataPointId,pointValueTime,source);

    //}
    public void setDataPointValue(int dataPointId, MangoValue value,
                                  SetPointSource source) {

        setDataPointValue(dataPointId,
                new PointValueTime(value, System.currentTimeMillis()), source);

    }
    public void setDataPointValue(int dataPointId, PointValueTime valueTime, SetPointSource source) {

        DataPointRT dataPointRT = Common.ctx.getRuntimeManager().getDataPointDependsOnDataPointId(dataPointId);

        checkDataPointProperties(dataPointRT);

        // Tell the data source to set the value of the point.
        DataSourceRT dataSourceRT = Common.ctx.getRuntimeManager().getRunningDataSource(dataPointRT.getDataSourceId());
        // The data source may have been disabled. Just make sure.
        if (dataSourceRT != null)
            dataSourceRT.setPointValue(dataPointRT,valueTime,source);

    }
    public void forcePointRead(int dataPointId){
        DataPointRT dataPointRT = Common.ctx.getRuntimeManager().getDataPoints().get(dataPointId);

        checkThatPointIsEnabled(dataPointRT);

        // Tell the data source to read the point value;
        DataSourceRT ds = Common.ctx.getRuntimeManager().getRunningDataSource(dataPointRT.getDataSourceId());
        if (ds != null)
            // The data source may have been disabled. Just make sure.
            ds.forcePointRead(dataPointRT);
    }
    public void relinquish(int dataPointId) {
        DataPointRT dataPointRT = Common.ctx.getRuntimeManager().getDataPoints().get(dataPointId);

        relinquishChecks(dataPointRT);

        // Tell the data source to relinquish value of the point.
        DataSourceRT ds = Common.ctx.getRuntimeManager().getRunningDataSource(dataPointRT.getDataSourceId());
        // The data source may have been disabled. Just make sure.
        if (ds != null)
            ds.relinquish(dataPointRT);
    }
    void relinquishChecks(DataPointRT dataPoint){

        checkDataPointProperties(dataPoint);

        if (!dataPoint.getPointLocator().isRelinquishable())
            throw new RTException("Point is not relinquishable");

    }
    void checkDataPointProperties(DataPointRT dataPointRT){

        checkThatPointIsEnabled(dataPointRT);

        if(!dataPointRT.getPointLocator().isSettable())
            throw new RTException("Point is not settable");
    }
    void checkThatPointIsEnabled(DataPointRT dataPointRT){

        if(dataPointRT == null)
            throw new RTException("Point is not enabled");
    }

}
