package com.serotonin.mango.view.component;

import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.view.ImageSet;
import com.serotonin.web.dwr.DwrResponseI18n;
import org.junit.Before;
import org.junit.Test;
import utils.ViewGraphicUtils;

import static org.junit.Assert.*;

public class BinaryGraphicComponentTest {

    private PointValueTime valueTime;
    private BinaryGraphicComponent subject;

    @Before
    public void config() {
        valueTime = new PointValueTime(MangoValue.objectToValue(true), 0L);
        subject = new BinaryGraphicComponent();
        subject.setZeroImage(0);
        subject.setOneImage(1);
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
    public void when_getImage_for_ImageSet_available_then_inactive_image() {

        //given:
        String activeImg = "expected/image/active";
        String inactiveImg = "expected/image/inactive";
        ImageSet imageSet = ViewGraphicUtils.newImageSet(activeImg, inactiveImg);
        subject.tsetImageSet(imageSet);
        subject.setZeroImage(1);
        subject.setOneImage(0);
        PointValueTime inactiveValue = new PointValueTime(MangoValue.objectToValue(false), 0L);

        //when:
        String imgResult = subject.getImage(inactiveValue);

        //then:
        assertEquals(inactiveImg, imgResult);
    }

    @Test
    public void when_getImage_for_ImageSet_available_then_active_image() {

        //given:
        String activeImg = "expected/image/active";
        String inactiveImg = "expected/image/inactive";
        ImageSet imageSet = ViewGraphicUtils.newImageSet(activeImg, inactiveImg);
        subject.tsetImageSet(imageSet);
        subject.setZeroImage(1);
        subject.setOneImage(0);
        PointValueTime activeValue = new PointValueTime(MangoValue.objectToValue(true), 0L);

        //when:
        String imgResult = subject.getImage(activeValue);

        //then:
        assertEquals(activeImg, imgResult);
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

    @Test
    public void when_validate_for_ImageSet_unavailable_then_empty_errors() {

        //given:
        ImageSet imageSet = ImageSet.unavailable("imageSet_id");
        subject.tsetImageSet(imageSet);

        DwrResponseI18n responseResult = new DwrResponseI18n();

        //when:
        subject.validate(responseResult);

        //then:
        assertFalse("Expected state is not has messages.", responseResult.getHasMessages());
    }
}