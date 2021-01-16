package com.serotonin.mango.rt.maint.work;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashSet;
import java.util.Set;

abstract class AbstractBeforeAfterWorkItem implements WorkItem, BeforeWork, AfterWork {

    private static final Log log = LogFactory.getLog(AbstractBeforeAfterWorkItem.class);

    @Override
    public final void execute() {
        Set<Throwable> throwables = new HashSet<>();
        try {
            try {
                beforeWork();
            } catch (Throwable throwable) {
                throwables.add(throwable);
                log.error(throwable.getMessage(), throwable);
                return;
            }
            try {
                work();
            } catch (Throwable throwable) {
                throwables.add(throwable);
                workError(throwable);
                return;
            }
            workSuccess();
        } catch (Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
            throwables.add(throwable);
        } finally {
            try {
                workFinally(throwables);
            } catch (Throwable throwable) {
                log.error(throwable.getMessage(), throwable);
            }
        }
    }

    public abstract void work();

    @Override
    public void beforeWork() {}

    @Override
    public void workError(Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
    }

    @Override
    public void workSuccess() {}

    @Override
    public void workFinally(Set<Throwable> throwables) {}
}
