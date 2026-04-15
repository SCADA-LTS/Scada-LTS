package org.scada_lts.utils;

import java.util.List;
import java.util.stream.Collectors;

public class ObjectsPaginationUtils {

    public static <T> List<T> pagination(List<T> list, int page, int limit) {
        return list.stream()
                .skip((long) (page - 1) * limit)
                .limit(limit)
                .collect(Collectors.toList());
    }

    public static <T> List<T> pagination(List<T> list, long offset, int limit) {
        return list.stream()
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
    }
}