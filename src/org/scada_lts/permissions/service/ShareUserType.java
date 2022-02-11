package org.scada_lts.permissions.service;
import com.serotonin.mango.view.ShareUser;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ShareUserType {

    ACCESS_SET(ShareUser.ACCESS_SET),
    ACCESS_READ(ShareUser.ACCESS_READ),
    ACCESS_OWNER(ShareUser.ACCESS_OWNER),
    ACCESS_NONE(ShareUser.ACCESS_NONE);

    private final int code;

    ShareUserType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Map<String, Integer> codes() {
        return Stream.of(ShareUserType.values())
                .collect(Collectors.toMap(Enum::name, ShareUserType::getCode));
    }

    public static ShareUserType getType(int code) {
        return Stream.of(ShareUserType.values())
                .filter(a -> a.getCode() == code)
                .findAny()
                .orElse(ShareUserType.ACCESS_NONE);
    }

    public static ShareUserType getType(String name) {
        return Stream.of(ShareUserType.values())
                .filter(a -> a.name().equalsIgnoreCase(name))
                .findAny()
                .orElse(ShareUserType.ACCESS_NONE);
    }
}