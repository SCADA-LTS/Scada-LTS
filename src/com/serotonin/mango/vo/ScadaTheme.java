package com.serotonin.mango.vo;

import org.scada_lts.serorepl.utils.StringUtils;

import java.util.stream.Stream;

public enum ScadaTheme {
    MODERN,
    DEFAULT;

    public static ScadaTheme getType(String name) {
        if(StringUtils.isEmpty(name))
            return ScadaTheme.DEFAULT;
        return Stream.of(ScadaTheme.values())
                .filter(a -> a.name().equalsIgnoreCase(name))
                .findAny()
                .orElse(ScadaTheme.DEFAULT);
    }
}


