package com.serotonin.mango.rt.dataSource.meta;

import com.serotonin.db.IntValuePair;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.web.i18n.LocalizableMessage;

import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public interface EventExecutor {
    Optional<DataPointStateException> execute(LocalizableMessage message,
                                              BiFunction<Long, DataPointRT, Consumer<LocalizableMessage>> raiseEvent,
                                              BiPredicate<DataPointRT, DataPointVO> raiseEventIf,
                                              BiFunction<Long, DataPointRT, Consumer<LocalizableMessage>> returnToNormal);
    static EventExecutor newExecutor(DataPointRT parentPoint, DataSourceRT parentSource,
                                     IntValuePair contextEntry, DataPointRT contextPoint,
                                     DataPointVO contextPointVO, ResourceBundle resourceBundle) {
        return new EventExecutorImpl(parentPoint, parentSource, contextEntry, contextPoint, contextPointVO, resourceBundle);
    }
}
