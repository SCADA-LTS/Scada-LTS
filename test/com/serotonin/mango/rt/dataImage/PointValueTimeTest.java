package com.serotonin.mango.rt.dataImage;

import com.serotonin.mango.rt.dataImage.types.MangoValue;
import org.junit.Assert;
import org.junit.Test;

public class PointValueTimeTest {

    private final String WHO_CHANGED_VALUE = "test";

    @Test
    public void thisPointValueTimeInstanceShouldNotHavePropertyWhoChangedValue_Test(){

        //given:
        AnnotatedPointValueTime annotatedPointValueTime = new AnnotatedPointValueTime(MangoValue.objectToValue(1), -1, SetPointSource.Types.USER, 1);
        annotatedPointValueTime.setSourceDescriptionArgument(WHO_CHANGED_VALUE);

        //when:
        String username = annotatedPointValueTime.getSourceDescriptionArgument();

        //then:
        Assert.assertTrue( username != null);
    }

    @Test
    public void thisPointValueTimeInstanceShouldHavePropertyWhoChangedValueAsAnEmptyString_Test(){

        AnnotatedPointValueTime annotatedPointValueTime = new AnnotatedPointValueTime(MangoValue.objectToValue(1), -1, SetPointSource.Types.USER, 1);
        annotatedPointValueTime.setSourceDescriptionArgument("");

        String username = annotatedPointValueTime.getSourceDescriptionArgument();

        Assert.assertTrue(username != null && username.equals(""));
    }

    @Test
    public void thisPointValueTimeInstanceShouldHaveCertainPropertyWhoChangedValue_Test(){

        //given:
        AnnotatedPointValueTime annotatedPointValueTime = new AnnotatedPointValueTime(MangoValue.objectToValue(1), -1, SetPointSource.Types.USER, 1);
        annotatedPointValueTime.setSourceDescriptionArgument(WHO_CHANGED_VALUE);

        //when:
        String username = annotatedPointValueTime.getSourceDescriptionArgument();

        //then:
        Assert.assertEquals(WHO_CHANGED_VALUE, username);
    }

    public MangoValue getMangoValueWithBinaryDataType(){
        return createMangoValue();
    }
    private MangoValue createMangoValue(){
        int BINARY_DATA_TYPE = 1;
        return MangoValue.stringToValue("1",BINARY_DATA_TYPE);
    }
}
