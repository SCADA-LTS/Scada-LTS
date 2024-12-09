package com.serotonin.mango.util;

import com.serotonin.mango.rt.maint.BackgroundProcessing;
import com.serotonin.mango.rt.maint.work.WorkItemPriority;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

    public static void terminate(ExecutorService executorService, String poolName) {

        try {
            executorService.shutdown();
            boolean lowDone = false;
            int rewaits = 12;
            while (rewaits > 0) {
                lowDone = executorService.awaitTermination(5, TimeUnit.SECONDS) && executorService.isTerminated();

                if (lowDone)
                    break;
                if (!lowDone)
                    LOG.info("BackgroundProcessing waiting for {} priority tasks to complete", poolName);

                rewaits--;
            }
            if(!executorService.isTerminated() && !executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }

        } catch (InterruptedException e) {
            LOG.info(LoggingUtils.exceptionInfo(e), e);
        } finally {
            if(executorService.isTerminated())
                LOG.info("Stopped {}", poolName);
            else
                LOG.info("Stopped {} Fail", poolName);
        }
    }

    private static ThreadPoolExecutor createThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit timeUnit, BlockingQueue<Runnable> blockingQueue) {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit, blockingQueue);
    }
}
