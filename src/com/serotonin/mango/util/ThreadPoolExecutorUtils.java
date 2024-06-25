package com.serotonin.mango.util;

import com.serotonin.mango.rt.maint.work.WorkItemPriority;
import org.scada_lts.utils.BlockingQueuesUtils;
import org.scada_lts.utils.SystemSettingsUtils;
import org.scada_lts.utils.TimeUnitUtils;

import java.util.concurrent.*;

public final class ThreadPoolExecutorUtils {

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

    private static ThreadPoolExecutor createThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit timeUnit, BlockingQueue<Runnable> blockingQueue) {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit, blockingQueue);
    }
}
