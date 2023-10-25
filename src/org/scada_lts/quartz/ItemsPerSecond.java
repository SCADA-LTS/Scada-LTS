package org.scada_lts.quartz;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.scada_lts.mango.service.SystemSettingsService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.IntPredicate;

public class ItemsPerSecond implements StatefulJob, ReadItemsPerSecond {

    private static final Log LOG = LogFactory.getLog(ItemsPerSecond.class);
    private final Map<Integer, Long> itemsPerSecondMap;
    private final Map<Integer, Integer> snapshots;
    private final AtomicInteger snapshotCounter;
    private final AtomicInteger itemsPerSecondCounter;
    private final SystemSettingsService systemSettingsService;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final int fromLastSeconds;
    private volatile int snapshotLastIndex;


    private ItemsPerSecond(int fromLastSeconds) {
        if (fromLastSeconds < 1) {
            throw new IllegalArgumentException("The value fromLastSeconds cannot be less than 1, but was: " + fromLastSeconds);
        }
        this.itemsPerSecondMap = new ConcurrentHashMap<>();
        this.snapshots = new ConcurrentHashMap<>();
        this.snapshotCounter = new AtomicInteger();
        this.itemsPerSecondCounter = new AtomicInteger();
        this.fromLastSeconds = fromLastSeconds;
        this.systemSettingsService = new SystemSettingsService();
    }

    public static ItemsPerSecond fromFifteenMinutes() {
        ItemsPerSecond itemsPerSecond = new ItemsPerSecond(60*15);
        EverySecond.schedule(itemsPerSecond);
        return itemsPerSecond;
    }

    @Override
    public int itemsPerSecond() {
        return itemsPerSecond(1);
    }

    @Override
    public int itemsPerSecondFromOneMinute() {
        return itemsPerSecond(60);
    }

    @Override
    public int itemsPerSecondFromFiveMinutes() {
        return itemsPerSecond(60*5);
    }

    @Override
    public int itemsPerSecondFromFifteenMinutes() {
        return itemsPerSecond(60*15);
    }

    @Override
    public int itemsPerSecond(int fromLastSeconds) {
        if(isDisabled(systemSettingsService, true)) {
            return -1;
        }
        if (fromLastSeconds > this.fromLastSeconds) {
            throw new IllegalArgumentException("The value fromLastSeconds cannot be greater than: " + this.fromLastSeconds + ", but was: " + fromLastSeconds);
        }
        lock.readLock().lock();
        try {
            if(fromLastSeconds == 1) {
                Integer stat = snapshots.get(snapshotLastIndex);
                return Objects.requireNonNullElse(stat, 0);
            }
            if (fromLastSeconds == this.fromLastSeconds) {
                return snapshots.values().stream().mapToInt(a -> a).sum() / fromLastSeconds;
            }
            return itemsPerSecond(fromLastSeconds, new HashMap<>(snapshots), snapshotLastIndex);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void increment() {
        if(isDisabled(systemSettingsService, false)) {
            return;
        }
        update(System.currentTimeMillis(), itemsPerSecondCounter, index -> index >= systemSettingsService.getWorkItemsReportingItemsPerSecondLimit(), itemsPerSecondMap);
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        if(isDisabled(systemSettingsService, false)) {
            return;
        }
        long currentTime = System.currentTimeMillis();
        itemsPerSecondMap.entrySet().removeIf(time -> currentTime - time.getValue() > 1000L);
        int result = itemsPerSecondMap.size();
        updateSnapshots(result);
    }

    private void updateSnapshots(int result) {
        lock.writeLock().lock();
        try {
            this.snapshotLastIndex = update(result, snapshotCounter, index -> fromLastSeconds == 1 || index >= fromLastSeconds, snapshots);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private static <T> int update(T result, AtomicInteger counter, IntPredicate resetIf, Map<Integer, T> objects) {
        int index = counter.incrementAndGet();
        if (resetIf.test(index)) {
            counter.set(0);
            objects.put(0, result);
            return 0;
        } else {
            objects.put(index, result);
            return index;
        }
    }

    private static int itemsPerSecond(int fromLastSeconds, Map<Integer, Integer> snapshots, int snapshotLastIndex) {
        List<Integer> toCalc = new ArrayList<>();
        int currentIndex = snapshotLastIndex;
        for(int i = 0; i < fromLastSeconds; i++) {
            if(currentIndex < 0) {
                currentIndex = snapshots.size() + currentIndex;
            }
            Integer stat = snapshots.get(currentIndex);
            toCalc.add(Objects.requireNonNullElse(stat, 0));
            --currentIndex;
        }
        return toCalc.stream().mapToInt(a -> a).sum() / fromLastSeconds;
    }

    private static boolean isDisabled(SystemSettingsService systemSettingsService, boolean log) {
        boolean disabled = !systemSettingsService.isWorkItemsReportingEnabled()
                || !systemSettingsService.isWorkItemsReportingItemsPerSecondEnabled()
                || systemSettingsService.getWorkItemsReportingItemsPerSecondLimit() <= 0;
        if(log && disabled) {
            LOG.warn("Reporting items per second is disabled");
        }
        return disabled;
    }
}