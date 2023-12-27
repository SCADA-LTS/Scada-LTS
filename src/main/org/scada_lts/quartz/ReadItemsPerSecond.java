package org.scada_lts.quartz;

public interface ReadItemsPerSecond {
    int itemsPerSecond();
    int itemsPerSecondFromOneMinute();
    int itemsPerSecondFromFiveMinutes();
    int itemsPerSecondFromFifteenMinutes();
    int itemsPerSecond(int fromLastSeconds);
}
