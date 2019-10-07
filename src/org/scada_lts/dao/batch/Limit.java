package org.scada_lts.dao.batch;

public final class Limit<T extends Number> {

    private final T limit;

    public Limit(T limit) {
        this.limit = limit;
    }

    public T get() {
        return limit;
    }
}