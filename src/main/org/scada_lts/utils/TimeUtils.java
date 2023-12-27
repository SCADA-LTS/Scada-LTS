package org.scada_lts.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TimeUtils {

    public static long toMs(long nanos) {
        if(nanos == -1)
            return -1;
        return BigDecimal.valueOf(nanos).divide(BigDecimal.valueOf(1000000L), RoundingMode.HALF_DOWN).longValue();
    }
}
