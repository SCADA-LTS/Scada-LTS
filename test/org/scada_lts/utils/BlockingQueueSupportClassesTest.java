package org.scada_lts.utils;

import org.junit.Test;

import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author kamil.jarmusik@gmail.com
 *
 */

public class BlockingQueueSupportClassesTest {

    @Test
    public void invoke_newBlockingQueue_for_LINKED_BLOCKING_QUEUE_then_LinkedBlockingQueueClass() {
        //when:
        BlockingQueue<Runnable> queue =
                BlockingQueueSupportClasses.LINKED_BLOCKING_QUEUE
                        .newBlockingQueue().orElse(null);
        //then:
        assertEquals(LinkedBlockingQueue.class, queue.getClass());
    }

    @Test
    public void invoke_newBlockingQueue_for_LINKED_BLOCKING_DEQUE_then_LinkedBlockingDequeClass() {
        //when:
        BlockingQueue<Runnable> queue =
                BlockingQueueSupportClasses.LINKED_BLOCKING_DEQUE
                        .newBlockingQueue().orElse(null);
        //then:
        assertEquals(LinkedBlockingDeque.class, queue.getClass());
    }

    @Test
    public void invoke_newBlockingQueue_for_SYNCHRONOUS_QUEUE_then_SynchronousQueueClass() {
        //when:
        BlockingQueue<Runnable> queue =
                BlockingQueueSupportClasses.SYNCHRONOUS_QUEUE
                        .newBlockingQueue().orElse(null);
        //then:
        assertEquals(SynchronousQueue.class, queue.getClass());
    }

    @Test
    public void invoke_newBlockingQueue_for_LINKED_TRANSFER_QUEUE_then_LinkedTransferQueueClass() {
        //when:
        BlockingQueue<Runnable> queue =
                BlockingQueueSupportClasses.LINKED_TRANSFER_QUEUE
                        .newBlockingQueue().orElse(null);
        //then:
        assertEquals(LinkedTransferQueue.class, queue.getClass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void invoke_of_for_not_exists_value_then_IllegalArgumentException() {
        //when:
        BlockingQueue<Runnable> queue =
                BlockingQueueSupportClasses.of("java.util.List")
                        .newBlockingQueue().orElse(null);
    }

    @Test
    public void invoke_of_for_LINKED_BLOCKING_QUEUE_then_LinkedBlockingQueueClass() {
        //when:
        BlockingQueueSupportClasses queue =
                BlockingQueueSupportClasses.of("java.util.concurrent.LinkedBlockingQueue");
        //then:
        assertEquals(BlockingQueueSupportClasses.LINKED_BLOCKING_QUEUE, queue);
    }

    @Test
    public void invoke_of_for_LINKED_BLOCKING_DEQUE_then_LinkedBlockingDequeClass() {
        //when:
        BlockingQueueSupportClasses queue =
                BlockingQueueSupportClasses.of("java.util.concurrent.LinkedBlockingDeque");
        //then:
        assertEquals(BlockingQueueSupportClasses.LINKED_BLOCKING_DEQUE, queue);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invoke_of_for_PRIORITY_BLOCKING_QUEUE_then_PriorityBlockingQueueClass() {
        //when:
        BlockingQueueSupportClasses queue =
                BlockingQueueSupportClasses.of("java.util.concurrent.PriorityBlockingQueue");
    }

    @Test
    public void invoke_of_for_SYNCHRONOUS_QUEUE_then_SynchronousQueueClass() {
        //when:
        BlockingQueueSupportClasses queue =
                BlockingQueueSupportClasses.of("java.util.concurrent.SynchronousQueue");
        //then:
        assertEquals(BlockingQueueSupportClasses.SYNCHRONOUS_QUEUE, queue);
    }

    @Test
    public void invoke_of_for_LINKED_TRANSFER_QUEUE_then_LinkedTransferQueueClass() {
        //when:
        BlockingQueueSupportClasses queue =
                BlockingQueueSupportClasses.of("java.util.concurrent.LinkedTransferQueue");
        //then:
        assertEquals(BlockingQueueSupportClasses.LINKED_TRANSFER_QUEUE, queue);
    }
}