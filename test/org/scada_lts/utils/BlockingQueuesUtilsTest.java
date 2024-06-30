package org.scada_lts.utils;

import org.junit.Test;

import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
import static org.scada_lts.utils.BlockingQueuesUtils.newBlockingQueue;

/**
 *
 * @author kamil.jarmusik@gmail.com
 *
 */

public class BlockingQueuesUtilsTest {

    @Test(expected = ClassCastException.class)
    public void invoke_add_from_PriorityBlockingQueue_for_Runnable_and_not_Comparable_then_ClassCastException() {
        //given:
        BlockingQueue<Runnable> blockingQueue = newBlockingQueue("java.util.concurrent.PriorityBlockingQueue",
                new PriorityBlockingQueue<>());
        //when:
        blockingQueue.add(() -> {});
    }

    @Test(expected = ClassCastException.class)
    public void twice_invoke_add_from_PriorityBlockingQueue_for_Runnable_and_not_Comparable_then_ClassCastException() {
        //given:
        BlockingQueue<Runnable> blockingQueue = newBlockingQueue("java.util.concurrent.PriorityBlockingQueue",
                new PriorityBlockingQueue<>());
        //when:
        blockingQueue.add(() -> {});
        blockingQueue.add(() -> {});
    }

    @Test(expected = ClassCastException.class)
    public void invoke_add_from_DelayQueue_for_Runnable_and_not_Comparable_then_ClassCastException() {
        //given:
        BlockingQueue<Runnable> blockingQueue = newBlockingQueue("java.util.concurrent.DelayQueue",
                new PriorityBlockingQueue<>());
        //when:
        blockingQueue.add(() -> {});
    }

    @Test(expected = ClassCastException.class)
    public void twice_invoke_add_from_DelayQueue_for_Runnable_and_not_Comparable_then_ClassCastException() {
        //given:
        BlockingQueue<Runnable> blockingQueue = newBlockingQueue("java.util.concurrent.DelayQueue",
                new PriorityBlockingQueue<>());
        //when:
        blockingQueue.add(() -> {});
        blockingQueue.add(() -> {});
    }

    @Test
    public void twice_invoke_add_from_ArrayBlockingQueue_without_args_for_Comparable_then_queue_2_size() {
        //given:
        BlockingQueue<Comparable> blockingQueue = newBlockingQueue("java.util.concurrent.ArrayBlockingQueue",
                new PriorityBlockingQueue<>());

        //when:
        blockingQueue.add(o -> 0);
        blockingQueue.add(o -> 1);

        //then:
        assertEquals(2, blockingQueue.size());
    }

    @Test
    public void twice_invoke_add_from_ArrayBlockingQueue_without_args_for_Comparable_then_queue_PriorityBlockingQueue() {
        //given:
        BlockingQueue<Comparable> blockingQueue = newBlockingQueue("java.util.concurrent.ArrayBlockingQueue",
                new PriorityBlockingQueue<>());

        //when:
        blockingQueue.add(o -> 0);
        blockingQueue.add(o -> 1);

        //then:
        assertEquals(PriorityBlockingQueue.class, blockingQueue.getClass());
    }

    @Test
    public void twice_invoke_add_from_LinkedBlockingQueue_for_Runnable_and_not_Comparable_then_queue_2_size() {
        //given:
        BlockingQueue<Runnable> blockingQueue = newBlockingQueue("java.util.concurrent.LinkedBlockingQueue",
                new PriorityBlockingQueue<>());
        //when:
        blockingQueue.add(() -> {});
        blockingQueue.add(() -> {});

        //then:
        assertEquals(2, blockingQueue.size());
    }

    @Test
    public void twice_invoke_add_from_ArrayBlockingQueue_for_Comparable_then_queue_ArrayBlockingQueue_2_size() {
        //given:
        BlockingQueue<Comparable> blockingQueue = newBlockingQueue("java.util.concurrent.ArrayBlockingQueue",
                new PriorityBlockingQueue<>(), 3);

        //when:
        blockingQueue.add(o -> 0);
        blockingQueue.add(o -> 1);

        //then:
        assertEquals(2, blockingQueue.size());
    }

    @Test
    public void twice_invoke_add_from_LinkedBlockingDeque_for_Runnable_and_not_Comparable_then_queue_LinkedBlockingDeque() {
        //given:
        BlockingQueue<Runnable> blockingQueue = newBlockingQueue("java.util.concurrent.LinkedBlockingDeque",
                new PriorityBlockingQueue<>());
        //when:
        blockingQueue.add(() -> {});
        blockingQueue.add(() -> {});

        //then:
        assertEquals(LinkedBlockingDeque.class, blockingQueue.getClass());
    }

    @Test
    public void twice_invoke_add_from_LinkedBlockingDeque_for_Runnable_and_not_Comparable_then_queue_2_size() {
        //given:
        BlockingQueue<Runnable> blockingQueue = newBlockingQueue("java.util.concurrent.LinkedBlockingDeque",
                new PriorityBlockingQueue<>());
        //when:
        blockingQueue.add(() -> {});
        blockingQueue.add(() -> {});

        //then:
        assertEquals(2, blockingQueue.size());
    }


