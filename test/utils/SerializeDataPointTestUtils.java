package utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SerializeDataPointTestUtils {

    public static ObjectMapper createObjectMapper(Map<String, Integer> stringToInteger,
                                                  Deserializers deserializers) {
        ObjectMapper mapper = new ObjectMapper();
        configureObjectMapper(mapper, createModule(deserializers, stringToInteger));
        return mapper;
    }

    private static void configureObjectMapper(ObjectMapper mapper, SimpleModule module) {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(module);
    }

    public static class Deserializers {
        public Map<Class<?>, Deserializer<?>> maps = new HashMap<>();

        public <T, S extends T> void put(Class<T> key, Deserializer<S> impl) {
            maps.put(key, impl);
        }

        public <T, S extends T> Deserializer<S> get(Class<T> key) {
            return Deserializer.class.cast(maps.get(key));
        }

        public <T, S extends T> Set<Map.Entry<Class<T>, Deserializer<S>>> entrySet() {
            return Set.class.cast(maps.entrySet());
        }
    }

    private static SimpleModule createModule(Deserializers deserializers, Map<String, Integer> stringToInteger) {
        SimpleModule module = new SimpleModule();

        for(Map.Entry<Class<Object>, Deserializer<Object>> object: deserializers.entrySet()) {
            module.addDeserializer(object.getKey(), object.getValue());
        }
        module.addDeserializer(int.class, new StdDelegatingDeserializer<>(new StringToIntConverter(stringToInteger)));
        return module;
    }

    private static class StringToIntConverter extends StdConverter<String, Integer> {

        private Map<String, Integer> types;

        public StringToIntConverter(Map<String, Integer> types) {
            this.types = types;
        }

        @Override
        public Integer convert(String s) {
            if(types.containsKey(s))
                return types.get(s);
            return Integer.parseInt(s);
        }
    }

    public static class Deserializer<T> extends StdDeserializer<T> {

        private Class<T> clazz;

        public Deserializer(Class<T> vc) {
            super(vc);
            this.clazz = vc;
        }

        @Override
        public T deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException {
            ObjectMapper mapper = (ObjectMapper)jp.getCodec();
            ObjectNode obj = mapper.readTree(jp);

            try {
                T object = clazz.newInstance();
                if(object instanceof PointLocatorVO) {

                }
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }

            return mapper.treeToValue(obj, clazz);
        }
    }
}
