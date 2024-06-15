package org.scada_lts.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.dynalink.linker.support.TypeUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Stream;

public final class BlockingQueuesUtils {

    private BlockingQueuesUtils() {}

    private static final Log log = LogFactory.getLog(BlockingQueuesUtils.class);


    public static <T> BlockingQueue<T> newBlockingQueue(String className, BlockingQueue<T> defaultImpl, Object... args) {
        return (BlockingQueue<T>) newObject(className, defaultImpl, BlockingQueue.class, args);
    }

    public static <R, T extends R> R newObject(String className, T defaultImpl, Class<R> defaultInterface, Object... args) {
        try {
            Class<?> type = Class.forName(className);
            if(type.isInterface())
                return defaultImpl;
            Class<?>[] argsTypes = toPrimitiveTypes(args);
            if(!ClassUtils.hasConstructor(type, argsTypes))
                return defaultImpl;
            Object object = type.getDeclaredConstructor(argsTypes).newInstance(args);
            return defaultInterface.cast(object);
        } catch (Throwable e) {
            log.error(e);
            return defaultImpl;
        }
    }

    public static Object[] parseObjects(String valuesAsString, String valueTypesAsString) {
        String[] values = valuesAsString.split(";");
        String[] valueTypes = valueTypesAsString.split(";");
        if(values.length != valueTypes.length) {
            throw new IllegalStateException();
        }
        List<Object> objects = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for(int i=0; i < valueTypes.length; i++) {
            try {
                Class<?> clazz = Class.forName(valueTypes[i]);
                objects.add(objectMapper.readValue(values[i], toPrimitiveType(clazz)));
            } catch (Throwable throwable) {
                throw new IllegalStateException(throwable);
            }
        }
        return objects.toArray(Object[]::new);
    }


    private static Class<?>[] toPrimitiveTypes(Object[] args) {
        return Stream.of(args).map(a -> toPrimitiveType(a.getClass())).toArray(Class[]::new);
    }

    private static Class<?> toPrimitiveType(Class<?> type) {
        Class<?> primitive = TypeUtilities.getPrimitiveType(type);
        if(primitive == null)
            return type;
        return primitive;
    }

}
