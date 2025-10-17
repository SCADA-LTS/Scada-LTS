package com.serotonin.mango.view.component;

import com.serotonin.mango.view.DynamicImage;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DynamicGraphicComponentTest {

    private DynamicGraphicComponent subject;

    @Before
    public void config() {
        subject = new DynamicGraphicComponent();
    }

    @Test
    public void when_getImage_for_ImageSet_not_available_then_unavailable_image() {

        //given:
        DynamicImage dynamicImage = DynamicImage.unavailable("dynamicImage_id");
        String imgExpected = dynamicImage.getImageFilename();
        subject.tsetDynamicImage(dynamicImage);

        //when:
        String imgResult = subject.getImage();

        //then:
        assertEquals(imgExpected, imgResult);
    }

    @Test
    public void when_getImage_for_ImageSet_available_then_image() {

        //given:
        String imgExpected = "expected/image";
        DynamicImage dynamicImage = DynamicImage.newInstance("dynamicImage_id", "dynamicImage_name", imgExpected, 0, 0, 0, 0);
        subject.tsetDynamicImage(dynamicImage);

        //when:
        String imgResult = subject.getImage();

        //then:
        assertEquals(imgExpected, imgResult);
    }

    @Test
    public void when_getImage_for_ImageSet_is_null_then_imageSetNotLoaded() {

        //given:
        subject.tsetDynamicImage(null);

        //when:
        String imgResult = subject.getImage();

        //then:
        assertNull(imgResult);
    }
}