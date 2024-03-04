package com.serotonin.mango.vo;

import com.serotonin.mango.rt.dataImage.SetPointSource;

public class RestApiSource implements SetPointSource {
    @Override
    public int getSetPointSourceType() {
        return Types.REST_API;
    }

    @Override
    public int getSetPointSourceId() {
        return -1;
    }

    @Override
    public void raiseRecursionFailureEvent() {

    }

    @Override
    public void pointSetComplete() {

    }
}
