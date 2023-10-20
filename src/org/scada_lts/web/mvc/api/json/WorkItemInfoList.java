package org.scada_lts.web.mvc.api.json;

import com.serotonin.mango.rt.maint.work.WorkItem;
import com.serotonin.mango.rt.maint.work.WorkItemMetrics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import java.util.stream.LongStream;

public class WorkItemInfoList {

    private final int size;

    private final long minExecutedMs;
    private final long avgExecutedMs;
    private final long maxExecutedMs;

    private final long minTimeInitMs;
    private final long avgTimeInitMs;
    private final long maxTimeInitMs;

    private final long minExecutedNanos;
    private final long avgExecutedNanos;
    private final long maxExecutedNanos;

    private final long minTimeInitNanos;
    private final long avgTimeInitNanos;
    private final long maxTimeInitNanos;
    private final long itemsPerSecond;
    private final long itemsPerSecondOneMinute;
    private final long itemsPerSecondFiveMinutes;
    private final long itemsPerSecondFifteenMinutes;

    private final List<WorkItemInfo> workItemExecutes;

    private WorkItemInfoList(WorkItemInfoList workItemInfoList, boolean onlyMetrics) {
        this.size = workItemInfoList.size;

        this.minExecutedMs = workItemInfoList.minExecutedMs;
        this.avgExecutedMs = workItemInfoList.avgExecutedMs;
        this.maxExecutedMs = workItemInfoList.maxExecutedMs;

        this.minExecutedNanos = workItemInfoList.minExecutedNanos;
        this.avgExecutedNanos = workItemInfoList.avgExecutedNanos;
        this.maxExecutedNanos = workItemInfoList.maxExecutedNanos;

        this.minTimeInitMs = workItemInfoList.minTimeInitMs;
        this.avgTimeInitMs = workItemInfoList.avgTimeInitMs;
        this.maxTimeInitMs = workItemInfoList.maxTimeInitMs;

        this.minTimeInitNanos = workItemInfoList.minTimeInitNanos;
        this.avgTimeInitNanos = workItemInfoList.avgTimeInitNanos;
        this.maxTimeInitNanos = workItemInfoList.maxTimeInitNanos;

        this.itemsPerSecond = workItemInfoList.itemsPerSecond;
        this.itemsPerSecondOneMinute = workItemInfoList.itemsPerSecondOneMinute;
        this.itemsPerSecondFiveMinutes = workItemInfoList.itemsPerSecondFiveMinutes;
        this.itemsPerSecondFifteenMinutes = workItemInfoList.itemsPerSecondFifteenMinutes;
        this.workItemExecutes = onlyMetrics ? new ArrayList<>() : workItemInfoList.workItemExecutes;
    }

