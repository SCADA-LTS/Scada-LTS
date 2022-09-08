package utils;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.*;

public class TestConcurrentUtils {

    public static <A, R> void function(int numberOfLaunches, Function<A, R> fun, A key) {
        ExecutorService executor = Executors.newFixedThreadPool(numberOfLaunches);
        Runnable action = () -> fun.apply(key);
        MultiThreadEngine.execute(executor, numberOfLaunches, action);
        executor.shutdownNow();
    }

    public static <A, B, R> void biFunction(int numberOfLaunches, BiFunction<A, B, R> fun, A keyA, B keyB) {
        ExecutorService executor = Executors.newFixedThreadPool(numberOfLaunches);
        Runnable action = () -> fun.apply(keyA, keyB);
        MultiThreadEngine.execute(executor, numberOfLaunches, action);
        executor.shutdownNow();
    }

    public static <A, B> void biConsumer(int numberOfLaunches, BiConsumer<A, B> fun, A keyA, B keyB) {
        ExecutorService executor = Executors.newFixedThreadPool(numberOfLaunches);
        Runnable action = () -> fun.accept(keyA, keyB);
        MultiThreadEngine.execute(executor, numberOfLaunches, action);
        executor.shutdownNow();
    }

    public static <A, R> List<R> functionWithResult(int numberOfLaunches, Function<A, R> fun, A key) {
        ExecutorService executor = Executors.newFixedThreadPool(numberOfLaunches);
        Callable<R> action = () -> fun.apply(key);
        List<R> result = MultiThreadEngine.execute(executor, numberOfLaunches, action);
        executor.shutdownNow();
        return result;
    }

    public static <A> void consumer(int numberOfLaunches, Consumer<A> fun, A key) {
        ExecutorService executor = Executors.newFixedThreadPool(numberOfLaunches);
        Runnable action = () -> fun.accept(key);
        MultiThreadEngine.execute(executor, numberOfLaunches, action);
        executor.shutdownNow();
    }

    public static <R> void supplier(int numberOfLaunches, Supplier<R> fun) {
        ExecutorService executor = Executors.newFixedThreadPool(numberOfLaunches);
        Runnable action = fun::get;
        MultiThreadEngine.execute(executor, numberOfLaunches, action);
        executor.shutdownNow();
    }

    public static <R> List<R> supplierWithResult(int numberOfLaunches, Supplier<R> fun) {
        ExecutorService executor = Executors.newFixedThreadPool(numberOfLaunches);
        Callable<R> action = fun::get;
        List<R> result = MultiThreadEngine.execute(executor, numberOfLaunches, action);
        executor.shutdownNow();
        return result;
    }

    public static void supplierVoid(int numberOfLaunches, SupplierVoid fun) {
        ExecutorService executor = Executors.newFixedThreadPool(numberOfLaunches);
        Runnable action = fun::execute;
        MultiThreadEngine.execute(executor, numberOfLaunches, action);
        executor.shutdownNow();
    }

    @FunctionalInterface
    public interface SupplierVoid {
        void execute();
    }
}