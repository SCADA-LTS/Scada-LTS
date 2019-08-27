package org.scada_lts.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Stream;

public enum BlockingQueueClasses {

    LINKED_BLOCKING_DEQUE("java.util.concurrent.LinkedBlockingDeque", LinkedBlockingDeque.class),
    LINKED_BLOCKING_QUEUE("java.util.concurrent.LinkedBlockingQueue", LinkedBlockingQueue.class),
    LINKED_TRANSFER_QUEUE("java.util.concurrent.LinkedTransferQueue", LinkedTransferQueue.class),
    PRIORITY_BLOCKING_QUEUE("java.util.concurrent.PriorityBlockingQueue", PriorityBlockingQueue.class),
    SYNCHRONOUS_QUEUE("java.util.concurrent.SynchronousQueue", SynchronousQueue.class);

    private String className;
    private Class<? extends BlockingQueue> blockingQueue;

    private static final Log log = LogFactory.getLog(BlockingQueuesUtil.class);

    BlockingQueueClasses(String className, Class<? extends BlockingQueue> blockingQueue) {
        this.className = className;
        this.blockingQueue = blockingQueue;
    }

    public Optional<BlockingQueue<Runnable>> newBlockingQueue() {
        try {
            return Optional.of(blockingQueue.newInstance());
        } catch (Throwable e) {
            log.error(e);
            return Optional.empty();
        }
    }

    public static BlockingQueueClasses of(String className) {
        return Stream.of(BlockingQueueClasses.values())
                .filter(a -> a.className.equals(className))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
