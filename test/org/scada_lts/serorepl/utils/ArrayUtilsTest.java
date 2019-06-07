package org.scada_lts.serorepl.utils;

import com.serotonin.util.ArrayUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class ArrayUtilsTest {
    @Test
    public void toHexString() throws Exception {

        byte[] byteArray = new byte[20];
        byte[] emptyArray = new byte[0];

        assertEquals(ArrayUtils.toHexString(byteArray) ,
                org.scada_lts.serorepl.utils.ArrayUtils.toHexString(byteArray));

        assertEquals(ArrayUtils.toHexString(emptyArray) ,
                org.scada_lts.serorepl.utils.ArrayUtils.toHexString(emptyArray));

    }

    @Test
    public void containsInt() throws Exception {

        int[] values = new int[]{21, 121, 12, 71, 8, 99, 19, 853};

        assertEquals(ArrayUtils.contains(values,21),
                org.scada_lts.serorepl.utils.ArrayUtils.contains(values,21));


        assertEquals(ArrayUtils.contains(values,19),
                org.scada_lts.serorepl.utils.ArrayUtils.contains(values,19));


        assertEquals(ArrayUtils.contains(values,7879),
                org.scada_lts.serorepl.utils.ArrayUtils.contains(values,7879));


        assertEquals(ArrayUtils.contains(values,54842),
                org.scada_lts.serorepl.utils.ArrayUtils.contains(values,18526));
    }

    @Test
    public void containsString() throws Exception {

        String[] stringArray1 = new String[]{"lambda", "xiox", "kecz", "___value___" ,"wuj"};
        String val = "___value___";

        assertEquals(ArrayUtils.contains(stringArray1,val),
                org.scada_lts.serorepl.utils.ArrayUtils.contains(stringArray1,val));

        assertEquals(ArrayUtils.contains(stringArray1,"kecz"),
                org.scada_lts.serorepl.utils.ArrayUtils.contains(stringArray1,"kecz"));

        assertEquals(ArrayUtils.contains(stringArray1,"zzz"),
                org.scada_lts.serorepl.utils.ArrayUtils.contains(stringArray1,"zzz"));

    }

    @Test
    public void indexOf() throws Exception {

        String[] stringArray1 = new String[]{"AAA", "ABC", "HURR", "___value___" ,"DURR"};
        String val = "___value___";

        assertEquals(ArrayUtils.indexOf(stringArray1,val),
                org.scada_lts.serorepl.utils.ArrayUtils.indexOf(stringArray1,val));

        assertEquals(ArrayUtils.indexOf(stringArray1,"TROLOLO"),
                org.scada_lts.serorepl.utils.ArrayUtils.indexOf(stringArray1,"TROLOLO"));



    }

    @Test(expected = NullPointerException.class)
    public void indexOfShouldThrowNPEWhenarrayIsNull(){

        assertEquals(ArrayUtils.indexOf(null,"TROLOLO"),
                org.scada_lts.serorepl.utils.ArrayUtils.indexOf(null,"TROLOLO"));

    }


}