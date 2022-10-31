package org.scada_lts.utils;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import com.serotonin.web.dwr.DwrMessageI18n;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;
import org.scada_lts.web.mvc.api.exceptions.BadRequestException;
import org.scada_lts.web.mvc.api.exceptions.InternalServerErrorException;
import org.scada_lts.web.mvc.api.exceptions.NotFoundException;
import org.scada_lts.web.mvc.api.exceptions.UnauthorizedException;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class DataSourcePointApiUtils {

    private DataSourcePointApiUtils() {}

    public static Map<String, String> toMapMessages(DwrResponseI18n responseI18n) {
        if(responseI18n.getHasMessages()) {
            AtomicInteger counter = new AtomicInteger();
            return responseI18n.getMessages().stream()
                    .collect(Collectors.toMap(a -> a.getContextKey() == null ? "message" + counter.incrementAndGet() : a.getContextKey(),
                            DataSourcePointApiUtils::getMessage, (a, b) -> b));
        }
        return Collections.emptyMap();
    }

    public static <I, T, R> R toObject(I id, User user, HttpServletRequest request,
                                       Function<I, T> get, BiPredicate<User, T> checkAccess,
                                       Function<T, R> creator) {
        if(id == null) {
            throw new BadRequestException("Object id cannot be null.", request.getRequestURI());
        }
        T object;
        try {
            object = get.apply(id);
        } catch (Exception ex) {
            throw new NotFoundException("Object with id not exists: " + id, request.getRequestURI());
        }
        if(object == null)
            throw new NotFoundException("Object with id not exists: " + id, request.getRequestURI());
        if(!checkAccess.test(user, object))
            throw new UnauthorizedException(request.getRequestURI());
        R result;
        try {
            result = creator.apply(object);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return result;
    }

    private static String getMessage(DwrMessageI18n a) {
        LocalizableMessage contextualMessage = a.getContextualMessage();
        if(contextualMessage == null) {
            LocalizableMessage genericMessage = a.getGenericMessage();
            if(genericMessage == null)
                return "unknown";
            return Common.getMessage(genericMessage.getKey(), genericMessage.getArgs());
        } else {
            return Common.getMessage(contextualMessage.getKey(), contextualMessage.getArgs());
        }
    }
}
