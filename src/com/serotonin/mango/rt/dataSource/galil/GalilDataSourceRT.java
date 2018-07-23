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
package com.serotonin.mango.rt.dataSource.galil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataSource.PollingDataSource;
import com.serotonin.mango.vo.dataSource.galil.GalilDataSourceVO;
import com.serotonin.messaging.MessageControl;
import com.serotonin.messaging.MessagingExceptionHandler;
import com.serotonin.messaging.StreamTransport;
import com.serotonin.web.i18n.LocalizableException;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
public class GalilDataSourceRT extends PollingDataSource implements MessagingExceptionHandler {
    public static final Charset CHARSET = Charset.forName("US-ASCII");

    private final Log LOG = LogFactory.getLog(GalilDataSourceRT.class);

    public static final int DATA_SOURCE_EXCEPTION_EVENT = 1;
    public static final int POINT_READ_EXCEPTION_EVENT = 2;
    public static final int POINT_WRITE_EXCEPTION_EVENT = 3;

    private final GalilDataSourceVO vo;
    private Socket socket;
    private MessageControl conn;

    public GalilDataSourceRT(GalilDataSourceVO vo) {
        super(vo);
        this.vo = vo;
        setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(), false);
    }

    @Override
    synchronized protected void doPoll(long time) {
        if (socket == null) {
            try {
                openConnection();
            }
            catch (IOException e) {
                return;
            }
        }

        Exception messageException = null;
        LocalizableMessage pointError = null;

        for (DataPointRT dataPoint : dataPoints) {
            GalilPointLocatorRT locator = dataPoint.getPointLocator();

            GalilRequest request = locator.getPollRequest();
            if (request != null) {
                LocalizableMessage sendMsg = null;

                try {
                    sendMsg = sendRequest(request, dataPoint, locator, time);
                }
                catch (IOException e) {
                    // The connection may have been reset, so try to reopen it and attempt the message again.
                    try {
                        LOG.debug("Keep-alive connection may have been reset. Attempting to re-open.");
                        closeConnection();
                        openConnection();
                        sendMsg = sendRequest(request, dataPoint, locator, time);
                    }
                    catch (Exception e2) {
                        messageException = e2;
                        closeConnection();
                        break;
                    }
                }

                if (sendMsg != null && pointError == null)
                    pointError = sendMsg;
            }
        }

        if (messageException != null) {
            // Raise an event.
            raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, time, true, new LocalizableMessage("event.pollingError",
                    messageException.getMessage()));
            LOG.info("Error while polling Galil device", messageException);
        }
        else
            // Deactivate any existing event.
            returnToNormal(DATA_SOURCE_EXCEPTION_EVENT, time);

        if (pointError != null) {
            // Raise an event.
            raiseEvent(POINT_READ_EXCEPTION_EVENT, time, true, pointError);
        }
        else
            // Deactivate any existing event.
            returnToNormal(POINT_READ_EXCEPTION_EVENT, time);
    }

    private LocalizableMessage sendRequest(GalilRequest request, DataPointRT dataPoint, GalilPointLocatorRT locator,
            long time) throws IOException {
        GalilResponse response = (GalilResponse) conn.send(request);

        if (response.isErrorResponse())
            return new LocalizableMessage("event.galil.errorResponse", dataPoint.getVO().getName());

        try {
            MangoValue value = locator.parsePollResponse(response.getResponseData(), dataPoint.getVO().getName());

            // Update the data image with the new value.
            dataPoint.updatePointValue(new PointValueTime(value, time));
        }
        catch (LocalizableException e) {
            return new LocalizableMessage("event.galil.parsingError", dataPoint.getVO().getName(),
                    response.getResponseData());
        }

        return null;
    }

    //
    // /
    // / Lifecycle
    // /
    //
    @Override
    public void initialize() {
        try {
            openConnection();
            returnToNormal(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis());
        }
        catch (Exception e) {
            raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(), true, new LocalizableMessage(
                    "event.initializationError", e.getMessage()));
            LOG.debug("Error while initializing data source", e);
            return;
        }
    }

    @Override
    synchronized public void terminate() {
        super.terminate();
        closeConnection();
    }

    //
    //
    // /
    // / Data source interface
    // /
    //
    //
    @Override
    synchronized public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source) {
        if (socket == null) {
            try {
                openConnection();
            }
            catch (IOException e) {
                raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(), true, new LocalizableMessage(
                        "event.galil.setPointFailed", dataPoint.getVO().getName(), e.getMessage()));
                LOG.debug("Error while initializing data source", e);
                return;
            }
            returnToNormal(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis());
        }

        GalilPointLocatorRT locator = dataPoint.getPointLocator();

        GalilRequest request = locator.getSetRequest(valueTime.getValue());
        if (request == null)
            raiseEvent(POINT_WRITE_EXCEPTION_EVENT, System.currentTimeMillis(), false, new LocalizableMessage(
                    "event.galil.setRequest", dataPoint.getVO().getName(), valueTime.getValue()));
        else {
            try {
                GalilResponse response = (GalilResponse) conn.send(request);

                if (response.isErrorResponse())
                    raiseEvent(POINT_WRITE_EXCEPTION_EVENT, System.currentTimeMillis(), false, new LocalizableMessage(
                            "event.galil.setResponse", dataPoint.getVO().getName()));
                else {
                    try {
                        // Update the data image with the new value.
                        dataPoint.updatePointValue(new PointValueTime(valueTime.getValue(), valueTime.getTime()));

                        MangoValue value = locator.parseSetResponse(response.getResponseData());
                        if (value != null)
                            // Update the data image with the newer value.
                            dataPoint.updatePointValue(new PointValueTime(value, System.currentTimeMillis()));
                    }
                    catch (LocalizableException e) {
                        raiseEvent(POINT_WRITE_EXCEPTION_EVENT, System.currentTimeMillis(), false,
                                new LocalizableMessage("event.galil.parsingError", dataPoint.getVO().getName(),
                                        response.getResponseData()));
                    }
                }
            }
            catch (IOException e) {
                raiseEvent(POINT_WRITE_EXCEPTION_EVENT, System.currentTimeMillis(), false, new LocalizableMessage(
                        "event.galil.sendError", dataPoint.getVO().getName(), e.getMessage()));
            }
        }
    }

    //
    //
    // /
    // / MessagingConnectionListener interface
    // /
    //
    //
    public void receivedException(Exception e) {
        raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(), true, new LocalizableMessage(
                "event.galil.connectionError", e.getMessage()));
    }

    public void receivedMessageMismatchException(Exception e) {
        raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(), true, new LocalizableMessage(
                "event.galil.connectionError", e.getMessage()));
    }

    public void receivedResponseException(Exception e) {
        raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(), true, new LocalizableMessage(
                "event.galil.connectionError", e.getMessage()));
    }

    //
    // /
    // / Private methods
    // /
    //
    private void openConnection() throws IOException {
        // Try 'retries' times to get the socket open.
        int retries = vo.getRetries();
        StreamTransport transport;
        while (true) {
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(vo.getHost(), vo.getPort()), vo.getTimeout());
                transport = new StreamTransport(socket.getInputStream(), socket.getOutputStream());
                break;
            }
            catch (IOException e) {
                closeConnection();

                if (retries <= 0)
                    throw e;
                LOG.warn("Open connection failed, trying again.");
                retries--;

                // Add a small delay
                try {
                    Thread.sleep(500);
                }
                catch (InterruptedException e1) {
                    // no op
                }
            }
        }

        conn = new MessageControl();
        conn.setRetries(vo.getRetries());
        conn.setTimeout(vo.getTimeout());
        conn.setExceptionHandler(this);
        conn.start(transport, new GalilMessageParser(), null, new GalilWaitingRoomKeyFactory());
        transport.start("Galil data source");
    }

    private void closeConnection() {
        if (conn != null)
            conn.close();

        try {
            if (socket != null)
                socket.close();
        }
        catch (IOException e) {
            receivedException(e);
        }

        conn = null;
        socket = null;
    }
}
