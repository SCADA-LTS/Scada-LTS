package com.serotonin.mango.vo;

import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.web.i18n.LocalizableMessage;

import java.util.ResourceBundle;
import java.util.stream.Stream;

public enum PointDataType {

    UNKNOWN(DataTypes.UNKNOWN),
    BINARY(DataTypes.BINARY),
    MULTISTATE(DataTypes.MULTISTATE),
    NUMERIC(DataTypes.NUMERIC),
    ALPHANUMERIC(DataTypes.ALPHANUMERIC),
    IMAGE(DataTypes.IMAGE);

    private final int code;

    PointDataType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getMessageText() {
        LocalizableMessage localizableMessage = DataTypes.getDataTypeMessage(code);
        ResourceBundle bundle = Common.getBundle();
        return localizableMessage.getLocalizedMessage(bundle);
    }

    public static PointDataType byCode(int code) {
        return Stream.of(PointDataType.values())
                .filter(a -> a.getCode() == code)
                .findAny()
                .orElse(PointDataType.UNKNOWN);
    }
}
