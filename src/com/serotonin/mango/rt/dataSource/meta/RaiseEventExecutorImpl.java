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

class RaiseEventExecutorImpl implements RaiseEventExecutor {

    private final DataPointRT parentPoint;
    private final DataSourceRT parentSource;
    private final IntValuePair contextEntry;
    private final DataPointRT contextPoint;
    private final DataPointVO contextPointVO;
    private final ResourceBundle resourceBundle;

    public RaiseEventExecutorImpl(DataPointRT parentPoint, DataSourceRT parentSource,
                                  IntValuePair contextEntry, DataPointRT contextPoint,
                                  DataPointVO contextPointVO, ResourceBundle resourceBundle) {
        this.parentPoint = parentPoint;
        this.parentSource = parentSource;
        this.contextEntry = contextEntry;
        this.contextPoint = contextPoint;
        this.contextPointVO = contextPointVO;
        this.resourceBundle = resourceBundle;
    }

    @Override
    public Optional<DataPointStateException> execute(LocalizableMessage message,
                                                     BiFunction<Long, DataPointRT, Consumer<LocalizableMessage>> raiseEvent,
                                                     BiPredicate<DataPointRT, DataPointVO> raiseEventIf,
                                                     BiFunction<Long, DataPointRT, Consumer<LocalizableMessage>> returnToNormal) {
        return doRaiseEvent(parentPoint, parentSource, contextEntry, contextPoint, contextPointVO, resourceBundle, message, raiseEvent, raiseEventIf, returnToNormal);
    }

    private static Optional<DataPointStateException> doRaiseEvent(DataPointRT parentPoint,
                                                                  DataSourceRT parentSource,
                                                                  IntValuePair contextEntry,
                                                                  DataPointRT contextPoint,
                                                                  DataPointVO contextPointVO,
                                                                  ResourceBundle resourceBundle,
                                                                  LocalizableMessage message,
                                                                  BiFunction<Long, DataPointRT, Consumer<LocalizableMessage>> raiseEvent,
                                                                  BiPredicate<DataPointRT, DataPointVO> raiseEventIf,
                                                                  BiFunction<Long, DataPointRT, Consumer<LocalizableMessage>> returnToNormal) {
        boolean runtimeContext = isRuntimeContext(parentPoint, parentSource);
        if (raiseEventIf.test(contextPoint, contextPointVO)) {
            DataPointStateException dataPointStateException = DataPointStateException.newInstance(contextEntry, message, null, resourceBundle);
            if(runtimeContext)
                raiseEvent.apply(System.currentTimeMillis(), parentPoint).accept(message);
            return Optional.of(dataPointStateException);
        } else if(runtimeContext) {
            returnToNormal.apply(System.currentTimeMillis(), parentPoint).accept(message);
        }
        return Optional.empty();
    }


    private static boolean isRuntimeContext(DataPointRT dataPointRT, DataSourceRT dataSourceRT) {
        return dataSourceRT != null && dataPointRT != null;
    }
}
