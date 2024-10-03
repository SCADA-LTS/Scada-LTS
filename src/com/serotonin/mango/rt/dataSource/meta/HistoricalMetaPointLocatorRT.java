package com.serotonin.mango.rt.dataSource.meta;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.vo.dataSource.meta.MetaPointLocatorVO;
import com.serotonin.timer.SimulationTimer;
import com.serotonin.web.i18n.LocalizableMessage;

@Deprecated(since = "2.8.0")
public class HistoricalMetaPointLocatorRT extends MetaPointLocatorRT {
    private long updates;

    public HistoricalMetaPointLocatorRT(MetaPointLocatorVO vo) {
        super(vo);
    }

    public void initialize(SimulationTimer timer, DataPointRT dataPoint) {
        this.timer = timer;
        this.dataPoint = dataPoint;
        initialized = true;
        initializeTimerTask();
        this.context = createContext(dataPoint);
    }

    @Override
    public void terminate() {
        synchronized (LOCK) {
            // Cancel scheduled job
            if (timerTask != null)
                timerTask.cancel();
        }
    }

    public long getUpdates() {
        return updates;
    }

    @Override
    protected void doUpdate(PointValueTime pvt, DataPointRT dataPoint) {
        super.doUpdate(pvt, dataPoint);
        updates++;
    }

    @Override
    protected void handleScriptError(long runtime, DataPointRT dataPoint, LocalizableMessage message) {
        throw new MetaPointExecutionException(message);
    }
}
