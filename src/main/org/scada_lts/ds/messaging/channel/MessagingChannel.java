package org.scada_lts.ds.messaging.channel;

import org.scada_lts.ds.messaging.exception.MessagingChannelException;

public interface MessagingChannel {
    boolean isOpen();
    void close(int timeout) throws MessagingChannelException;
    void publish(String message) throws MessagingChannelException;
}
