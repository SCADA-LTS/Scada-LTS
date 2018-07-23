package com.serotonin.mango.rt.dataImage;

import java.util.List;

import com.serotonin.NotImplementedException;
import com.serotonin.mango.db.dao.PointValueDao;
import com.serotonin.timer.SimulationTimer;

public class HistoricalDataPoint implements IDataPoint {
    private final int id;
    private final int dataTypeId;
    private final PointValueDao pointValueDao;
    private final SimulationTimer timer;

    public HistoricalDataPoint(int id, int dataTypeId, SimulationTimer timer, PointValueDao pointValueDao) {
        this.id = id;
        this.dataTypeId = dataTypeId;
        this.pointValueDao = pointValueDao;
        this.timer = timer;
    }

    public int getId() {
        return id;
    }

    @Override
    public List<PointValueTime> getLatestPointValues(int limit) {
        return pointValueDao.getLatestPointValues(id, limit, timer.currentTimeMillis());
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
        return pointValueDao.getPointValueBefore(id, timer.currentTimeMillis() + 1);
    }

    @Override
    public PointValueTime getPointValueBefore(long time) {
        return pointValueDao.getPointValueBefore(id, time);
    }

    @Override
    public List<PointValueTime> getPointValues(long since) {
        return pointValueDao.getPointValuesBetween(id, since, timer.currentTimeMillis());
    }

    @Override
    public List<PointValueTime> getPointValuesBetween(long from, long to) {
        return pointValueDao.getPointValuesBetween(id, from, to);
    }

    @Override
    public int getDataTypeId() {
        return dataTypeId;
    }
}
