package org.scada_lts.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class TimeUnitUtils {

    private static final Log log = LogFactory.getLog(TimeUnitUtils.class);

    public static Optional<TimeUnit> timeUnitByValueName(String valueName) {
        try {
            return Optional.of(TimeUnit.valueOf(valueName.toUpperCase()));
        } catch (Throwable e) {
            log.error(e);
            return Optional.empty();
        }
    }
}
