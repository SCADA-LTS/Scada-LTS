package br.org.scadabr.rt.scripting.context.properties;


import com.serotonin.mango.vo.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mozilla.javascript.NativeObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(Parameterized.class)
public class DataPointPurgeTypeNativeObjectTest {

    @Parameterized.Parameters(name= "{index}: purgeType: {0}, period: {1}")
    public static Object[] data() {
        return new Object[][] {
                {PurgeType.YEARS, 2},
                {PurgeType.MONTHS, 2},
                {PurgeType.WEEKS, 2},
                {PurgeType.DAYS, 2},
                {PurgeType.YEARS, 33},
                {PurgeType.MONTHS, 33},
                {PurgeType.WEEKS, 33},
                {PurgeType.DAYS, 33},
        };
    }

    private final DataPointUpdate expected;
    private final NativeObject nativeObjectMock;

    public DataPointPurgeTypeNativeObjectTest(PurgeType purgeType, int purgePeriod) {
        this.expected = new DataPointPurgeTypeProperties(purgeType, purgePeriod);
        this.nativeObjectMock = mock(NativeObject.class);
        when(nativeObjectMock.get(eq("purgeType"))).thenReturn(purgeType.name());
        when(nativeObjectMock.get(eq("purgePeriod"))).thenReturn((double)purgePeriod);
    }

    @Test
    public void when_byNativeObject_for_PurgeType() {
        //when:
        DataPointPurgeTypeProperties result = DataPointPurgeTypeProperties.byNativeObject(nativeObjectMock);

        //then:
        assertEquals(expected, result);
    }
}
