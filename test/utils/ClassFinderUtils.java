package utils;

import com.serotonin.mango.vo.DataPointVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ClassFinderUtils {

    private static final Log LOG = LogFactory.getLog(ClassFinderUtils.class);

    private static final Predicate<Class<?>> DAO_SERVICE_FILTER = a -> a.getSimpleName().contains("Dao") || a.getSimpleName().contains("DAO")
            || a.getSimpleName().contains("service") || a.getSimpleName().contains("Service");

    private static final Predicate<Class<?>> NO_INTERFACE_PUBLIC_FILTER = a -> !Modifier.isInterface(a.getModifiers())
            && !Modifier.isAbstract(a.getModifiers()) && Modifier.isPublic(a.getModifiers())
            && getConstructor(a).isPresent() && Modifier.isPublic(getConstructor(a).get().getModifiers());

    private static final Predicate<Class<?>> DAO_SERVICE_NO_INTERFACE_PUBLIC_FILTER =
            DAO_SERVICE_FILTER.and(NO_INTERFACE_PUBLIC_FILTER);

    public static Set<NameClass> getDaoServiceClasses(String... packageNames) {
        return getClasses(DAO_SERVICE_FILTER, packageNames);
    }

    public static Set<NameClass> getNoInterfacePublicClasses(String... packageNames) {
        return getClasses(NO_INTERFACE_PUBLIC_FILTER, packageNames);
    }

    public static Set<NameClass> getDaoServiceNoInterfacePublicClasses(String... packageNames) {
        return getClasses(DAO_SERVICE_NO_INTERFACE_PUBLIC_FILTER, packageNames);
    }

    public static Set<NameClass> getClasses(String... packageNames) {
        Set<NameClass> set = new HashSet<>();
        for(String packageName: packageNames) {
            set.addAll(getClasses(packageName));
        }
        return set;
    }

    public static Set<NameClass> getClasses(Predicate<Class<?>> filter, String... packageNames) {
        Set<NameClass> set = new HashSet<>();
        for(String packageName: packageNames) {
            set.addAll(getClasses(packageName, filter));
        }
        return set;
    }

    public static Set<NameClass> getClasses(String packageName) {
        return getClasses(packageName, a -> true);
    }

    public static Set<NameClass> getClasses(String packageName, Predicate<Class<?>> filter) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = null;
        try {
            resources = classLoader.getResources(path);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        List<Class<?>> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.stream()
                .filter(a -> !Modifier.isFinal(a.getModifiers()))
                .filter(a -> !a.getSimpleName().endsWith("Test"))
                .filter(a -> !a.getSimpleName().isEmpty())
                .filter(filter)
                .map(NameClass::new)
                .collect(Collectors.toSet());
    }

    public static Object[] nulls(Parameter... parameters) {
        List<Object> list = new ArrayList<>();
        for(Parameter parameter: parameters) {
            Class<?> type = parameter.getType();
            if(type.isPrimitive()) {
                if(type.equals(boolean.class)) {
                    list.add(false);
                } else if(type.equals(double.class)) {
                    list.add(0.0);
                } else {
                    list.add(0);
                }
            } else {
                try {
                    if(type.equals(DataPointVO.class)) {
                        list.add(type.getConstructor(int.class).newInstance(1));
                    } else {
                        Optional<Constructor<?>> constructor = getConstructor(type);
                        Object object;
                        if (constructor.isPresent() && constructor.get().getParameters().length > 0) {
                            object = constructor.get().newInstance(nulls(constructor.get().getParameters()));
                        } else {
                            object = type.newInstance();
                        }
                        list.add(object);
                    }
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    list.add(null);
                }
            }
        }
        return list.toArray();
    }

    private static List<Class<?>> findClasses(File directory, String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                try {
                    LOG.info(file);
                    classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
                } catch (ClassNotFoundException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
        return classes;
    }

    public static Optional<Constructor<?>> getConstructor(Class<?> clazz) {
        for(Constructor<?> constructor: clazz.getConstructors()) {
            if(constructor.getParameters().length == 0) {
                return Optional.of(constructor);
            }
        }
        for(Constructor<?> constructor: clazz.getConstructors()) {
            if(constructor.getParameters().length > 0) {
                if(constructor.getParameters()[0].getType().isPrimitive())
                    return Optional.of(constructor);
            }
        }
        for(Constructor<?> constructor: clazz.getConstructors()) {
            if(constructor.getParameters().length > 0) {
                return Optional.of(constructor);
            }
        }
        return Optional.empty();
    }
}
