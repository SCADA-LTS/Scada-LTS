package com.serotonin.mango.rt.dataImage;

import com.serotonin.mango.db.dao.IPointValueDao;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import org.openjdk.jmh.annotations.*;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import utils.PointValueDaoToBenchmark;
import utils.SetPointSourceTestImpl;

import java.util.concurrent.TimeUnit;

import static utils.PointValueCacheFactory.*;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(value = 1, jvmArgs = {"-Xms2G","-Xmx2G"})
public class SavePointValueMethodBenchmark {

    private static final int NUMBER_OF_RUNNERS = 1_000_000;

    private PointValueTime pointValueTimeFirstSaved;
    private PointValueCache pointValueCacheSubject;

    @Param({"false"})
    private boolean logValue;
    @Param({"false"})
    private boolean async;
    @Param({"1", "100", "10000"})
    private int defaultSize;

    @Setup
    public void setup() {
        pointValueTimeFirstSaved = new PointValueTime(MangoValue.stringToValue("1", 3), System.currentTimeMillis());
        IPointValueDao dao = new PointValueDaoToBenchmark(defaultSize);
        int dataPointId = 123;
        pointValueCacheSubject = newPointValueCacheV2(dataPointId, defaultSize, dao);
    }

    @Benchmark
    public void savePointValueIntoDaoAndCacheUpdate() {
        for (int i = 0 ; i < NUMBER_OF_RUNNERS ; i++) {
            pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirstSaved, new SetPointSourceTestImpl(), logValue, async);
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(SavePointValueMethodBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
