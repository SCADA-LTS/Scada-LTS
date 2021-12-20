package utils;

import java.util.Objects;

import static utils.ClassFinderUtils.getConstructor;

public class NameClass {
    private final Class<?> clazz;

    public NameClass(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NameClass)) return false;
        NameClass nameClass = (NameClass) o;
        return Objects.equals(getClazz().getName(), nameClass.getClazz().getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClazz().getName());
    }

    @Override
    public String toString() {
        return getConstructor(clazz).isPresent() ? getConstructor(clazz).get().toGenericString() : clazz.getName();
    }
}