package org.scada_lts.utils;

import org.junit.Test;

import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.scada_lts.utils.BlockingQueuesUtil.newBlockingQueue;

/**
 *
 * @author kamil.jarmusik@gmail.com
 *
 */

public class BlockingQueuesUtilTest {

    @Test(expected = ClassCastException.class)
    public void invoke_add_from_PRIORITY_BLOCKING_QUEUE_for_Runnable_and_not_Comparable_then_ClassCastException() {
        //given:
        BlockingQueue<Runnable> blockingQueue = newBlockingQueue("java.util.concurrent.PriorityBlockingQueue",
                new PriorityBlockingQueue<>());
        //when:
        blockingQueue.add(() -> {});
    }

    @Test(expected = ClassCastException.class)
    public void twice_invoke_add_from_PRIORITY_BLOCKING_QUEUE_for_Runnable_and_not_Comparable_then_ClassCastException() {
        //given:
        BlockingQueue<Runnable> blockingQueue = newBlockingQueue("java.util.concurrent.PriorityBlockingQueue",
                new PriorityBlockingQueue<>());
        //when:
        blockingQueue.add(() -> {});
        blockingQueue.add(() -> {});
    }

    @Test
    public void invoke_newBlockingQueue_from_SYNCHRONOUS_QUEUE_then_size_0() {
        //when:
        BlockingQueue<Runnable> blockingQueue = newBlockingQueue(BlockingQueueSupportClasses.SYNCHRONOUS_QUEUE);
        //then:
        assertEquals(0, blockingQueue.size());
    }

    @Test
    public void invoke_poll_from_SYNCHRONOUS_QUEUE_for_Runnable_then_size_0() throws InterruptedException {
        //given:
        BlockingQueue<Runnable> blockingQueue = newBlockingQueue(BlockingQueueSupportClasses.SYNCHRONOUS_QUEUE);
        //when:
        blockingQueue.poll(1, TimeUnit.SECONDS);
        //then:
        assertEquals(0, blockingQueue.size());
    }

    @Test
    public void twice_invoke_remove_from_SYNCHRONOUS_QUEUE_for_Runnable_then_size_0() {
        //given:
        BlockingQueue<Runnable> blockingQueue = newBlockingQueue(BlockingQueueSupportClasses.SYNCHRONOUS_QUEUE);
        //when:
        blockingQueue.remove((Runnable)() -> {});
        blockingQueue.remove((Runnable)() -> {});
        //then:
        assertEquals(0, blockingQueue.size());
    }

    @Test
    public void invoke_remove_from_SYNCHRONOUS_QUEUE_for_Runnable_then_empty() {
        //given:
        BlockingQueue<Runnable> blockingQueue = newBlockingQueue(BlockingQueueSupportClasses.SYNCHRONOUS_QUEUE);
        Runnable runnable = () -> {};
        //when:
        blockingQueue.remove(runnable);
        //then:
        assertTrue(blockingQueue.isEmpty());
    }

    @Test
    public void invoke_add_from_LINKED_BLOCKING_DEQUE_for_Runnable_then_size_1() {
        //given:
        BlockingQueue<Runnable> blockingQueue = newBlockingQueue(BlockingQueueSupportClasses.LINKED_BLOCKING_DEQUE);
        //when:
        blockingQueue.add(() -> {});
        //then:
        assertTrue(!blockingQueue.isEmpty());
        assertEquals(1, blockingQueue.size());
    }

    @Test
    public void twice_invoke_add_from_LINKED_BLOCKING_DEQUE_for_Runnable_then_size_2() {
        //given:
        BlockingQueue<Runnable> blockingQueue = newBlockingQueue(BlockingQueueSupportClasses.LINKED_BLOCKING_DEQUE);
        //when:
        blockingQueue.add(() -> {});
        blockingQueue.add(() -> {});
        //then:
        assertTrue(!blockingQueue.isEmpty());
        assertEquals(2, blockingQueue.size());
    }

    @Test
    public void invoke_remove_from_LINKED_BLOCKING_DEQUE_for_Runnable_then_empty() {
        //given:
        BlockingQueue<Runnable> blockingQueue = newBlockingQueue(BlockingQueueSupportClasses.LINKED_BLOCKING_DEQUE);
        Runnable runnable = () -> {};
        blockingQueue.add(runnable);
        //when:
        blockingQueue.remove(runnable);
        //then:
        assertTrue(blockingQueue.isEmpty());
    }

    @Test
    public void invoke_add_from_LINKED_BLOCKING_QUEUE_for_Runnable_then_size_1() {
        //given:
        BlockingQueue<Runnable> blockingQueue = newBlockingQueue(BlockingQueueSupportClasses.LINKED_BLOCKING_QUEUE);
        //when:
        blockingQueue.add(() -> {});
        //then:
        assertTrue(!blockingQueue.isEmpty());
        assertEquals(1, blockingQueue.size());
    }

