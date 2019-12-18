package concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

public class ConcurrentTestUtil {

    private static Logger logger = LoggerFactory.getLogger(ConcurrentTestUtil.class);

    public static <A, R> void function(int numberOfLaunches, Function<A, R> fun, A key) throws InterruptedException {
        Executor executor = Executors.newFixedThreadPool(numberOfLaunches);
        Runnable action = () -> fun.apply(key);
        double time = MultiThreadEngine.test(executor, numberOfLaunches, action);
        logger.info("time: {}", time);
    }

    public static <T> void consumer(int numberOfLaunches, Consumer<T> fun, T key) throws InterruptedException {
        Executor executor = Executors.newFixedThreadPool(numberOfLaunches);
        Runnable action = () -> fun.accept(key);
        double time = MultiThreadEngine.test(executor, numberOfLaunches, action);
        logger.info("time: {}", time);
    }
}
