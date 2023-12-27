package com.serotonin.mango.rt.dataSource.http;

import com.serotonin.mango.vo.dataSource.http.HttpRetrieverDataSourceVO;
import org.scada_lts.ds.StartStopDsRT;
import org.scada_lts.ds.model.ReactivationDs;
import org.scada_lts.ds.reactivation.ReactivationConnectHttpRetriever;
import org.scada_lts.ds.reactivation.ReactivationManager;
import org.scada_lts.ds.state.SleepStateDs;
import org.scada_lts.ds.state.StopChangeEnableStateDs;

public class StopSleepRT {

    private final HttpRetrieverDataSourceVO vo;

    public StopSleepRT(HttpRetrieverDataSourceVO vo) {
        this.vo = vo;
    }

    public void sleep(ReactivationDs r) {
        ReactivationConnectHttpRetriever rhr = new ReactivationConnectHttpRetriever();
        ReactivationManager.getInstance().addProcess(rhr, r, vo);
        StartStopDsRT stopDsRT = new StartStopDsRT(vo.getId(), false, new SleepStateDs());
        new Thread(stopDsRT).start();
    }

    public void stop() {
        StartStopDsRT stopDsRT = new StartStopDsRT(vo.getId(), false, new StopChangeEnableStateDs());
        new Thread(stopDsRT).start();
    }
}
