package com.serotonin.mango.view.component;

import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.view.ImageSet;
import org.junit.Before;
import org.junit.Test;
import utils.ViewGraphicUtils;

import static org.junit.Assert.*;

public class AnalogGraphicComponentTest {

    private PointValueTime valueTime;
    private AnalogGraphicComponent subject;

    @Before
    public void config() {
        valueTime = new PointValueTime(MangoValue.objectToValue(123.4), 0L);
        subject = new AnalogGraphicComponent();
        subject.setMin(0);
        subject.setMax(150);
    }

    @Test
    public void when_getImage_for_ImageSet_not_available_then_unavailable_image() {

        //given:
        ImageSet imageSet = ImageSet.unavailable("imageSet_id");
        String imgExpected = imageSet.getImageFilename(0);
        subject.tsetImageSet(imageSet);


        //when:
        String imgResult = subject.getImage(valueTime);

        //then:
        assertEquals(imgExpected, imgResult);
    }

    @Test
    public void when_getImage_for_ImageSet_available_then_image() {

        //given:
        String imgExpected = "expected/image";
        ImageSet imageSet = ViewGraphicUtils.newImageSet(imgExpected);
        subject.tsetImageSet(imageSet);


        //when:
        String imgResult = subject.getImage(valueTime);

        //then:
        assertEquals(imgExpected, imgResult);
    }

    @Test
    public void when_getImage_for_ImageSet_is_null_then_imageSetNotLoaded() {

        //given:
        subject.tsetImageSet(null);
        String imgExpected = "imageSetNotLoaded";

        //when:
        String imgResult = subject.getImage(valueTime);

        //then:
        assertEquals(imgExpected, imgResult);
    }
}