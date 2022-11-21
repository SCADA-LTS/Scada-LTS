package org.scada_lts.utils;

import com.serotonin.mango.vo.User;
import org.scada_lts.web.mvc.api.exceptions.BadRequestException;
import org.scada_lts.web.mvc.api.exceptions.InternalServerErrorException;
import org.scada_lts.web.mvc.api.exceptions.NotFoundException;
import org.scada_lts.web.mvc.api.exceptions.UnauthorizedException;

import javax.servlet.http.HttpServletRequest;
import java.util.function.BiPredicate;
import java.util.function.Function;

public final class DataSourcePointApiUtils {

    private DataSourcePointApiUtils() {}

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
}
