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
package com.serotonin.mango.web.dwr.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.serotonin.io.serial.SerialParameters;
import com.serotonin.mango.rt.dataSource.nmea.NmeaMessage;
import com.serotonin.mango.rt.dataSource.nmea.NmeaMessageListener;
import com.serotonin.mango.rt.dataSource.nmea.NmeaReceiver;
import com.serotonin.web.i18n.I18NUtils;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
public class NmeaUtilListener implements NmeaMessageListener, TestingUtility {
    private final ResourceBundle bundle;
    private final NmeaReceiver nmeaReceiver;
    private final List<String> messages = new ArrayList<String>();
    private String message;

    // Auto shut-off stuff
    private final AutoShutOff autoShutOff;

    public NmeaUtilListener(ResourceBundle bundle, String commPortId, int baudRate) {
        this.bundle = bundle;
        message = I18NUtils.getMessage(bundle, "dsEdit.nmea.tester.listening");

        SerialParameters params = new SerialParameters();
        params.setCommPortId(commPortId);
        params.setBaudRate(baudRate);
        params.setPortOwnerName("Mango NMEA Test Listener");

        nmeaReceiver = new NmeaReceiver(this, params);

        try {
            nmeaReceiver.initialize();
        }
        catch (Exception e) {
            message = getMessage("dsEdit.nmea.tester.startError", e.getMessage());
        }

        autoShutOff = new AutoShutOff() {
            @Override
            void shutOff() {
                NmeaUtilListener.this.cancel();
            }
        };
    }

    public List<String> getMessages() {
        autoShutOff.update();
        synchronized (messages) {
            List<String> l = new ArrayList<String>(messages);
            messages.clear();
            return l;
        }
    }

    public String getMessage() {
        autoShutOff.update();
        return message;
    }

    public void cancel() {
        if (nmeaReceiver != null) {
            autoShutOff.cancel();
            nmeaReceiver.terminate();
        }
    }

    public void receivedException(Exception e) {
        message = getMessage("dsEdit.nmea.tester.exception", e.getMessage());
    }

    public void receivedMessageMismatchException(Exception e) {
        message = getMessage("dsEdit.nmea.tester.mismatch", e.getMessage());
    }

    public void receivedResponseException(Exception e) {
        message = getMessage("dsEdit.nmea.tester.response", e.getMessage());
    }

    public void receivedMessage(NmeaMessage message) {
        synchronized (messages) {
            messages.add(message.getMessage());
        }
    }

    private String getMessage(String key, String param) {
        return new LocalizableMessage(key, param).getLocalizedMessage(bundle);
    }
}
