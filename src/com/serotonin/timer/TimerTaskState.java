package com.serotonin.timer;

import java.util.stream.Stream;

public enum TimerTaskState {
    VIRGIN(TimerTask.VIRGIN),
    CANCELLED(TimerTask.CANCELLED),
    EXECUTED(TimerTask.EXECUTED),
    SCHEDULED(TimerTask.SCHEDULED);

    private final int stateCode;

    TimerTaskState(int stateCode) {
        this.stateCode = stateCode;
    }

    public int getStateCode() {
        return stateCode;
    }

    public static TimerTaskState stateOf(int stateCode) {
        return Stream.of(TimerTaskState.values())
                .filter(a -> a.getStateCode() == stateCode)
                .findAny()
                .orElse(TimerTaskState.VIRGIN);
    }

    public static TimerTaskState stateOf(TimerTask timerTask) {
        return Stream.of(TimerTaskState.values())
                .filter(a -> a.getStateCode() == timerTask.getState())
                .findAny()
                .orElse(TimerTaskState.VIRGIN);
    }
}