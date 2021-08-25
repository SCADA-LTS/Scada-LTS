package org.scada_lts;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;

@RunWith(Parameterized.class)
public class InitStaticFieldTest {

    @Parameterized.Parameters(name= "{index}: classname: {0}")
    public static NameClass[] data() {
        String classPackage = "org.scada_lts.mango.service";
        String classPackage2 = "org.scada_lts.permissions.service";
        String classPackage3 = "org.scada_lts.service";
        String classPackage4 = "org.scada_lts.dao";
        List<NameClass> nameClasses = getClasses(classPackage);
        nameClasses.addAll(getClasses(classPackage2));
        nameClasses.addAll(getClasses(classPackage3));
        nameClasses.addAll(getClasses(classPackage4));
        return nameClasses.toArray(new NameClass[]{});
    }

    private static class NameClass {
        String name;
        Class<?> clazz;

        public NameClass(String name, Class<?> clazz) {
            this.name = name;
            this.clazz = clazz;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private NameClass nameClass;

    public InitStaticFieldTest(NameClass nameClass) {
        this.nameClass = nameClass;
    }

    @Test
    public void when_init_static_field_then_ok(){
        mock(nameClass.clazz);
    }

    private static List<NameClass> getClasses(String packageName) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = null;
        try {
            resources = classLoader.getResources(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        List<Class> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.stream()
                .filter(a -> !Modifier.isFinal(a.getModifiers()))
                .filter(a -> !a.getSimpleName().endsWith("Test"))
                .filter(a -> !a.getSimpleName().isEmpty())
                .map(a -> new NameClass(a.getSimpleName(), a))
                .collect(Collectors.toList());
    }

    private static List<Class> findClasses(File directory, String packageName) {
        List<Class> classes = new ArrayList<>();
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
                    classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return classes;
    }
}
