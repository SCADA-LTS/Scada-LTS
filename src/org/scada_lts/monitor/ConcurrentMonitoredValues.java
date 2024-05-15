//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.scada_lts.monitor;

import org.scada_lts.monitor.type.ValueMonitor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentMonitoredValues implements IMonitoredValues {
    private final Map<String, ValueMonitor<?>> monitors = new ConcurrentHashMap<>();

    @Override
    public <T> ValueMonitor<T> addIfMissingStatMonitor(ValueMonitor<T> monitor) {
        monitors.put(monitor.getId(), monitor);
        return monitor;
    }

    @Override
    public ValueMonitor<?> getValueMonitor(String id) {
        return monitors.get(id);
    }
}
