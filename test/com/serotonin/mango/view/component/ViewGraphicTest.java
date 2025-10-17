package com.serotonin.mango.view.component;

import com.serotonin.mango.view.DynamicImage;
import com.serotonin.mango.view.ImageSet;
import org.junit.Test;
import utils.ViewGraphicUtils;

import static org.junit.Assert.*;

public class ViewGraphicTest {

    @Test
    public void when_newInstance_for_ImageSet_available_then_available() {
        //when:
        String imgExpected = "expected/image";
        ImageSet imageSet = ImageSet.newInstance("imageSet_id", "imageSet_name", new String[]{imgExpected}, 0, 0, 0, 0);

        //then:
        assertTrue("Expected state is an image available", imageSet.isAvailable());
    }


    @Test
    public void when_newInstance_for_DynamicImage_available_then_available() {
        //when:
        String imgExpected = "expected/image";
        DynamicImage dynamicImage = DynamicImage.newInstance("dynamicImage_id", "dynamicImage_name", imgExpected, 0, 0, 0, 0);

        //then:
        assertTrue("Expected state is an image available", dynamicImage.isAvailable());
    }

    @Test
    public void when_ImageSet_unavailable_for_id_then_unavailable() {
        //when:
        ImageSet imageSet = ImageSet.unavailable("imageSet_id");

        //then:
        assertFalse("Expected state is an image unavailable", imageSet.isAvailable());
    }

    @Test
    public void when_ImageSet_unavailable_for_null_then_unavailable() {
        //when:
        ImageSet imageSet = ImageSet.unavailable(null);

        //then:
        assertFalse("Expected state is an image unavailable", imageSet.isAvailable());
    }

    @Test
    public void when_DynamicImage_unavailable_for_id_then_unavailable() {
        //when:
        DynamicImage dynamicImage = DynamicImage.unavailable("dynamicImage_id");

        //then:
        assertFalse("Expected state is an image unavailable", dynamicImage.isAvailable());
    }

    @Test
    public void when_DynamicImage_unavailable_for_null_then_unavailable() {
        //when:
        DynamicImage dynamicImage = DynamicImage.unavailable(null);

        //then:
        assertFalse("Expected state is an image unavailable", dynamicImage.isAvailable());
    }

    @Test
    public void when_getImageFilename_for_ImageSet_available_then_filename() {

        //given:
        String imgExpected = "expected/image1";
        ImageSet imageSet = ViewGraphicUtils.newImageSet(imgExpected);

        //when:
        String imgResult = imageSet.getImageFilename(0);

        //then:
        assertEquals(imgExpected, imgResult);
    }


    @Test
    public void when_getImageFilename_for_DynamicImage_available_then_filename() {

        //given:
        String imgExpected = "expected/image2";
        DynamicImage dynamicImage = ViewGraphicUtils.newDynamicImage(imgExpected);

        //when:
        String imgResult = dynamicImage.getImageFilename();

        //then:
        assertEquals(imgExpected, imgResult);
    }

    @Test
    public void when_getImageFilename_for_ImageSet_unavailable_then_not_null_filename() {
        //when:
        ImageSet imageSet = ImageSet.unavailable("imageSet_id");

        //when:
        String imgResult = imageSet.getImageFilename(0);

        //then:
        assertNotNull(imgResult);
    }

    @Test
    public void when_getImageFilename_for_ImageSet_unavailable_then_not_empty_filename() {
        //when:
        ImageSet imageSet = ImageSet.unavailable("imageSet_id");

        //when:
        String imgResult = imageSet.getImageFilename(0);

        //then:
        assertNotNull(imgResult);
        assertFalse("Expected state is a non-empty value.", imgResult.trim().isEmpty());
    }

    @Test
    public void when_getImageFilename_for_DynamicImage_unavailable_then_not_null_filename() {
        //when:
        DynamicImage dynamicImage = DynamicImage.unavailable("dynamicImage_id");

        //when:
        String imgResult = dynamicImage.getImageFilename();

        //then:
        assertNotNull(imgResult);
    }

    @Test
    public void when_getImageFilename_for_DynamicImage_unavailable_then_not_empty_filename() {
        //when:
        DynamicImage dynamicImage = DynamicImage.unavailable("dynamicImage_id");

        //when:
        String imgResult = dynamicImage.getImageFilename();

        //then:
        assertNotNull(imgResult);
        assertFalse("Expected state is a non-empty value.", imgResult.trim().isEmpty());
    }

    @Test
    public void when_getImageCount_for_ImageSet_with_three_files_then_three() {
        //given:
        int countExpected = 3;
        String firstImg = "expected/image/first";
        String secondImg = "expected/image/second";
        String thirdImg = "expected/image/third";
        ImageSet imageSet = ViewGraphicUtils.newImageSet(firstImg, secondImg, thirdImg);

        //when:
        int countResult = imageSet.getImageCount();

        //then:
        assertEquals(countExpected, countResult);
    }
}