package com.serotonin.modbus4j;

public class ScadaExceptionResult extends ExceptionResult {

    private final ExceptionResult exceptionResult;

    public ScadaExceptionResult(ExceptionResult exceptionResult) {
        super(exceptionResult.getExceptionCode());
        this.exceptionResult = exceptionResult;
    }

    @Override
    public byte getExceptionCode() {
        return exceptionResult.getExceptionCode();
    }

    @Override
    public String getExceptionMessage() {
        return exceptionResult.getExceptionMessage();
    }
}
