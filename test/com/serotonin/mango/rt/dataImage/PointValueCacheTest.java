package com.serotonin.mango.rt.dataImage;

import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.vo.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PointValueCacheTest {

    private SetPointSource source;
    private PointValueCache pointValueCache;

    @Before
    public void init() throws Exception {
        pointValueCache = new PointValueCache(1,1);
    }

    @Test
    public void sourceIsNotEmptyFillNeededPropertyInPointValueTime_Test(){
        source = new User();
        PointValueTime pvt = new PointValueTime(MangoValue.stringToValue("1",1), new Long(1));
        pointValueCache.savePointValueIntoDaoAndCacheUpdate(pvt,source,Boolean.FALSE,Boolean.FALSE);
    }

    @Test
    public void sourceIsEmptyNotFillNeededPropertyInPointValueTime_Test(){
        PointValueTime pvt = new PointValueTime(MangoValue.stringToValue("1",1), new Long(1));
        pointValueCache.savePointValueIntoDaoAndCacheUpdate(pvt,source,Boolean.FALSE,Boolean.FALSE);
    }

    @After
    public void finalized() {
        source = null;
        pointValueCache = null;
    }
}
