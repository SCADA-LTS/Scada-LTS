package org.scada_lts.ds.state.change;

import com.serotonin.mango.rt.dataSource.http.HttpRetrieverDataSourceRT;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import org.scada_lts.ds.state.IStateDs;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Grzegorz Bylica on behalf of Abil'I.T. (code owner) email: sdt@abilit.eu
 */
public class AlertObserver implements PropertyChangeListener {

    public AlertObserver(ChangeStatus changeManageStatus) {
        changeManageStatus.addChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {

        String describe = ((IStateDs) propertyChangeEvent.getNewValue()).getDescribe();
        DataSourceVO<?> vo = (DataSourceVO<?>) propertyChangeEvent.getSource();

        HttpRetrieverDataSourceRT.raiseEvent(describe, vo);

    }
}
