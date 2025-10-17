package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class MultiThreadEngine {

    private static final Logger LOG = LoggerFactory.getLogger(MultiThreadEngine.class);

    public static void execute(final Executor executor, int concurrency, final Runnable action) throws Throwable {
        final CountDownLatch ready = new CountDownLatch(concurrency);
        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch done = new CountDownLatch(concurrency);
        final Set<Throwable> throwableMap = new CopyOnWriteArraySet<>();
        for (int i = 0; i < concurrency; i++) {
            executor.execute(() -> {
                ready.countDown();
                try {
                    start.await();
                    action.run();
                } catch (Throwable ex) {
                    throwableMap.add(ex);
                } finally {
                    done.countDown();
                }
            });
        }
        long startNanos = 0;
        try {
            ready.await();
            startNanos = System.nanoTime();
            start.countDown();
            done.await();
            if(!throwableMap.isEmpty()) {
                throw throwableMap.iterator().next();
            }
        } catch (Throwable ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        } finally {
            LOG.info("time: {}", (System.nanoTime() - startNanos) / 1000000000.0);
        }
    }

    public static <R> List<R> execute(final Executor executor, int concurrency, final Callable<R> action) throws Throwable {
        final CountDownLatch ready = new CountDownLatch(concurrency);
        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch done = new CountDownLatch(concurrency);
        final List<R> results = new CopyOnWriteArrayList<>();
        final Set<Throwable> throwableMap = new CopyOnWriteArraySet<>();
        for (int i = 0; i < concurrency; i++) {
            executor.execute(() -> {
                ready.countDown();
                try {
                    start.await();
                    R result = action.call();
                    results.add(result);
                } catch (Throwable ex) {
                    throwableMap.add(ex);
                } finally {
                    done.countDown();
                }
            });
        }
        long startNanos = 0;
        try {
            ready.await();
            startNanos = System.nanoTime();
            start.countDown();
            done.await();

            if(!throwableMap.isEmpty()) {
                throw throwableMap.iterator().next();
            }
        } catch (Throwable ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        } finally {
            LOG.info("time: {}", (System.nanoTime() - startNanos) / 1000000000.0);
        }
        return results;
    }

    public static void execute(final Executor executor, int concurrency, final List<Runnable> actions) throws Throwable {
        final CountDownLatch ready = new CountDownLatch(concurrency * actions.size());
        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch done = new CountDownLatch(concurrency * actions.size());
        final Set<Throwable> throwableMap = new CopyOnWriteArraySet<>();
        for (int i = 0; i < concurrency; i++) {
            for (Runnable runnable : actions) {
                executor.execute(() -> {
                    ready.countDown();
                    try {
                        start.await();
                        runnable.run();
                    } catch (Throwable ex) {
                        throwableMap.add(ex);
                    } finally {
                        done.countDown();
                    }
                });
            }
        }
        long startNanos = 0;
        try {
            ready.await();
            startNanos = System.nanoTime();
            start.countDown();
            done.await();
            if(!throwableMap.isEmpty()) {
                throw throwableMap.iterator().next();
            }
        } catch (Throwable ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        } finally {
            LOG.info("time: {}", (System.nanoTime() - startNanos)/1000000000.0);
        }
    }
}