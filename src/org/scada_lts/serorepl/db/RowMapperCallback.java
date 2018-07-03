package org.scada_lts.serorepl.db;

public interface RowMapperCallback<T> {
    void row(T value_1, int value_2);
}
