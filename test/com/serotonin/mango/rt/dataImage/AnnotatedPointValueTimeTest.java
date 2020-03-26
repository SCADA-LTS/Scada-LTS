package com.serotonin.mango.rt.dataImage;

import org.junit.Assert;
import org.junit.Test;

public class AnnotatedPointValueTimeTest {

    @Test
    public void annotaionPartShouldExistInAnnotationPointValueTime_Test(){

        // that AnnotaionPartWhichShouldBeVisibleOnGui will be visible on GUI after text "User:"
        String AnnotaionPartWhichShouldBeVisibleOnGui = "admin";

        AnnotatedPointValueTime annotatedPointValueTime = new AnnotatedPointValueTime(
                AnnotaionPartWhichShouldBeVisibleOnGui,
                new PointValueTimeTest().getMangoValueWithBinaryDataType(),
                0,
                SetPointSource.Types.USER,
                SetPointSource.Types.USER);

        Assert.assertEquals(AnnotaionPartWhichShouldBeVisibleOnGui, annotatedPointValueTime.getSourceDescriptionArgument());
    }
    @Test
    public void annotaionPartShouldBeAnEmptyInPointValueTime_Test(){

        String EMPTY_STRING = "";

        AnnotatedPointValueTime annotatedPointValueTime = new AnnotatedPointValueTime(
                new PointValueTimeTest().getMangoValueWithBinaryDataType(),
                0,
                SetPointSource.Types.USER,
                SetPointSource.Types.USER);

        Assert.assertEquals(EMPTY_STRING, annotatedPointValueTime.getSourceDescriptionArgument());
    }
}
