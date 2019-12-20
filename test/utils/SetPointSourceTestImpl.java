package utils;

import com.serotonin.mango.rt.dataImage.SetPointSource;

public class SetPointSourceTestImpl implements SetPointSource {

    @Override
    public int getSetPointSourceType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getSetPointSourceId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void raiseRecursionFailureEvent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void pointSetComplete() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "{}";
    }
}
