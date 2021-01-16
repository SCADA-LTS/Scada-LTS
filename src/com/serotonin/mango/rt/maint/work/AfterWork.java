package com.serotonin.mango.rt.maint.work;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Set;

public interface AfterWork {

    enum AfterWorkLog {
        LOG;

        private final Log log = LogFactory.getLog(AfterWork.class);

        public void error(Object o, Throwable throwable) {
            log.error(o, throwable);
        }
    }

    default void workSuccess() {}
    default void workFinally(Set<Throwable> throwables) {}

    default void workError(Throwable throwable) {
        AfterWorkLog.LOG.error(throwable.getMessage(), throwable);
    }
}
