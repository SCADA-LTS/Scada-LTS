package org.scada_lts.monitor;

import org.scada_lts.monitor.type.ValueMonitor;

public interface IMonitoredValues {
    <T> ValueMonitor<T> addIfMissingStatMonitor(ValueMonitor<T> monitor);
    ValueMonitor<?> getValueMonitor(String id);
}
