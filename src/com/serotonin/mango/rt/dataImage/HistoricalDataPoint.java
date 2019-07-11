package com.serotonin.mango.rt.dataImage;

import java.util.List;

import com.serotonin.NotImplementedException;
import org.scada_lts.mango.service.PointValueService;
import com.serotonin.timer.SimulationTimer;

public class HistoricalDataPoint implements IDataPoint {
    private final int id;
    private final int dataTypeId;
    private final PointValueService pointValueService;
    private final SimulationTimer timer;

    public HistoricalDataPoint(int id, int dataTypeId, SimulationTimer timer, PointValueService pointValueService) {
        this.id = id;
        this.dataTypeId = dataTypeId;
        this.pointValueService = pointValueService;
        this.timer = timer;
    }

    public int getId() {
        return id;
    }

    @Override
    public List<PointValueTime> getLatestPointValues(int limit) {
        return pointValueService.getLatestPointValues(id, limit, timer.currentTimeMillis());
    }

    @Override
    public void updatePointValue(PointValueTime newValue) {
        throw new NotImplementedException();
    }

    @Override
    public void updatePointValue(PointValueTime newValue, boolean async) {
        throw new NotImplementedException();
    }

    @Override
    public void setPointValue(PointValueTime newValue, SetPointSource source) {
        throw new NotImplementedException();
    }

    @Override
    public PointValueTime getPointValue() {
        return pointValueService.getPointValueBefore(id, timer.currentTimeMillis() + 1);
    }

    @Override
    public PointValueTime getPointValueBefore(long time) {
        return pointValueService.getPointValueBefore(id, time);
    }

    @Override
    public List<PointValueTime> getPointValues(long since) {
        return pointValueService.getPointValuesBetween(id, since, timer.currentTimeMillis());
    }

    @Override
    public List<PointValueTime> getPointValuesBetween(long from, long to) {
        return pointValueService.getPointValuesBetween(id, from, to);
    }

    @Override
    public int getDataTypeId() {
        return dataTypeId;
    }
}
