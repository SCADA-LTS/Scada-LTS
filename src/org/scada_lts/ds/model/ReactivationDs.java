package org.scada_lts.ds.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * @autor grzegorz.bylica@gmail.com on 23.10.18
 *
 * Associated with class in vuejs SleepAndReactivationDS.vue
 */
public class ReactivationDs implements Serializable {

    private static final long serialVersionUID = 8828977247133645961L;

    public static final short TYPE_OF_SLEEP_MINUTE = 0;
    public static final short TYPE_OF_SLEEP_HOUR = 1;
    public static final short TYPE_OF_SLEEP_DAY = 2;

    private boolean sleep;
    private short type;
    private short value;

    public ReactivationDs() {
        this.sleep = false;
        this.type = TYPE_OF_SLEEP_MINUTE;
        this.value = 1; // 1 minute
    }

    public ReactivationDs(boolean sleep, short type, short value) {
        this.sleep = sleep;
        this.type = type;
        this.value = value;
    }

    public boolean isSleep() {
        return sleep;
    }

    public void setSleep(boolean sleep) {
        this.sleep = sleep;
    }

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public short getValue() {
        return value;
    }

    public void setValue(short value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReactivationDs that = (ReactivationDs) o;
        return sleep == that.sleep &&
                type == that.type &&
                value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sleep, type, value);
    }

    @Override
    public String toString() {
        return "ReactivationDs{" +
                "sleep=" + sleep +
                ", type=" + type +
                ", value=" + value +
                '}';
    }
}
