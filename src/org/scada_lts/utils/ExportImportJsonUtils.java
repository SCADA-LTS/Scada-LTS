package org.scada_lts.utils;

import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.util.LocalizableJsonException;

import java.util.function.Function;
import java.util.function.Predicate;

public final class ExportImportJsonUtils {

    private ExportImportJsonUtils() {}

    public static <T> int getInt(String propertiesName, Function<String, T> getValue,
                                 Predicate<T> isMissing, Function<T, Integer> convertValue,
                                 ExportCodes exportCodes) throws LocalizableJsonException {
        T value = getValue.apply(propertiesName);
        if(isMissing.test(value)) {
            throw new LocalizableJsonException("emport.error.missing", propertiesName, exportCodes.getCodeList());
        }
        int valueInt = convertValue.apply(value);
        if (!exportCodes.isValidId(valueInt))
            throw new LocalizableJsonException("emport.error.invalid", propertiesName, valueInt, exportCodes.getCodeList());
        return valueInt;
    }
}
