package com.serotonin.mango.util;

import com.serotonin.mango.rt.dataImage.AnnotatedPointValueTime;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.vo.User;
import com.serotonin.web.i18n.I18NUtils;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public final class AnnotatedPointValueUtils {

    private AnnotatedPointValueUtils(){}

    public static AnnotatedPointValueTime createAnnotatedPointValueTime(PointValueTime pvt, SetPointSource source) {
        String sourceDescriptionArgument = getSourceDescriptionArgument(source);
        AnnotatedPointValueTime annotatedPointValueTime = new AnnotatedPointValueTime(pvt.getValue(), pvt.getTime(),
                source.getSetPointSourceType(), source.getSetPointSourceId());
        annotatedPointValueTime.setSourceDescriptionArgument(sourceDescriptionArgument);
        return annotatedPointValueTime;
    }

    public static String getSourceDescriptionArgument(SetPointSource source) {
        if(source instanceof User) {
            User user = (User) source;
            return user.getUsername();
        } else {
            return source.getClass().getSimpleName();
        }
    }

    public static String getSourceDescriptionKey(int sourceType) {
        switch (sourceType) {
            case SetPointSource.Types.ANONYMOUS:
                return "annotation.anonymous";
            case SetPointSource.Types.EVENT_HANDLER:
                return "annotation.eventHandler";
            case SetPointSource.Types.USER:
                return "annotation.user";
            case SetPointSource.Types.POINT_LINK:
                return "annotation.pointLink";
            case SetPointSource.Types.REST_API:
                return "annotation.api";
            default:
                return "annotation.unknown";
        }
    }

    public static String getAnnotation(ResourceBundle bundle, int sourceType, String sourceDescriptionArgument) {
        String pattern = I18NUtils.getMessage(bundle, getSourceDescriptionKey(sourceType));
        if (sourceDescriptionArgument == null)
            return MessageFormat.format(pattern, I18NUtils.getMessage(bundle, "common.deleted"));
        return MessageFormat.format(pattern, sourceDescriptionArgument);
    }
}