    @Test
    public void twice_invoke_add_from_LinkedTransferQueue_for_Runnable_and_not_Comparable_then_queue_2_size() {
        //given:
        BlockingQueue<Runnable> blockingQueue = newBlockingQueue("java.util.concurrent.LinkedTransferQueue",
                new PriorityBlockingQueue<>());
        //when:
        blockingQueue.add(() -> {});
        blockingQueue.add(() -> {});

        //then:
        assertEquals(2, blockingQueue.size());
    }

    @Test
    public void invoke_newBlockingQueue_for_not_support_ArrayBlockingQueue_then_LinkedBlockingQueue() {
        //when:
        BlockingQueue<Comparable> blockingQueue = BlockingQueuesUtils
                .newBlockingQueue("java.util.concurrent.ArrayBlockingQueue",
                        new LinkedBlockingQueue<>(), 3);
        //then:
        assertEquals(ArrayBlockingQueue.class, blockingQueue.getClass());
    }

    @Test
    public void invoke_newBlockingQueue_for_LinkedBlockingDeque_then_LinkedBlockingDeque() {
        //when:
        BlockingQueue<Runnable> blockingQueue = BlockingQueuesUtils
                .newBlockingQueue("java.util.concurrent.LinkedBlockingDeque",
                        null);
        //then:
        assertEquals(LinkedBlockingDeque.class, blockingQueue.getClass());
    }

    @Test
    public void invoke_newBlockingQueue_for_LinkedBlockingQueue_then_LinkedBlockingQueue() {
        //when:
        BlockingQueue<Runnable> blockingQueue = BlockingQueuesUtils
                .newBlockingQueue("java.util.concurrent.LinkedBlockingQueue",
                        null);
        //then:
        assertEquals(LinkedBlockingQueue.class, blockingQueue.getClass());
    }

    @Test
    public void invoke_newBlockingQueue_for_LinkedTransferQueue_then_LinkedTransferQueue() {
        //when:
        BlockingQueue<Runnable> blockingQueue = BlockingQueuesUtils
                .newBlockingQueue("java.util.concurrent.LinkedTransferQueue",
                        null);
        //then:
        assertEquals(LinkedTransferQueue.class, blockingQueue.getClass());
    }

    @Test
    public void invoke_newBlockingQueue_for_not_support_DelayQueue_then_LinkedBlockingQueue() {
        //when:
        BlockingQueue<Runnable> blockingQueue = BlockingQueuesUtils
                .newBlockingQueue("java.util.concurrent.DelayQueue",
                        new LinkedBlockingQueue<>());
        //then:
        assertEquals(DelayQueue.class, blockingQueue.getClass());
    }

    @Test
    public void invoke_newBlockingQueue_for_not_support_PriorityBlockingQueue_then_PriorityBlockingQueue() {
        //when:
        BlockingQueue<Runnable> blockingQueue = BlockingQueuesUtils
                .newBlockingQueue("java.util.concurrent.PriorityBlockingQueue",
                        new LinkedBlockingQueue<>());
        //then:
        assertEquals(PriorityBlockingQueue.class, blockingQueue.getClass());
    }

    @Test
    public void invoke_newBlockingQueue_for_SynchronousQueue_then_SynchronousQueue() {
        //when:
        BlockingQueue<Runnable> blockingQueue = BlockingQueuesUtils
                .newBlockingQueue("java.util.concurrent.SynchronousQueue",
                        null);
        //then:
        assertEquals(SynchronousQueue.class, blockingQueue.getClass());
    }

    @Test
    public void invoke_newBlockingQueue_for_class_not_implements_BlockingQueue_then_LinkedBlockingQueue() {
        //when:
        BlockingQueue<Runnable> blockingQueue = BlockingQueuesUtils
                .newBlockingQueue("java.util.List",
                        new LinkedBlockingQueue<>());
        //then:
        assertEquals(LinkedBlockingQueue.class, blockingQueue.getClass());
    }


    @Test
    public void invoke_newBlockingQueue_for_ArrayList_class_not_implements_BlockingQueue_then_LinkedBlockingQueue() {
        //when:
        BlockingQueue<Runnable> blockingQueue = BlockingQueuesUtils
                .newBlockingQueue("java.util.ArrayList",
                        new LinkedBlockingQueue<>());
        //then:
        assertEquals(LinkedBlockingQueue.class, blockingQueue.getClass());
    }

    @Test
    public void invoke_newBlockingQueue_for_not_exists_class_then_LinkedBlockingQueue() {
        //when:
        BlockingQueue<Runnable> blockingQueue = BlockingQueuesUtils
                .newBlockingQueue("java.util.concurrent.test",
                        new LinkedBlockingQueue<>());
        //then:
        assertEquals(LinkedBlockingQueue.class, blockingQueue.getClass());
    }



}