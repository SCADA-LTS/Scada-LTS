package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

public class MultiThreadEngine {

    private static Logger logger = LoggerFactory.getLogger(MultiThreadEngine.class);

    public static void execute(final Executor executor, int concurrency, final Runnable action) {
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
        try {
            ready.await();
            long startNanos = System.nanoTime();
            start.countDown();
            done.await();
            logger.info("time: {}", (System.nanoTime() - startNanos)/1000000000.0);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public static <R> List<R> execute(final Executor executor, int concurrency, final Callable<R> action) {
        final CountDownLatch ready = new CountDownLatch(concurrency);
        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch done = new CountDownLatch(concurrency);
        final List<R> results = new CopyOnWriteArrayList<>();
        for (int i = 0; i < concurrency; i++) {
            executor.execute(() -> {
                ready.countDown();
                try {
                    start.await();
                    R result = action.call();
                    results.add(result);
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                } finally {
                    done.countDown();
                }
            });
        }
        try {
            ready.await();
            long startNanos = System.nanoTime();
            start.countDown();
            done.await();
            logger.info("time: {}", (System.nanoTime() - startNanos) / 1000000000.0);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return results;
    }

    public static void execute(final Executor executor, int concurrency, final List<Runnable> actions) {
        final CountDownLatch ready = new CountDownLatch(concurrency * actions.size());
        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch done = new CountDownLatch(concurrency * actions.size());
        for (int i = 0; i < concurrency; i++) {
            for (Runnable runnable : actions) {
                executor.execute(() -> {
                    ready.countDown();
                    try {
                        start.await();
                        runnable.run();
                    } catch (InterruptedException ex) {
                        logger.error(ex.getMessage(), ex);
                    } finally {
                        done.countDown();
                    }
                });
            }
        }
        try {
            ready.await();
            long startNanos = System.nanoTime();
            start.countDown();
            done.await();
            logger.info("time: {}", (System.nanoTime() - startNanos)/1000000000.0);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}