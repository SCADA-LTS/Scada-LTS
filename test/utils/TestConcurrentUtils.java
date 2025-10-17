package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.*;
import java.util.stream.Collectors;

public class TestConcurrentUtils {

    public static <A, R> void function(int numberOfLaunches, Function<A, R> fun, A key) throws RuntimeException {
        try {
            functionCheck(numberOfLaunches, fun, key);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static <A, R> void functionCheck(int numberOfLaunches, Function<A, R> fun, A key) throws Throwable {
        ExecutorService executor = Executors.newFixedThreadPool(numberOfLaunches);
        Runnable action = () -> fun.apply(key);
        try {
            MultiThreadEngine.execute(executor, numberOfLaunches, action);
        } finally {
            executor.shutdownNow();
        }
    }

    public static <A, B, R> void biFunction(int numberOfLaunches, BiFunction<A, B, R> fun, A keyA, B keyB) throws RuntimeException {
        try {
            biFunctionCheck(numberOfLaunches, fun, keyA, keyB);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static <A, B, R> void biFunctionCheck(int numberOfLaunches, BiFunction<A, B, R> fun, A keyA, B keyB) throws Throwable {
        ExecutorService executor = Executors.newFixedThreadPool(numberOfLaunches);
        Runnable action = () -> fun.apply(keyA, keyB);
        try {
            MultiThreadEngine.execute(executor, numberOfLaunches, action);
        } finally {
            executor.shutdownNow();
        }
    }

    public static <A, B> void biConsumer(int numberOfLaunches, BiConsumer<A, B> fun, A keyA, B keyB) throws RuntimeException {
        try {
            biConsumerCheck(numberOfLaunches, fun, keyA, keyB);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static <A, B> void biConsumerCheck(int numberOfLaunches, BiConsumer<A, B> fun, A keyA, B keyB) throws Throwable {
        ExecutorService executor = Executors.newFixedThreadPool(numberOfLaunches);
        Runnable action = () -> fun.accept(keyA, keyB);
        try {
            MultiThreadEngine.execute(executor, numberOfLaunches, action);
        } finally {
            executor.shutdownNow();
        }
    }

    public static void biConsumer(int numberOfLaunches, List<SupplierVoid> actions) throws RuntimeException {
        try {
            biConsumerCheck(numberOfLaunches, actions);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void biConsumerCheck(int numberOfLaunches, List<SupplierVoid> actions) throws Throwable {
        ExecutorService executor = Executors.newFixedThreadPool(numberOfLaunches * actions.size());
        List<Runnable> runnables = new ArrayList<>();
        for(SupplierVoid action: actions) {
            runnables.add(action::execute);
        }
        try {
            MultiThreadEngine.execute(executor, numberOfLaunches, runnables);
        } finally {
            executor.shutdownNow();
        }
    }

    public static <A, R> List<R> functionWithResult(int numberOfLaunches, Function<A, R> fun, A key) throws RuntimeException {
        try {
            return functionWithResultCheck(numberOfLaunches, fun, key);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static <A, R> List<R> functionWithResultCheck(int numberOfLaunches, Function<A, R> fun, A key) throws Throwable {
        ExecutorService executor = Executors.newFixedThreadPool(numberOfLaunches);
        Callable<R> action = () -> fun.apply(key);
        List<R> result = null;
        try {
            result = MultiThreadEngine.execute(executor, numberOfLaunches, action);
        } finally {
            executor.shutdownNow();
        }
        return result;
    }

    public static <A, R, N> List<N> functionWithResult(int numberOfLaunches, Function<A, R> fun, A key, Function<R, N> converter) throws RuntimeException {
        try {
            return functionWithResultCheck(numberOfLaunches, fun, key, converter);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static <A, R, N> List<N> functionWithResultCheck(int numberOfLaunches, Function<A, R> fun, A key, Function<R, N> converter) throws Throwable {
        ExecutorService executor = Executors.newFixedThreadPool(numberOfLaunches);
        Callable<R> action = () -> fun.apply(key);
        List<R> result = null;
        try {
            result = MultiThreadEngine.execute(executor, numberOfLaunches, action);
        } finally {
            executor.shutdownNow();
        }
        return result.stream().map(converter).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static <A> void consumer(int numberOfLaunches, Consumer<A> fun, A key) throws RuntimeException {
        try {
            consumerCheck(numberOfLaunches, fun, key);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static <A> void consumerCheck(int numberOfLaunches, Consumer<A> fun, A key) throws Throwable {
        ExecutorService executor = Executors.newFixedThreadPool(numberOfLaunches);
        Runnable action = () -> fun.accept(key);
        try {
            MultiThreadEngine.execute(executor, numberOfLaunches, action);
        } finally {
            executor.shutdownNow();
        }
    }

    public static <R> void supplier(int numberOfLaunches, Supplier<R> fun) throws RuntimeException {
        try {
            supplierCheck(numberOfLaunches, fun);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static <R> void supplierCheck(int numberOfLaunches, Supplier<R> fun) throws Throwable {
        ExecutorService executor = Executors.newFixedThreadPool(numberOfLaunches);
        Runnable action = fun::get;
        try {
            MultiThreadEngine.execute(executor, numberOfLaunches, action);
        } finally {
            executor.shutdownNow();
        }
    }

    public static <R> List<R> supplierWithResult(int numberOfLaunches, Supplier<R> fun) throws RuntimeException {
        try {
            return supplierWithResultCheck(numberOfLaunches, fun);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static <R> List<R> supplierWithResultCheck(int numberOfLaunches, Supplier<R> fun) throws Throwable {
        ExecutorService executor = Executors.newFixedThreadPool(numberOfLaunches);
        Callable<R> action = fun::get;
        List<R> result = null;
        try {
            result = MultiThreadEngine.execute(executor, numberOfLaunches, action);
        } finally {
            executor.shutdownNow();
        }
        return result;
    }

    public static void supplierVoid(int numberOfLaunches, SupplierVoid fun) throws RuntimeException {
        try {
            supplierVoidCheck(numberOfLaunches, fun);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void supplierVoidCheck(int numberOfLaunches, SupplierVoid fun) throws Throwable {
        ExecutorService executor = Executors.newFixedThreadPool(numberOfLaunches);
        Runnable action = fun::execute;
        try {
            MultiThreadEngine.execute(executor, numberOfLaunches, action);
        } finally {
            executor.shutdownNow();
        }
    }

    @FunctionalInterface
    public interface SupplierVoid {
        void execute();
    }

    public static class BiConsumerAction<A, B> implements SupplierVoid {
        private final BiConsumer<A, B> fun;
        A keyA;
        B keyB;

        public BiConsumerAction(BiConsumer<A, B> fun, A keyA, B keyB) {
            this.fun = fun;
            this.keyA = keyA;
            this.keyB = keyB;
        }

        @Override
        public void execute() {
            fun.accept(keyA, keyB);
        }
    }

    public static class ConsumerAction<A> implements SupplierVoid {
        private final Consumer<A> fun;
        A keyA;

        public ConsumerAction(Consumer<A> fun, A keyA) {
            this.fun = fun;
            this.keyA = keyA;
        }
        @Override
        public void execute() {
            fun.accept(keyA);
        }
    }

    public static class FunctionAction<A, R> implements SupplierVoid {
        private final Function<A, R> fun;
        A keyA;

        public FunctionAction(Function<A, R> fun, A keyA) {
            this.fun = fun;
            this.keyA = keyA;
        }
        @Override
        public void execute() {
            fun.apply(keyA);
        }
    }

    public static class BiFunctionAction<A, B, R> implements SupplierVoid {
        private final BiFunction<A, B, R> fun;
        A keyA;
        B keyB;

        public BiFunctionAction(BiFunction<A, B, R> fun, A keyA, B keyB) {
            this.fun = fun;
            this.keyA = keyA;
            this.keyB = keyB;
        }
        @Override
        public void execute() {
            fun.apply(keyA, keyB);
        }
    }
}