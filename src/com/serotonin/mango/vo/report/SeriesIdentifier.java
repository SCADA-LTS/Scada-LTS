package com.serotonin.mango.vo.report;

import java.util.Objects;

public class SeriesIdentifier implements Comparable<SeriesIdentifier> {

    private final int id;
    private final String name;

    public SeriesIdentifier(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int compareTo(SeriesIdentifier o) {
        int nameComparison = this.name.compareTo(o.name);
        if (nameComparison == 0) {
            return Integer.compare(this.id, o.id);
        }
        return nameComparison;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeriesIdentifier that = (SeriesIdentifier) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name;
    }
}
