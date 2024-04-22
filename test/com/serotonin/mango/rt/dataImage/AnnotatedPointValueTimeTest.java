package com.serotonin.mango.rt.dataImage;

import org.junit.Assert;
import org.junit.Test;

public class AnnotatedPointValueTimeTest {

    @Test
    public void annotaionPartShouldExistInAnnotationPointValueTime_Test(){

        // that AnnotaionPartWhichShouldBeVisibleOnGui will be visible on GUI after text "User:"
        String username = "admin";

        AnnotatedPointValueTime annotatedPointValueTime = new AnnotatedPointValueTime(null,
                0,
                SetPointSource.Types.USER,
                -1);
        annotatedPointValueTime.setSourceDescriptionArgument(username);

        Assert.assertEquals(username, annotatedPointValueTime.getSourceDescriptionArgument());
    }
    @Test
    public void annotaionPartShouldBeAnEmptyInPointValueTime_Test(){
        AnnotatedPointValueTime annotatedPointValueTime = new AnnotatedPointValueTime(
                new PointValueTimeTest().getMangoValueWithBinaryDataType(),
                0,
                SetPointSource.Types.USER,
                -1);

        Assert.assertEquals("", annotatedPointValueTime.getSourceDescriptionArgument());
    }
}
