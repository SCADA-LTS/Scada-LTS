package com.serotonin.mango.rt.event.type;

/**
 * Class created by Arkadiusz Parafiniuk
 *
 * @Author arkadiusz.parafiniuk@gmail.com
 */
public class UserLockEventType extends EventType {
    @Override
    public int getEventSourceId() {
        return EventSources.USER_LOCK;
    }

    @Override
    public int getDuplicateHandling() {
        return DuplicateHandling.IGNORE;
    }

    @Override
    public int getReferenceId1() {
        return 0;
    }

    @Override
    public int getReferenceId2() {
        return 0;
    }
}
