package org.scada_lts.utils;

import java.util.concurrent.*;

import static org.scada_lts.utils.CreateObjectUtils.newObject;

public final class BlockingQueuesUtils {

    private BlockingQueuesUtils() {}

    public static <T> BlockingQueue<T> newBlockingQueue(String className, BlockingQueue<T> defaultImpl, Object... args) {
        return newObject(className, defaultImpl, BlockingQueue.class, args);
    }
}
