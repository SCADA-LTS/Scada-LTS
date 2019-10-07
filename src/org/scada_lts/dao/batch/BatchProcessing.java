package org.scada_lts.dao.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class BatchProcessing {

    public static <T> void preprocess(Consumer<List<T>> preprocess,
                                   BiFunction<Long, Limit<Integer>, List<T>> objectsToProcess,
                                   int batchSize) {
        preprocess(preprocess, objectsToProcess, batchSize, 300);
    }

    public static <T> void preprocess(Consumer<List<T>> preprocess,
                                   BiFunction<Long, Limit<Integer>, List<T>> objectsToProcess,
                                   int batchSize,
                                   int iterationsLimit) {
        int i = 0;
        List<T> objects = new ArrayList<>();
        Limit<Integer> limit = new Limit<>(batchSize);
        do {
            if (i > 0) {
                run(preprocess, objects);

            }
            objects = objectsToProcess.apply((long)(i++) * limit.get(), limit);
        } while (!objects.isEmpty() && i < iterationsLimit);
    }

    private static <T> void run(Consumer<List<T>> preprocess, List<T> objects) {
        preprocess.accept(objects);
        objects.clear();
    }
}
