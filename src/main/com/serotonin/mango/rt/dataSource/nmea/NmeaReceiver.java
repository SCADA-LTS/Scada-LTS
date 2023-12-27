/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.rt.dataSource.nmea;

import gnu.io.SerialPort;

import java.nio.charset.Charset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.io.serial.SerialParameters;
import com.serotonin.io.serial.SerialUtils;
import com.serotonin.messaging.IncomingMessage;
import com.serotonin.messaging.IncomingRequestMessage;
import com.serotonin.messaging.MessageControl;
import com.serotonin.messaging.MessageParser;
import com.serotonin.messaging.OutgoingResponseMessage;
import com.serotonin.messaging.RequestHandler;
import com.serotonin.messaging.StreamTransport;
import com.serotonin.util.queue.ByteQueue;

/**
 * @author Matthew Lohbihler
 */
public class NmeaReceiver implements RequestHandler, MessageParser {
    private static final Log LOG = LogFactory.getLog(NmeaReceiver.class);

    private static final Charset CHARSET = Charset.forName("US-ASCII");
    private static final byte[] MESSAGE_START = "$".getBytes(CHARSET);
    private static final byte[] MESSAGE_END = "\r\n".getBytes(CHARSET);

    //
    // Configuration fields.
    private final SerialParameters serialParameters;
    private final NmeaMessageListener listener;

    // Runtime fields.
    private SerialPort serialPort;
    private StreamTransport transport;
    private MessageControl conn;

    public NmeaReceiver(NmeaMessageListener listener, SerialParameters params) {
        this.listener = listener;
        serialParameters = params;
    }

    public void initialize() throws Exception {
        serialPort = SerialUtils.openSerialPort(serialParameters);
        transport = new StreamTransport(serialPort.getInputStream(), serialPort.getOutputStream());

        conn = new MessageControl();
        conn.setExceptionHandler(listener);
        conn.start(transport, this, this, null);
        transport.start("NMEA data source");
    }

    public void terminate() {
        if (conn != null)
            conn.close();
        SerialUtils.close(serialPort);
    }

    //
    // /
    // / Message parsing
    // /
    //
    public IncomingMessage parseMessage(ByteQueue queue) throws Exception {
        // Look for the start indicator and toss everything before it.
        int start = queue.indexOf(MESSAGE_START);

        // If it wasn't found, clear the buffer.
        if (start == -1) {
            LOG.warn("Discarding data " + queue);
            queue.clear();
            return null;
        }

        // Dump all of the data before the start indicator.
        if (start > 0) {
            LOG.warn("Discarding data before start=" + start + ", queue=" + queue);
            queue.pop(start);
        }

        // Look for an end indicator.
        int endPos = queue.indexOf(MESSAGE_END);
        if (endPos == -1)
            // There is no end indicator yet, so wait for the next set of data.
            return null;

        // Dump the start indicator.
        queue.pop(MESSAGE_START.length);

        // Tokenize the data
        byte[] dataArr = new byte[endPos - MESSAGE_START.length];
        queue.pop(dataArr);
        String data = new String(dataArr, CHARSET);

        // Dump the end indicator.
        queue.pop(MESSAGE_END.length);

        // Check if there is a checksum
        if (data.length() > 2 && data.charAt(data.length() - 3) == '*') {
            int checksum = Integer.parseInt(data.substring(data.length() - 2, data.length()), 16);

            int sum = 0;
            for (int i = 0; i < dataArr.length - 3; i++)
                sum ^= dataArr[i];
            sum &= 0xff;

            if (checksum != sum) {
                LOG.warn("Message failed checksum, calc=" + Integer.toString(sum, 16) + ", data=" + data);
                return null;
            }

            // Dump the checksum
            data = data.substring(0, data.length() - 3);
        }

        return new NmeaMessage(data);
    }

    //
    // /
    // / Message handling
    // /
    //
    public OutgoingResponseMessage handleRequest(IncomingRequestMessage request) {
        listener.receivedMessage(((NmeaMessage) request));

        // We just listen for incoming data, so there is never a response to send back.
        return null;
    }
}
