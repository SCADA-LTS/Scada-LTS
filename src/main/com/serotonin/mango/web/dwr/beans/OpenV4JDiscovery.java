/*
 *   Mango - Open Source M2M - http://mango.serotoninsoftware.com
 *   Copyright (C) 2010 Arne Pl\u00f6se
 *   @author Arne Pl\u00f6se
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.web.dwr.beans;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.openv4j.DataPoint;
import net.sf.openv4j.Devices;
import net.sf.openv4j.Group;
import net.sf.openv4j.protocolhandlers.ProtocolHandler;
import net.sf.openv4j.protocolhandlers.SegmentedDataContainer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.web.i18n.I18NUtils;

/**
 * @author aploese
 */
public class OpenV4JDiscovery implements TestingUtility {

    private void addToDataContainer(DataPoint dataPoint) {
        dc.addToDataContainer(dataPoint);
    }

    private void setMessage(String message) {
        this.message = message;
    }

    class SearchThread extends Thread {

        @Override
        public void run() {
            LOG.info("start search");
            try {
                protocolHandler.setReadRequest(dc);
                synchronized (dc) {
                    dc.wait(4000 * dc.getDataBlockCount());
                }

            }
            catch (InterruptedException ex) {
                LOG.info("Interrupted)");
            }
            catch (Exception ex) {
                LOG.warn("SearchThread.run", ex);
            }
            LOG.info("Search finished!");
            try {
                finished = true;
                protocolHandler.close();
                if (sPort != null) {
                    sPort.close();
                }
            }
            catch (InterruptedException ex) {
                LOG.info("Interrupted)");
            }
        }
    }

    static final Log LOG = LogFactory.getLog(OpenV4JDiscovery.class);
    final ResourceBundle bundle;
    SerialPort sPort;
    final ProtocolHandler protocolHandler;
    private final AutoShutOff autoShutOff;
    String message;
    boolean finished;
    private SearchThread searchThread;
    final SegmentedDataContainer dc;

    public OpenV4JDiscovery(ResourceBundle bundle) {
        LOG.info("OpenV4J Discovery(...)");
        this.bundle = bundle;
        autoShutOff = new AutoShutOff(AutoShutOff.DEFAULT_TIMEOUT * 4) {

            @Override
            void shutOff() {
                message = I18NUtils.getMessage(OpenV4JDiscovery.this.bundle, "dsEdit.mbus.tester.auto");
                OpenV4JDiscovery.this.cleanup();
            }
        };
        // Thread starten , der sucht....
        protocolHandler = new ProtocolHandler();
        dc = new SegmentedDataContainer();
    }

    public static OpenV4JDiscovery searchDataPoints(ResourceBundle bundle, String commPortId) {
        OpenV4JDiscovery result = new OpenV4JDiscovery(bundle);
        for (DataPoint dp : DataPoint.values()) {
            result.addToDataContainer(dp);
        }
        result.start(commPortId);
        result.setMessage(I18NUtils.getMessage(bundle, "dsEdit.openv4j.tester.searchingDataPoints"));
        return result;
    }

    public static OpenV4JDiscovery detectDevice(ResourceBundle bundle, String commPortId) {
        OpenV4JDiscovery result = new OpenV4JDiscovery(bundle);
        result.addToDataContainer(DataPoint.COMMON_CONFIG_DEVICE_TYPE_ID);
        result.start(commPortId);
        result.setMessage(I18NUtils.getMessage(bundle, "dsEdit.openv4j.tester.detectingDevice"));
        return result;
    }

    private void start(String commPortId) {
        try {
            sPort = ProtocolHandler.openPort(commPortId);
            protocolHandler.setStreams(sPort.getInputStream(), sPort.getOutputStream());
        }
        catch (NoSuchPortException ex) {
            Logger.getLogger(OpenV4JDiscovery.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (PortInUseException ex) {
            Logger.getLogger(OpenV4JDiscovery.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (UnsupportedCommOperationException ex) {
            Logger.getLogger(OpenV4JDiscovery.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex) {
            Logger.getLogger(OpenV4JDiscovery.class.getName()).log(Level.SEVERE, null, ex);
        }
        searchThread = new SearchThread();
        searchThread.start();
    }

    public void addDeviceInfo(Map<String, Object> result) {
        LOG.info("addDecviceInfo()");
        autoShutOff.update();
        result.put("finished", finished);
        if (finished) {
            result.put("deviceName", Devices.getDeviceById(
                    dc.getShortHex(DataPoint.COMMON_CONFIG_DEVICE_TYPE_ID.getAddr())).name());
            result.put("message", message);
        }
    }

    public void addUpdateInfo(Map<String, Object> result) {
        LOG.info("addUpdateInfo()");
        autoShutOff.update();

        DataPoint[] p = DataPoint.values();
        List<OpenV4JDataPointBean> values = new ArrayList<OpenV4JDataPointBean>(p.length);
        DataPoint[] sortedPoints = DataPoint.getSortedPoints();
        for (Group g : Group.values()) {
            for (DataPoint pr : sortedPoints) {
                if (g == pr.getGroup()) {
                    String valueAsString = String.format("%s", pr.decode(dc));
                    values.add(new OpenV4JDataPointBean(pr, valueAsString));
                }
            }
        }
        result.put("valuesByGroup", values);
        result.put("message", message);
        result.put("finished", finished);
    }

    @Override
    public void cancel() {
        LOG.info("cancel()");
        message = I18NUtils.getMessage(bundle, "dsEdit.openv4j.tester.cancelled");
        cleanup();
    }

    void cleanup() {
        LOG.info("cleanup()");
        if (!finished) {
            finished = true;
            try {
                protocolHandler.close();
            }
            catch (InterruptedException ex) {
                LOG.error("Shutdown comport", ex);
            }
            autoShutOff.cancel();
            searchThread.interrupt();
        }
    }
}
