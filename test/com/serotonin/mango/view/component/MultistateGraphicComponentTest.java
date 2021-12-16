package com.serotonin.mango.view.component;

import com.serotonin.db.IntValuePair;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.view.ImageSet;
import com.serotonin.web.dwr.DwrResponseI18n;
import org.junit.Before;
import org.junit.Test;
import utils.ViewGraphicUtils;

import java.util.Arrays;

import static org.junit.Assert.*;

public class MultistateGraphicComponentTest {

    private PointValueTime valueTime;
    private MultistateGraphicComponent subject;

    @Before
    public void config() {
        valueTime = new PointValueTime(MangoValue.objectToValue(1), 0L);
        subject = new MultistateGraphicComponent();
        subject.setDefaultImage(0);
        subject.setImageStateList(Arrays.asList(new IntValuePair(0,"1"), new IntValuePair(1, "2"), new IntValuePair(2, "0")));
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
    public void when_getImage_for_ImageSet_available_value_0_then_first_image() {

        //given:
        String firstImg = "expected/image/first";
        String secondImg = "expected/image/second";
        String thirdImg = "expected/image/third";
        ImageSet imageSet = ViewGraphicUtils.newImageSet(firstImg, secondImg, thirdImg);
        subject.tsetImageSet(imageSet);
        subject.setImageStateList(Arrays.asList(new IntValuePair(0,"0"), new IntValuePair(1, "1"), new IntValuePair(2, "2")));

        PointValueTime valueTime = new PointValueTime(MangoValue.objectToValue(0), 0L);

        //when:
        String imgResult = subject.getImage(valueTime);

        //then:
        assertEquals(firstImg, imgResult);
    }

    @Test
    public void when_getImage_for_ImageSet_available_value_1_then_second_image() {

        //given:
        String firstImg = "expected/image/first";
        String secondImg = "expected/image/second";
        String thirdImg = "expected/image/third";
        ImageSet imageSet = ViewGraphicUtils.newImageSet(firstImg, secondImg, thirdImg);
        subject.tsetImageSet(imageSet);
        subject.setImageStateList(Arrays.asList(new IntValuePair(0,"0"), new IntValuePair(1, "1"), new IntValuePair(2, "2")));

        PointValueTime valueTime = new PointValueTime(MangoValue.objectToValue(1), 0L);

        //when:
        String imgResult = subject.getImage(valueTime);

        //then:
        assertEquals(secondImg, imgResult);
    }

    @Test
    public void when_getImage_for_ImageSet_available_value_2_then_third_image() {

        //given:
        String firstImg = "expected/image/first";
        String secondImg = "expected/image/second";
        String thirdImg = "expected/image/third";
        ImageSet imageSet = ViewGraphicUtils.newImageSet(firstImg, secondImg, thirdImg);
        subject.tsetImageSet(imageSet);
        subject.setImageStateList(Arrays.asList(new IntValuePair(0,"0"), new IntValuePair(1, "1"), new IntValuePair(2, "2")));

        PointValueTime valueTime = new PointValueTime(MangoValue.objectToValue(2), 0L);

        //when:
        String imgResult = subject.getImage(valueTime);

        //then:
        assertEquals(thirdImg, imgResult);
    }

    @Test
    public void when_getImage_for_ImageSet_available_value_3_then_default_image() {

        //given:
        String firstImg = "expected/image/first";
        String secondImg = "expected/image/second";
        String defaultImg = "expected/image/third";
        int defaultImage = 2;
        ImageSet imageSet = ViewGraphicUtils.newImageSet(firstImg, secondImg, defaultImg);
        subject.tsetImageSet(imageSet);
        subject.setDefaultImage(defaultImage);
        subject.setImageStateList(Arrays.asList(new IntValuePair(0,"0"), new IntValuePair(1, "1"), new IntValuePair(2, "2")));

        PointValueTime valueTime = new PointValueTime(MangoValue.objectToValue(3), 0L);

        //when:
        String imgResult = subject.getImage(valueTime);

        //then:
        assertEquals(defaultImg, imgResult);
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
        subject.setImageStateList(Arrays.asList(new IntValuePair(3,"0"), new IntValuePair(4, "1")));
        DwrResponseI18n responseResult = new DwrResponseI18n();

        //when:
        subject.validate(responseResult);

        //then:
        assertFalse("Expected state is not has messages.", responseResult.getHasMessages());
    }

    @Test
    public void when_validate_for_ImageSet_with_state_index_greater_images_size_then_errors() {

        //given:
        String firstImg = "expected/image/first";
        String secondImg = "expected/image/second";
        String thirdImg = "expected/image/third";
        ImageSet imageSet = ViewGraphicUtils.newImageSet(firstImg, secondImg, thirdImg);
        subject.tsetImageSet(imageSet);
        subject.setImageStateList(Arrays.asList(new IntValuePair(3,"1")));
        DwrResponseI18n responseResult = new DwrResponseI18n();

        //when:
        subject.validate(responseResult);

        //then:
        assertTrue("Expected state is has messages.", responseResult.getHasMessages());
    }

}