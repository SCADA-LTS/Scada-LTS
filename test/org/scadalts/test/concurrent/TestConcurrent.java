package org.scadalts.test.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class TestConcurrent {

    private static Logger logger = LoggerFactory.getLogger(TestConcurrent.class);

    public static <A, R> void function(int numberOfLaunches, Function<A, R> fun, A key) throws InterruptedException {
        Executor executor = Executors.newFixedThreadPool(numberOfLaunches);
        Runnable action = () -> fun.apply(key);
        double time = MultiThreadEngine.execute(executor, numberOfLaunches, action);
        logger.info("time: {}", time);
    }

    public static <T> void consumer(int numberOfLaunches, Consumer<T> fun, T key) throws InterruptedException {
        Executor executor = Executors.newFixedThreadPool(numberOfLaunches);
        Runnable action = () -> fun.accept(key);
        double time = MultiThreadEngine.execute(executor, numberOfLaunches, action);
        logger.info("time: {}", time);
    }

    public static <T> void supplier(int numberOfLaunches, Supplier<T> fun) throws InterruptedException {
        Executor executor = Executors.newFixedThreadPool(numberOfLaunches);
        Runnable action = fun::get;
        double time = MultiThreadEngine.execute(executor, numberOfLaunches, action);
        logger.info("time: {}", time);
    }

    public static void supplierVoid(int numberOfLaunches, SupplierVoid fun) throws InterruptedException {
        Executor executor = Executors.newFixedThreadPool(numberOfLaunches);
        Runnable action = fun::execute;
        double time = MultiThreadEngine.execute(executor, numberOfLaunches, action);
        logger.info("time: {}", time);
    }

    @FunctionalInterface
    public interface SupplierVoid {
        void execute();
    }
}
