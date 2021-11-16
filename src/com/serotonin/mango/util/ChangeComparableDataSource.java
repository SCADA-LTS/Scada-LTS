package com.serotonin.mango.util;

import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.web.i18n.LocalizableMessage;

import java.util.List;

public interface ChangeComparableDataSource<T> extends ChangeComparable<T>{
    void addPropertyChangesDS(List<LocalizableMessage> list, DataSourceVO<?> from);
}
