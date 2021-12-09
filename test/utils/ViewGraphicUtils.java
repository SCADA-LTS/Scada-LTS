package utils;

import com.serotonin.mango.view.DynamicImage;
import com.serotonin.mango.view.ImageSet;

import java.util.stream.Stream;

public class ViewGraphicUtils {

    public static ImageSet newImageSet(String img, String...imgs) {
        return ImageSet.newInstance("imageSet_id", "imageSet_name", Stream.concat(Stream.of(img), Stream.of(imgs)).toArray(String[]::new), 0, 0, 0, 0);
    }

    public static DynamicImage newDynamicImage(String img) {
        return DynamicImage.newInstance("dynamicImage_id", "dynamicImage_name", img, 0, 0, 0, 0);
    }
}
