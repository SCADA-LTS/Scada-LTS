package org.scada_lts.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import utils.UploadFileTestUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class UploadGraphicsFileUtilsTest {

    @Parameterized.Parameters(name = "{index}: file: {0}, accept: {1}")
    public static List<Object[]> data() {
        List<Object[]> datas = new ArrayList<>();

        datas.add(new Object[] {"gifFile.gif", true});
        datas.add(new Object[] {"jpgFile.jpg", true});
        datas.add(new Object[] {"pngFile.png", true});
        datas.add(new Object[] {"svgFile.svg", false});
        datas.add(new Object[] {"354.svg", false});
        datas.add(new Object[] {"354-bad.svg", false});

        datas.add(new Object[] {"jspFile.jsp", false});
        datas.add(new Object[] {"xmlFile.xml", false});
        datas.add(new Object[] {"info.txt", true});
        datas.add(new Object[] {"abc.abc", false});
        datas.add(new Object[] {"abc", false});
        datas.add(new Object[] {"Thumbs.db", false});
        datas.add(new Object[] {"jsFile.js", false});

        datas.add(new Object[] {"jspAsSvg.svg", false});
        datas.add(new Object[] {"jspAsBmp.bmp", false});
        datas.add(new Object[] {"jspAsGif.gif", false});
        datas.add(new Object[] {"jspAsJpg.jpg", false});
        datas.add(new Object[] {"jspAsPng.png", false});

        datas.add(new Object[] {"xmlAsSvg.svg", false});
        datas.add(new Object[] {"xmlAsBmp.bmp", false});
        datas.add(new Object[] {"xmlAsGif.gif", false});
        datas.add(new Object[] {"xmlAsJpg.jpg", false});
        datas.add(new Object[] {"xmlAsPng.png", false});

        datas.add(new Object[] {"jsAsSvg.svg", false});
        datas.add(new Object[] {"jsAsBmp.bmp", false});
        datas.add(new Object[] {"jsAsGif.gif", false});
        datas.add(new Object[] {"jsAsJpg.jpg", false});
        datas.add(new Object[] {"jsAsPng.png", false});

        datas.add(new Object[] {"/../../jsAsSvg.svg", false});
        datas.add(new Object[] {"/../jsAsBmp.bmp", false});
        datas.add(new Object[] {"./jsAsGif.gif", false});
        datas.add(new Object[] {"../jsAsJpg.jpg", false});
        datas.add(new Object[] {"././jsAsPng.png", false});

        datas.add(new Object[] {"gifFile.zip.gif", false});
        datas.add(new Object[] {"jpgFile.o.jpg", false});
        datas.add(new Object[] {"pngFile.jpg.png", false});

        datas.add(new Object[] {"", false});
        datas.add(new Object[] {"\\.jpg", false});
        datas.add(new Object[] {"/.jpg", false});
        datas.add(new Object[] {".php%00.jpg", false});
        datas.add(new Object[] {".jpg%00.jpg", false});
        datas.add(new Object[] {"%00.jpg", false});
        datas.add(new Object[] {".%00.jpg", false});
        datas.add(new Object[] {".jpg", false});

        datas.add(new Object[] {"txt" + File.pathSeparator + "info.txt", false});
        return datas;
    }

    private final String filePath;
    private final boolean expected;

    public UploadGraphicsFileUtilsTest(String fileName, boolean expected) {
        this.filePath = UploadFileTestUtils.getResourcesPath("svg", fileName);
        this.expected = expected;
    }

    @Test
    public void when_isToGraphics() {

        //given:
        File file = new File(filePath);

        //when:
        boolean result = UploadFileUtils.isToGraphics(file);

        //then:
        assertEquals(expected, result);
    }
}