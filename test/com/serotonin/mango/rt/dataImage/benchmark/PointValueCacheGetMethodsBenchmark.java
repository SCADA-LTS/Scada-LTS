package com.serotonin.mango.rt.dataImage.benchmark;

import com.serotonin.mango.db.dao.IPointValueDao;
import com.serotonin.mango.rt.dataImage.PointValueCache;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import utils.PointValueCacheFactory;
import utils.PointValueDaoToBenchmark;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(value = 1, jvmArgs = {"-Xms2G","-Xmx2G"})
public class PointValueCacheGetMethodsBenchmark {

    private static final int NUMBER_OF_RUNNERS = 1_000_000;
    private static final int dataPointId = 123;

    private PointValueCache pointValueCacheSubject;

    @Param({"1", "100", "10000"})
    private int defaultSize;

    @Setup
    public void setup() {
        IPointValueDao dao = new PointValueDaoToBenchmark(defaultSize + 2);
        pointValueCacheSubject = PointValueCacheFactory.newPointValueCacheV2(dataPointId,defaultSize,dao);
    }

    @Benchmark
    public void getLatestPointValue() {
        for (int i = 0 ; i < NUMBER_OF_RUNNERS ; i++) {
            pointValueCacheSubject.getLatestPointValue();
        }
    }

    @Benchmark
    public void getLatestPointValues_limit_greater_than_defaultSize() {
        for (int i = 0 ; i < NUMBER_OF_RUNNERS ; i++) {
            pointValueCacheSubject.getLatestPointValues(defaultSize+2);
        }
    }

    @Benchmark
    public void getLatestPointValues_limit_equals_defaultSize() {
        for (int i = 0 ; i < NUMBER_OF_RUNNERS ; i++) {
            pointValueCacheSubject.getLatestPointValues(defaultSize);
        }
    }

    @Benchmark
    public void getLatestPointValues_limit_less_than_defaultSize() {
        for (int i = 0 ; i < NUMBER_OF_RUNNERS ; i++) {
            pointValueCacheSubject.getLatestPointValues(defaultSize-2);
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(PointValueCacheGetMethodsBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