    @Test
    public void twice_invoke_add_from_LINKED_BLOCKING_QUEUE_for_Runnable_then_size_2() {
        //given:
        BlockingQueue<Runnable> blockingQueue = newBlockingQueue(BlockingQueueSupportClasses.LINKED_BLOCKING_QUEUE);
        //when:
        blockingQueue.add(() -> {});
        blockingQueue.add(() -> {});
        //then:
        assertTrue(!blockingQueue.isEmpty());
        assertEquals(2, blockingQueue.size());
    }

    @Test
    public void invoke_remove_from_LINKED_BLOCKING_QUEUE_for_Runnable_then_empty() {
        //given:
        BlockingQueue<Runnable> blockingQueue = newBlockingQueue(BlockingQueueSupportClasses.LINKED_BLOCKING_QUEUE);
        Runnable runnable = () -> {};
        blockingQueue.add(runnable);
        //when:
        blockingQueue.remove(runnable);
        //then:
        assertTrue(blockingQueue.isEmpty());
    }

    @Test
    public void invoke_newBlockingQueue_for_not_support_ArrayBlockingQueue_then_LinkedBlockingQueue() {
        //when:
        BlockingQueue<Runnable> blockingQueue = BlockingQueuesUtil
                .newBlockingQueue("java.util.concurrent.ArrayBlockingQueue",
                        new LinkedBlockingQueue<>());
        //then:
        assertEquals(LinkedBlockingQueue.class, blockingQueue.getClass());
    }

    @Test
    public void invoke_newBlockingQueue_for_LinkedBlockingDeque_then_LinkedBlockingDeque() {
        //when:
        BlockingQueue<Runnable> blockingQueue = BlockingQueuesUtil
                .newBlockingQueue("java.util.concurrent.LinkedBlockingDeque",
                        null);
        //then:
        assertEquals(LinkedBlockingDeque.class, blockingQueue.getClass());
    }

    @Test
    public void invoke_newBlockingQueue_for_LinkedBlockingQueue_then_LinkedBlockingQueue() {
        //when:
        BlockingQueue<Runnable> blockingQueue = BlockingQueuesUtil
                .newBlockingQueue("java.util.concurrent.LinkedBlockingQueue",
                        null);
        //then:
        assertEquals(LinkedBlockingQueue.class, blockingQueue.getClass());
    }

    @Test
    public void invoke_newBlockingQueue_for_LinkedTransferQueue_then_LinkedTransferQueue() {
        //when:
        BlockingQueue<Runnable> blockingQueue = BlockingQueuesUtil
                .newBlockingQueue("java.util.concurrent.LinkedTransferQueue",
                        null);
        //then:
        assertEquals(LinkedTransferQueue.class, blockingQueue.getClass());
    }

    @Test
    public void invoke_newBlockingQueue_for_not_support_DelayQueue_then_LinkedBlockingQueue() {
        //when:
        BlockingQueue<Runnable> blockingQueue = BlockingQueuesUtil
                .newBlockingQueue("java.util.concurrent.DelayQueue",
                        new LinkedBlockingQueue<>());
        //then:
        assertEquals(LinkedBlockingQueue.class, blockingQueue.getClass());
    }

    @Test
    public void invoke_newBlockingQueue_for_not_support_PriorityBlockingQueue_then_PriorityBlockingQueue() {
        //when:
        BlockingQueue<Runnable> blockingQueue = BlockingQueuesUtil
                .newBlockingQueue("java.util.concurrent.PriorityBlockingQueue",
                        new LinkedBlockingQueue<>());
        //then:
        assertEquals(LinkedBlockingQueue.class, blockingQueue.getClass());
    }

    @Test
    public void invoke_newBlockingQueue_for_SynchronousQueue_then_SynchronousQueue() {
        //when:
        BlockingQueue<Runnable> blockingQueue = BlockingQueuesUtil
                .newBlockingQueue("java.util.concurrent.SynchronousQueue",
                        null);
        //then:
        assertEquals(SynchronousQueue.class, blockingQueue.getClass());
    }

    @Test
    public void invoke_newBlockingQueue_for_class_not_implements_BlockingQueue_then_LinkedBlockingQueue() {
        //when:
        BlockingQueue<Runnable> blockingQueue = BlockingQueuesUtil
                .newBlockingQueue("java.util.List",
                        new LinkedBlockingQueue<>());
        //then:
        assertEquals(LinkedBlockingQueue.class, blockingQueue.getClass());
    }

    @Test
    public void invoke_newBlockingQueue_for_not_exists_class_then_LinkedBlockingQueue() {
        //when:
        BlockingQueue<Runnable> blockingQueue = BlockingQueuesUtil
                .newBlockingQueue("java.util.concurrent.test",
                        new LinkedBlockingQueue<>());
        //then:
        assertEquals(LinkedBlockingQueue.class, blockingQueue.getClass());
    }



}