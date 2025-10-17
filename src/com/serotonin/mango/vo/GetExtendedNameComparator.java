package com.serotonin.mango.vo;

import com.serotonin.util.StringUtils;

import java.util.Comparator;

public class GetExtendedNameComparator implements Comparator<GetExtendedName> {
    public static final GetExtendedNameComparator instance = new GetExtendedNameComparator();

    public int compare(GetExtendedName o1, GetExtendedName o2) {
        if (StringUtils.isEmpty(o1.getExtendedName()) || StringUtils.isEmpty(o2.getExtendedName()))
            return -1;
        return o1.getExtendedName().compareToIgnoreCase(o2.getExtendedName());
    }
}

