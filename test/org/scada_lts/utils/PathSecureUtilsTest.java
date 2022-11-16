package org.scada_lts.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class PathSecureUtilsTest {

    @Parameterized.Parameters(name = "{index}: file: {0}, accept: {1}")
    public static List<Object[]> data() {
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        List<Object[]> datas = new ArrayList<>();

        datas.add(new Object[] {"/../../jsAsSvg.svg", false});
        datas.add(new Object[] {"/../jsAsBmp.bmp", false});
        datas.add(new Object[] {"./jsAsGif.gif", false});
        datas.add(new Object[] {"../jsAsJpg.jpg", false});
        datas.add(new Object[] {"././jsAsPng.png", false});

        datas.add(new Object[] {"gifFile.zip.gif", true});
        datas.add(new Object[] {"jpgFile.o.jpg", true});
        datas.add(new Object[] {"pngFile.jpg.png", true});

        datas.add(new Object[] {"", false});
        datas.add(new Object[] {".php%00.jpg", false});
        datas.add(new Object[] {".jpg%00.jpg", false});
        datas.add(new Object[] {"%00.jpg", false});
        datas.add(new Object[] {".%00.jpg", false});
        datas.add(new Object[] {".jpg", false});
        datas.add(new Object[] {"/.jpg", false});
        datas.add(new Object[] {"\\.jpg", false});

        datas.add(new Object[] {"*.jpg", !isWindows});
        datas.add(new Object[] {":.jpg", !isWindows});
        datas.add(new Object[] {"\".jpg", !isWindows});
        datas.add(new Object[] {"<.jpg", !isWindows});
        datas.add(new Object[] {">.jpg", !isWindows});
        datas.add(new Object[] {"|.jpg", !isWindows});
        datas.add(new Object[] {"?.jpg", !isWindows});

        datas.add(new Object[] {"%2F.jpg", false});
        datas.add(new Object[] { '\000' + ".jpg", false});
        datas.add(new Object[] {"v2.7.2.jpg", true});

        return datas;
    }

    private final String fileName;
    private final boolean expected;

    public PathSecureUtilsTest(String fileName, boolean expected) {
        this.fileName = fileName;
        this.expected = expected;
    }

    @Test
    public void validateFilename() {

        //when:
        boolean result = PathSecureUtils.validateFilename(fileName);

        //then:
        assertEquals(expected, result);
    }
}