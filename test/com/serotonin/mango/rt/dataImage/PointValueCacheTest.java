package com.serotonin.mango.rt.dataImage;

import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.link.PointLinkRT;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.link.PointLinkVO;
import org.junit.Assert;
import org.junit.Test;

public class PointValueCacheTest {

    @Test
    public void sourceIsNotEmptyFillNeededPropertyInPointValueTime_Test(){
        User source = new User();
        source.setUsername("testName");
        PointValueTime pvt = savePropertiesAboutOwnerOfPointValueChange(source);
        Assert.assertEquals("testName",pvt.getWhoChangedValue());
    }

    @Test
    public void sourceIsEmptyNotFillNeededPropertyInPointValueTime_Test(){
        PointLinkRT source = new PointLinkRT(new PointLinkVO());
        PointValueTime pvt = savePropertiesAboutOwnerOfPointValueChange(source);
        Assert.assertNotSame("testName",pvt.getWhoChangedValue());
    }
    public PointValueTime getSavedPointValueTimeInCache (SetPointSource source ){
        return savePropertiesAboutOwnerOfPointValueChange( source );
    }
    private PointValueTime savePropertiesAboutOwnerOfPointValueChange (SetPointSource source ){
        PointValueTime pvt = new PointValueTime(MangoValue.stringToValue("1",1), new Long(1));
        PointValueCache pointValueCache = new PointValueCache();
        pointValueCache.savePointValueIntoDaoAndCacheUpdate(pvt,source,Boolean.FALSE,Boolean.FALSE);
        return pvt;
    }

}
