package com.serotonin.mango.util;

import com.serotonin.mango.rt.maint.work.WorkItemPriority;
import org.apache.catalina.startup.SafeForkJoinWorkerThreadFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.scada_lts.utils.BlockingQueuesUtils;
import org.scada_lts.utils.SystemSettingsUtils;
import org.scada_lts.utils.TimeUnitUtils;

import java.util.concurrent.*;

public final class ThreadPoolExecutorUtils {

    private static final Logger LOG = LogManager.getLogger(ThreadPoolExecutorUtils.class);

    private ThreadPoolExecutorUtils() {}

    public static ThreadPoolExecutor createPool(WorkItemPriority priority) {

        int corePoolSize = SystemSettingsUtils.getThreadExecutorCorePoolSize(priority);
        int maximumPoolSize = SystemSettingsUtils.getThreadExecutorMaximumPoolSize(priority);
        long keepAliveTime = SystemSettingsUtils.getThreadExecutorKeepAliveTime(priority);
        Object[] objects = SystemSettingsUtils.getThreadExecutorBlockingQueueInterfaceImplArgs(priority);
        TimeUnit timeUnit = TimeUnitUtils
                .timeUnitByValueName(SystemSettingsUtils.getThreadExecutorTimeUnitEnumValue(priority))
                .orElse(TimeUnit.SECONDS);
        BlockingQueue<Runnable> blockingQueue = BlockingQueuesUtils
                .newBlockingQueue(SystemSettingsUtils.getThreadExecutorBlockingQueueInterfaceImpl(priority),
                        priority == WorkItemPriority.HIGH ? new SynchronousQueue<>() : new LinkedBlockingQueue<>(), objects);
        return createThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit, blockingQueue);
    }

    public static ForkJoinPool createForkJoinPool() {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        int parallelismFormEnvProperties = SystemSettingsUtils.getRecursiveExecutorParallelism();
        long keepAliveTimeFromEnvProperties = SystemSettingsUtils.getRecursiveExecutorKeepAliveTime();
        int maximumPoolSizeFromEnvProperties = SystemSettingsUtils.getRecursiveExecutorMaximumPoolSize();

        int parallelism = parallelismFormEnvProperties <= 0 || parallelismFormEnvProperties > availableProcessors ? availableProcessors : parallelismFormEnvProperties;
        int maximumPoolSize = Math.max(maximumPoolSizeFromEnvProperties, parallelism);
        long keepAliveTime = keepAliveTimeFromEnvProperties <= 0 ? 1 : keepAliveTimeFromEnvProperties;

        int corePoolSize = SystemSettingsUtils.getRecursiveExecutorCorePoolSize();
        int minimumRunnable = SystemSettingsUtils.getRecursiveExecutorMinimumRunnable();
        boolean asyncMode = SystemSettingsUtils.getRecursiveExecutorAsyncMode();

        TimeUnit timeUnit = TimeUnitUtils
                .timeUnitByValueName(SystemSettingsUtils.getRecursiveExecutorTimeUnitEnumValue())
                .orElse(TimeUnit.SECONDS);

        return new ForkJoinPool(parallelism,
                new SafeForkJoinWorkerThreadFactory(),
                (t, e) -> {
                    LOG.warn(LoggingUtils.exceptionInfo(e));
                },
                asyncMode,
                corePoolSize,
                maximumPoolSize,
                minimumRunnable,
                pool -> true,
                keepAliveTime,
                timeUnit);
    }

    public static void joinTermination(ExecutorService service, String poolName) {
        boolean done;
        try {
            int rewaits = 3;
            while (rewaits > 0) {
                done = service.awaitTermination(5, TimeUnit.SECONDS) && service.isTerminated();

                if (done)
                    break;
                else
                    waitInfo(service, poolName);

                rewaits--;
            }
            if(!service.isTerminated() && !service.awaitTermination(5, TimeUnit.SECONDS)) {
                service.shutdownNow();
            }
        } catch (InterruptedException e) {
            LOG.info(LoggingUtils.exceptionInfo(e), e);
        } finally {
            if(service.isTerminated())
                LOG.info("Stopped {}", poolName);
            else
                LOG.info("Stopped {} Fail", poolName);
        }
    }

    private static void waitInfo(ExecutorService executorService, String poolName) {
        if(executorService instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executorService;
            LOG.info("BackgroundProcessing waiting for {} ({}) tasks to complete",
                    poolName, threadPoolExecutor.getQueue().size());
        } else {
            LOG.info("BackgroundProcessing waiting for {} tasks to complete", poolName);
        }
    }

    private static ThreadPoolExecutor createThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit timeUnit, BlockingQueue<Runnable> blockingQueue) {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit, blockingQueue);
    }
}
