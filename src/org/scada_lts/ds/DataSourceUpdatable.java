package org.scada_lts.ds;

import com.serotonin.mango.vo.dataSource.DataSourceVO;

public interface DataSourceUpdatable<T extends DataSourceVO<?>> {
    int getUpdatePeriodType();
    int getUpdatePeriods();
    int getUpdateAttempts();
    DataSourceVO<T> toDataSource();
}