    public WorkItemInfoList(List<WorkItemInfo> workItemExecutes) {
        this.workItemExecutes = workItemExecutes;
        this.size = workItemExecutes.size();

        this.minExecutedMs = min(longStream(workItemExecutes, WorkItemMetrics::isExecuted, WorkItemMetrics::getExecutedMs));
        this.avgExecutedMs = avg(longStream(workItemExecutes, WorkItemMetrics::isExecuted, WorkItemMetrics::getExecutedMs));
        this.maxExecutedMs = max(longStream(workItemExecutes, WorkItemMetrics::isExecuted, WorkItemMetrics::getExecutedMs));

        this.minExecutedNanos = min(longStream(workItemExecutes, WorkItemMetrics::isExecuted, WorkItemMetrics::getExecutedNanos));
        this.avgExecutedNanos = avg(longStream(workItemExecutes, WorkItemMetrics::isExecuted, WorkItemMetrics::getExecutedNanos));
        this.maxExecutedNanos = max(longStream(workItemExecutes, WorkItemMetrics::isExecuted, WorkItemMetrics::getExecutedNanos));

        this.minTimeInitMs = min(longStream(workItemExecutes, workItem -> workItem.getTimeInitMs() != -1, WorkItemMetrics::getTimeInitMs));
        this.avgTimeInitMs = avg(longStream(workItemExecutes, workItem -> workItem.getTimeInitMs() != -1, WorkItemMetrics::getTimeInitMs));
        this.maxTimeInitMs = max(longStream(workItemExecutes, workItem -> workItem.getTimeInitMs() != -1, WorkItemMetrics::getTimeInitMs));

        this.minTimeInitNanos = min(longStream(workItemExecutes, workItem -> workItem.getTimeInitNanos() != -1, WorkItemMetrics::getTimeInitNanos));
        this.avgTimeInitNanos = avg(longStream(workItemExecutes, workItem -> workItem.getTimeInitNanos() != -1, WorkItemMetrics::getTimeInitNanos));
        this.maxTimeInitNanos = max(longStream(workItemExecutes, workItem -> workItem.getTimeInitNanos() != -1, WorkItemMetrics::getTimeInitNanos));

        this.itemsPerSecond = avg(workItemExecutes.stream().filter(a -> a.getItemsPerSecondOneSecond() > -1).mapToLong(WorkItemInfo::getItemsPerSecondOneSecond));
        this.itemsPerSecondOneMinute = avg(workItemExecutes.stream().filter(a -> a.getItemsPerSecondOneMinute() > -1).mapToLong(WorkItemInfo::getItemsPerSecondOneMinute));
        this.itemsPerSecondFiveMinutes = avg(workItemExecutes.stream().filter(a -> a.getItemsPerSecondFiveMinutes() > -1).mapToLong(WorkItemInfo::getItemsPerSecondFiveMinutes));
        this.itemsPerSecondFifteenMinutes = avg(workItemExecutes.stream().filter(a -> a.getItemsPerSecondFifteenMinutes() > -1).mapToLong(WorkItemInfo::getItemsPerSecondFifteenMinutes));
    }

    public List<WorkItemInfo> getWorkItemExecutes() {
        return workItemExecutes;
    }

    public int getSize() {
        return size;
    }

    public long getMinExecutedMs() {
        return minExecutedMs;
    }

    public long getAvgExecutedMs() {
        return avgExecutedMs;
    }

    public long getMaxExecutedMs() {
        return maxExecutedMs;
    }

    public long getMinTimeInitMs() {
        return minTimeInitMs;
    }

    public long getAvgTimeInitMs() {
        return avgTimeInitMs;
    }

    public long getMaxTimeInitMs() {
        return maxTimeInitMs;
    }

    public long getMinExecutedNanos() {
        return minExecutedNanos;
    }

    public long getAvgExecutedNanos() {
        return avgExecutedNanos;
    }

    public long getMaxExecutedNanos() {
        return maxExecutedNanos;
    }

    public long getMinTimeInitNanos() {
        return minTimeInitNanos;
    }

    public long getAvgTimeInitNanos() {
        return avgTimeInitNanos;
    }

    public long getMaxTimeInitNanos() {
        return maxTimeInitNanos;
    }

    public long getItemsPerSecond() {
        return itemsPerSecond;
    }

    public long getItemsPerSecondOneMinute() {
        return itemsPerSecondOneMinute;
    }

    public long getItemsPerSecondFiveMinutes() {
        return itemsPerSecondFiveMinutes;
    }

    public long getItemsPerSecondFifteenMinutes() {
        return itemsPerSecondFifteenMinutes;
    }

    public WorkItemInfoList onlyMetrics() {
        return new WorkItemInfoList(this, true);
    }

    private static long avg(LongStream longStream) {
        return longStream.average()
                .stream()
                .mapToLong(a -> BigDecimal.valueOf(a).setScale(1, RoundingMode.HALF_UP).longValue())
                .findAny()
                .orElse(-1);
    }

    private static long min(LongStream longStream) {
        return longStream.min()
                .orElse(-1);
    }

    private static long max(LongStream longStream) {
        return longStream.max()
                .orElse(-1);
    }

    private static LongStream longStream(List<WorkItemInfo> workItemExecutes, Predicate<WorkItem> predicate,
                                         ToLongFunction<WorkItem> get) {
        return workItemExecutes.stream()
                .filter(a -> predicate.test(a.getWorkItem()))
                .mapToLong(a -> get.applyAsLong(a.getWorkItem()));
    }
}
