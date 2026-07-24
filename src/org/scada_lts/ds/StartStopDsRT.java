package org.scada_lts.ds;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.ds.state.IStateDs;
import org.scada_lts.ds.state.change.AlertObserver;

/**
 * @project Scada-LTS
 * @author Grzegorz Bylica on behalf of Abil'I.T. (code owner) email: sdt@abilit.eu
 */
public class StartStopDsRT implements Runnable {

    private static final Log LOG = LogFactory.getLog(StartStopDsRT.class);

    private int idDs;
    private boolean enable;
    private IStateDs state;

    public StartStopDsRT(int idDs, boolean enable, IStateDs state) {
        this.idDs = idDs;
        this.enable = enable;
        this.state = state;
    }

    @Override
    public void run() {
        try {
            RuntimeManager runtimeManager = Common.ctx.getRuntimeManager();
            DataSourceVO<?> dataSource = runtimeManager.getDataSource(idDs);
            new AlertObserver(dataSource);
            dataSource.setEnabled(enable);
            dataSource.setState(state);
            runtimeManager.saveDataSource(dataSource);

        } catch (Exception e) {
            LOG.error(e);
        }

    }
}
