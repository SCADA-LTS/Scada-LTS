package org.scada_lts.ds;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.vo.dataSource.DataSourceVO;

/**
 * @project Scada-LTS
 * @autor grzegorz.bylica@gmail.com on 05.10.18
 */
public class StopDsRT implements Runnable {

    private int idDs;

    public StopDsRT(int idDs) {
        this.idDs = idDs;
    }

    @Override
    public void run() {
        try {
            RuntimeManager runtimeManager = Common.ctx.getRuntimeManager();
            DataSourceVO<?> dataSource = runtimeManager.getDataSource(idDs);
            dataSource.setEnabled(false);
            runtimeManager.saveDataSource(dataSource);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
