package org.scada_lts.serorepl.utils;

import com.serotonin.InvalidArgumentException;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

public class ColorUtilsTest {

    @Test
    public void toColorTest() throws InvalidArgumentException {
        assertEquals(ColorUtils.toColor("#F0F8FF"), com.serotonin.util.ColorUtils.toColor("#F0F8FF"));
        assertNotSame(ColorUtils.toColor("#F0F8FF"), com.serotonin.util.ColorUtils.toColor("#DEB887"));
    }
    @Test
    public void toHexStringStringTest(){
        // testowanie przelotki?
    }
    @Test
    public void toHexStringColorTest(){
        assertEquals(ColorUtils.toHexString( new Color(50, 50 ,50)) , com.serotonin.util.ColorUtils.toHexString(new Color(50,50,50)));
    }

}
