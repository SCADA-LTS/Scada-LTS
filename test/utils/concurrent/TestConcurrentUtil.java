package utils.concurrent;


import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class TestConcurrentUtil {

    public static <A, R> void function(int numberOfLaunches, Function<A, R> fun, A key) {
        Executor executor = Executors.newFixedThreadPool(numberOfLaunches);
        Runnable action = () -> fun.apply(key);
        MultiThreadEngine.execute(executor, numberOfLaunches, action);
    }

    public static <A, R> List<R> functionWithResult(int numberOfLaunches, Function<A, R> fun, A key) {
        Executor executor = Executors.newFixedThreadPool(numberOfLaunches);
        Callable<R> action = () -> fun.apply(key);
        return MultiThreadEngine.execute(executor, numberOfLaunches, action);
    }

    public static <A> void consumer(int numberOfLaunches, Consumer<A> fun, A key) {
        Executor executor = Executors.newFixedThreadPool(numberOfLaunches);
        Runnable action = () -> fun.accept(key);
        MultiThreadEngine.execute(executor, numberOfLaunches, action);
    }

    public static <R> void supplier(int numberOfLaunches, Supplier<R> fun) {
        Executor executor = Executors.newFixedThreadPool(numberOfLaunches);
        Runnable action = fun::get;
        MultiThreadEngine.execute(executor, numberOfLaunches, action);
    }

    public static <R> List<R> supplierWithResult(int numberOfLaunches, Supplier<R> fun) {
        Executor executor = Executors.newFixedThreadPool(numberOfLaunches);
        Callable<R> action = fun::get;
        return MultiThreadEngine.execute(executor, numberOfLaunches, action);
    }

    public static void supplierVoid(int numberOfLaunches, SupplierVoid fun) {
        Executor executor = Executors.newFixedThreadPool(numberOfLaunches);
        Runnable action = fun::execute;
        MultiThreadEngine.execute(executor, numberOfLaunches, action);
    }

    @FunctionalInterface
    public interface SupplierVoid {
        void execute();
    }
}
