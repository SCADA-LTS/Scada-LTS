package com.serotonin.mango.web.dwr;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueCacheTest;
import com.serotonin.mango.rt.dataImage.PointValueFacade;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.rt.link.PointLinkRT;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.DataPointVO.LoggingTypes;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.link.PointLinkVO;
import org.junit.Assert;
import org.junit.Test;

public class DataPointDetailsDwrTest {


    @Test
    public void AnnotatedPointValueWithAnnotations_Test(){
        PointValueCacheTest pointValueCacheTest =new PointValueCacheTest();
        String FIRST_ANNOTATION = "testName";
        String SECOND_ANNOTATION = "another user";

        User source = new User();
        source.setUsername(FIRST_ANNOTATION);
        PointValueTime pointValueTime = pointValueCacheTest.getSavedPointValueTimeInCache(source);

        DataPointVO dataPointVO = new DataPointVO(LoggingTypes.ON_CHANGE);
        dataPointVO.setId(2);
        dataPointVO.setDefaultCacheSize(10);

        DataPointRT dataPointRT = new DataPointRT(dataPointVO, new PointLocatorRT() {
            @Override
            public boolean isSettable() {
                return true;
            }
        },4,5);
        dataPointRT.addCollectionIntoCache(pointValueTime);


        PointValueFacade pointValueFacade = new PointValueFacade(dataPointVO.getId(),dataPointRT);
        source.setUsername(SECOND_ANNOTATION);
        dataPointRT.addCollectionIntoCache( pointValueCacheTest.getSavedPointValueTimeInCache(source) );

        PointLinkVO pointLinkVO = new PointLinkVO();
        pointLinkVO.setSourcePointId(2);
        pointLinkVO.setTargetPointId(3);
        pointLinkVO.setId(1);
        pointLinkVO.setXid("X_POINT_LINK");

        PointLinkRT pointLinkRT   = new PointLinkRT( pointLinkVO);
        dataPointRT.addCollectionIntoCache( pointValueCacheTest.getSavedPointValueTimeInCache(pointLinkRT) );


        DataPointDetailsDwr dataPointDetailsDwr = new DataPointDetailsDwr();
        boolean doThisAnnotationExist;
        doThisAnnotationExist = dataPointDetailsDwr.doThisAnnotationExist(FIRST_ANNOTATION,pointValueFacade,2);
        doThisAnnotationExist = dataPointDetailsDwr.doThisAnnotationExist(SECOND_ANNOTATION,pointValueFacade,2);
        Assert.assertTrue(doThisAnnotationExist);
    }
}
