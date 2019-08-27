package org.scada_lts.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.*;

public class BlockingQueuesUtil {

    private static final Log log = LogFactory.getLog(BlockingQueuesUtil.class);

    public static BlockingQueue<Runnable> newBlockingQueue(String className, BlockingQueue<Runnable> defaultImpl) {
        try {
            BlockingQueueClasses blockingQueues = BlockingQueueClasses.of(className);
            return blockingQueues.newBlockingQueue().orElse(defaultImpl);
        } catch (Throwable e) {
            log.error(e);
            return defaultImpl;
        }
    }

    public static BlockingQueue<Runnable> newBlockingQueue(BlockingQueueClasses blockingQueues) {
        return blockingQueues.newBlockingQueue().orElse(null);
    }
}
