package org.scada_lts.config;

public enum ThreadPoolConfigKeys implements ScadaConfigKey {

    CORE_POOL_SIZE("workers-priority-medium.pool-threads.core-pool-size"),
    MAXIMUM_POOL_SIZE("workers-priority-medium.pool-threads.maximum-pool-size"),
    KEEP_ALIVE_TIME("workers-priority-medium.pool-threads.keep-alive-time"),
    BLOCKING_QUEUE_INTERFACE_IMPL("workers-priority-medium.pool-threads.blocking-queue-interface-impl"),
    TIME_UNIT_ENUM_VALUE("workers-priority-medium.pool-threads.time-unit-enum-value");

    private String key;

    ThreadPoolConfigKeys(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return key;
    }
}
