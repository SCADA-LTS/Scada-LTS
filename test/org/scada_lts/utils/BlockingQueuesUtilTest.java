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

    @Test
    public void invoke_add_from_BlockingQueue_for_Runnable_then_size_one() {
        //given:
        BlockingQueue<Runnable> blockingQueue = newBlockingQueue(BlockingQueueClasses.LINKED_BLOCKING_QUEUE);
        //when:
        blockingQueue.add(() -> {});
        //then:
        assertTrue(!blockingQueue.isEmpty());
        assertEquals(1, blockingQueue.size());
    }

    @Test
    public void invoke_remove_from_BlockingQueue_for_Runnable_then_empty() {
        //given:
        BlockingQueue<Runnable> blockingQueue = newBlockingQueue(BlockingQueueClasses.LINKED_BLOCKING_QUEUE);
        Runnable runnable = () -> {};
        blockingQueue.add(runnable);
        //when:
        blockingQueue.remove(runnable);
        //then:
        assertTrue(blockingQueue.isEmpty());
    }

    @Test
    public void invoke_newBlockingQueue_for_ArrayBlockingQueue_then_LinkedBlockingQueue() {
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
    public void invoke_newBlockingQueue_for_DelayQueue_then_LinkedBlockingQueue() {
        //when:
        BlockingQueue<Runnable> blockingQueue = BlockingQueuesUtil
                .newBlockingQueue("java.util.concurrent.DelayQueue",
                        new LinkedBlockingQueue<>());
        //then:
        assertEquals(LinkedBlockingQueue.class, blockingQueue.getClass());
    }

    @Test
    public void invoke_newBlockingQueue_for_PriorityBlockingQueue_then_PriorityBlockingQueue() {
        //when:
        BlockingQueue<Runnable> blockingQueue = BlockingQueuesUtil
                .newBlockingQueue("java.util.concurrent.PriorityBlockingQueue",
                        null);
        //then:
        assertEquals(PriorityBlockingQueue.class, blockingQueue.getClass());
    }

    @Test
    public void invoke_newBlockingQueue_for_SynchronousQueue_then_SynchronousQueue() {
        //when:
        BlockingQueue<Runnable> blockingQueue = BlockingQueuesUtil
                .newBlockingQueue("java.util.concurrent.SynchronousQueue",
                        new LinkedBlockingQueue<>());
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