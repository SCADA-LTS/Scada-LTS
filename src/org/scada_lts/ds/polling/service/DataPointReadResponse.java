package org.scada_lts.ds.polling.service;

import com.serotonin.mango.rt.dataImage.PointValueTime;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DataPointReadResponse {

    private final Map<String, Result> values;

    public DataPointReadResponse() {
        this.values = new HashMap<>();
    }

    public Optional<Throwable> getError(String xid) {
        Result result = values.get(xid);
        if(result == null)
            return Optional.empty();
        return Optional.ofNullable(result.getError());
    }

    public Optional<PointValueTime> getValue(String xid) {
        Result result = values.get(xid);
        if(result == null)
            return Optional.empty();
        return Optional.ofNullable(result.getValue());
    }

    public void add(String xid, Throwable error) {
        values.computeIfPresent(xid, (x,res)-> new Result(
                x,
                error,
                res.getValue())
        );
        values.putIfAbsent(xid, new Result(xid, error));
    }

    public void add(String xid, PointValueTime value) {
        values.computeIfPresent(xid, (x,res)-> new Result(
                x,
                res.getError(),
                value)
        );
        values.putIfAbsent(xid, new Result(xid, value));
    }

    public static class Result {

        private final String xid;
        private final Throwable error;
        private final PointValueTime value;

        public Result(String xid, Throwable error, PointValueTime value) {
            this.error = error;
            this.xid = xid;
            this.value = value;
        }

        public Result(String xid, Throwable error) {
            this.xid = xid;
            this.value = null;
            this.error = error;
        }

        public Result(String xid, PointValueTime value) {
            this.xid = xid;
            this.value = value;
            this.error = null;
        }

        public Throwable getError() {
            return error;
        }

        public PointValueTime getValue() {
            return value;
        }

        public String getXid() {
            return xid;
        }
    }
}
