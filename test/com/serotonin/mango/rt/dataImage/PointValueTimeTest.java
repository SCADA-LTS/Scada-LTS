package com.serotonin.mango.rt.dataImage;

import com.serotonin.mango.rt.dataImage.types.MangoValue;
import org.junit.Assert;
import org.junit.Test;

public class PointValueTimeTest {

    private final String WHO_CHANGED_VALUE = "test";
    private boolean createInstanceWithVWhoValuedValue;

    @Test
    public void thisPointValueTimeInstanceShouldNotHavePropertyWhoChangedValue_Test(){
        createInstanceWithVWhoValuedValue=false;

        Assert.assertTrue( createPointValueTime( createInstanceWithVWhoValuedValue ).getWhoChangedValue() != null);
    }

    @Test
    public void thisPointValueTimeInstanceShouldHavePropertyWhoChangedValueAsAnEmptyString_Test(){
        createInstanceWithVWhoValuedValue=false;

        PointValueTime pointValueTime = createPointValueTime( createInstanceWithVWhoValuedValue );

        String EMPTY_STRING = "";

        Assert.assertTrue(
                pointValueTime.getWhoChangedValue() != null &&
                pointValueTime.getWhoChangedValue().equals(EMPTY_STRING)
                );
    }

    @Test
    public void thisPointValueTimeInstanceShouldHaveCertainPropertyWhoChangedValue_Test(){
        createInstanceWithVWhoValuedValue=true;

        Assert.assertEquals(WHO_CHANGED_VALUE,  createPointValueTime( createInstanceWithVWhoValuedValue ).getWhoChangedValue() );
    }

    private  PointValueTime createPointValueTime(boolean createInstanceWithVWhoValuedValue){

        return createInstanceWithVWhoValuedValue
                ?new PointValueTime( createMangoValue(), 0, WHO_CHANGED_VALUE)
                :new PointValueTime( createMangoValue(), 0);
    }
    public MangoValue getMangoValueWithBinaryDataType(){
        return createMangoValue();
    }
    private MangoValue createMangoValue(){
        int BINARY_DATA_TYPE = 1;
        return MangoValue.stringToValue("1",BINARY_DATA_TYPE);
    }
}
