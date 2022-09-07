package org.scada_lts.ds.messaging;

public interface MessagingChannel<T> {
    boolean isOpen();
    void close(int timeout) throws Exception;
    T toOrigin();
}
