package com.serotonin.mango.vo;

public interface TimePeriod {
    int getCode();
    long toMs(long value);
    String getKey();
}
