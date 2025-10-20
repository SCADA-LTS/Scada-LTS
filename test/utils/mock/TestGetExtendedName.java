package utils.mock;

import com.serotonin.mango.vo.GetExtendedName;

public class TestGetExtendedName implements GetExtendedName {

    private final String name;

    public TestGetExtendedName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "$classname{" +
        "name='" + getName() + '\'' +
        '}';
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GetExtendedName)) return false;
        GetExtendedName that = (GetExtendedName) o;
        return this.getName().equals(that.getName());
    }
}
