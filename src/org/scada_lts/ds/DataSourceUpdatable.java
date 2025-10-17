package org.scada_lts.ds;

import com.serotonin.mango.vo.dataSource.DataSourceVO;

public interface DataSourceUpdatable<T extends DataSourceVO<?>> {
    int getUpdatePeriodType();
    int getUpdatePeriods();
    default int getUpdateAttempts() {
        return -1;
    }
    default boolean isQuantize() {
        return false;
    }
    DataSourceVO<T> toDataSource();
}
