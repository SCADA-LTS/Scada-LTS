package utils.concurrent;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

public class MultiThreadEngine {

    private static Logger logger = LoggerFactory.getLogger(MultiThreadEngine.class);

    public static double execute(final Executor executor, int numberOfLaunches, final List<Runnable> actions) throws InterruptedException {
        final CountDownLatch ready = new CountDownLatch(numberOfLaunches);
        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch done = new CountDownLatch(numberOfLaunches);
        final Iterator<Runnable> iterator = actions.iterator();
        for (int i = 0; i < numberOfLaunches; i++) {
            executor.execute(() -> {
                ready.countDown();
                try {
                    start.await();
                    iterator.next().run();
                } catch (InterruptedException ex) {
                    logger.error(ex.getMessage(), ex);
                } finally {
                    done.countDown();
                }
            });
        }
        ready.await();
        long startNanos = System.nanoTime();
        start.countDown();
        done.await();
        return (System.nanoTime() - startNanos)/1000000000.0;
    }

    public static double execute(final Executor executor, int concurrency, final Runnable action) throws InterruptedException {
        final CountDownLatch ready = new CountDownLatch(concurrency);
        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch done = new CountDownLatch(concurrency);
        for (int i = 0; i < concurrency; i++) {
            executor.execute(() -> {
                ready.countDown();
                try {
                    start.await();
                    action.run();
                } catch (InterruptedException ex) {
                    logger.error(ex.getMessage(), ex);
                } finally {
                    done.countDown();
                }
            });
        }
        ready.await();
        long startNanos = System.nanoTime();
        start.countDown();
        done.await();
        return (System.nanoTime() - startNanos)/1000000000.0;
    }
}
