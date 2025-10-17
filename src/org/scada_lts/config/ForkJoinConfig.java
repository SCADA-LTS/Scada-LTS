package org.scada_lts.config;

/**
 * Enum with chain keys for thread pool config
 *
 * @author kamil.jarmusik@gmail.com
 *
 */

public enum ForkJoinConfig {

    CORE_POOL_SIZE(ThreadPoolExecutorConfig.CORE_POOL_SIZE),
    MAXIMUM_POOL_SIZE(ThreadPoolExecutorConfig.MAXIMUM_POOL_SIZE),
    KEEP_ALIVE_TIME(ThreadPoolExecutorConfig.KEEP_ALIVE_TIME),
    TIME_UNIT_ENUM_VALUE(ThreadPoolExecutorConfig.TIME_UNIT_ENUM_VALUE),
    PARALLELISM("parallelism"),
    MINIMUM_RUNNABLE("minimum-runnable"),
    ASYNC_MODE("async-mode");

    private final String name;

    ForkJoinConfig(String name) {
        this.name = name;
    }

    ForkJoinConfig(ThreadPoolExecutorConfig poolExecutorConfig) {
        this.name = poolExecutorConfig.getName();
    }

    public String getName() {
        return name;
    }

    public static String getKey(ForkJoinConfig param) {
        return "recursive-pool-executor." + param.getName();
    }
}