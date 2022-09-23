package org.scada_lts.utils;

import org.scada_lts.ds.messaging.BrokerMode;
import org.scada_lts.ds.messaging.protocol.amqp.AmqpVersion;
import org.scada_lts.ds.messaging.protocol.mqtt.MqttVersion;
import org.scada_lts.ds.state.IStateDs;
import org.scada_lts.ds.state.StartSleepStateDs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class ReflectionUtils {

    private static final Map<Class<?>, Supplier<Object>> objectGenerators = new HashMap<>();
    private static final Random random = new Random();

    static {
        objectGenerators.put(int.class, () -> random.nextInt(1234));
        objectGenerators.put(Integer.class, () -> random.nextInt(1234));
        objectGenerators.put(String.class, () -> UUID.randomUUID().toString());
        objectGenerators.put(boolean.class, random::nextBoolean);
        objectGenerators.put(Boolean.class, random::nextBoolean);
        objectGenerators.put(IStateDs.class, StartSleepStateDs::new);
        objectGenerators.put(AmqpVersion.class, () -> AmqpVersion.V0_9_1_EXT_AMQP);
        objectGenerators.put(BrokerMode.class, () -> BrokerMode.NATIVE);
        objectGenerators.put(List.class, ArrayList::new);
        objectGenerators.put(MqttVersion.class, () -> MqttVersion.V5_0_MQTT);
    }

    private ReflectionUtils() {}

    public static Object settingRandom(Object object) {
        List<Method> setters = getSetters(object.getClass());
        setting(object, setters);
        return object;
    }

    public static List<Method> getSetters(Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        return Arrays.stream(methods)
                .filter(a -> !Modifier.isStatic(a.getModifiers()))
                .filter(a -> a.getName().contains("set"))
                .collect(Collectors.toList());
    }

    public static void setting(Object object, List<Method> setters) {
        for(Method method: setters) {
            Class<?>[] parameter = method.getParameterTypes();
            if(parameter.length == 1) {
                try {
                    if(objectGenerators.containsKey(parameter[0]))
                        method.invoke(object, objectGenerators.get(parameter[0]).get());
                    else
                        throw new IllegalStateException("Not exists generator for type: " + parameter[0].getName());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
            if(parameter.length == 2) {
                try {
                    method.invoke(object, objectGenerators.get(parameter[0]).get(),
                            objectGenerators.get(parameter[1]).get());
                    if(!objectGenerators.containsKey(parameter[0]))
                        throw new IllegalStateException("Not exists generator for type: " + parameter[0].getName());
                    if(!objectGenerators.containsKey(parameter[1]))
                        throw new IllegalStateException("Not exists generator for type: " + parameter[1].getName());
                    method.invoke(object, objectGenerators.get(parameter[0]).get(),
                                objectGenerators.get(parameter[1]).get());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
            if(parameter.length > 2) {
                throw new IllegalStateException("Too many parameters: > 2");
            }
        }
    }
}
