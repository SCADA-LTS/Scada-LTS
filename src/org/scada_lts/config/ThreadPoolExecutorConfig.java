package org.scada_lts.config;

/**
 * Enum with chain keys for thread pool config
 *
 * @author kamil.jarmusik@gmail.com
 *
 */

public enum ThreadPoolExecutorConfig {

    CORE_POOL_SIZE("core-pool-size"),
    MAXIMUM_POOL_SIZE("maximum-pool-size"),
    KEEP_ALIVE_TIME("keep-alive-time"),
    BLOCKING_QUEUE_INTERFACE_IMPL("blocking-queue-interface-impl"),
    BLOCKING_QUEUE_INTERFACE_IMPL_ARGS("blocking-queue-interface-impl.args"),
    BLOCKING_QUEUE_INTERFACE_IMPL_ARGS_TYPES("blocking-queue-interface-impl.args-types"),
    TIME_UNIT_ENUM_VALUE("time-unit-enum-value");

    private final String name;

    ThreadPoolExecutorConfig(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public enum Priority {
        LOW("low-priority."),
        MEDIUM("medium-priority."),
        HIGH("high-priority.");

        private final String name;

        Priority(String priority) {
            this.name = priority;
        }

        public String getName() {
            return name;
        }
    }

    public static String getKey(Priority priority, ThreadPoolExecutorConfig param) {
        return "thread-pool-executor." + priority.getName() + param.getName();
    }
}