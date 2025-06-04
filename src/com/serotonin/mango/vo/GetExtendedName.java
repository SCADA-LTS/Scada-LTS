package com.serotonin.mango.vo;

public interface GetExtendedName {
    String getName();

    default String getExtendedName() {
        return getName();
    }
}
