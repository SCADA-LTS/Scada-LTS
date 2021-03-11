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
package com.serotonin.mango.vo.report;

import java.io.PrintWriter;
import java.util.List;
import java.util.ResourceBundle;

import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.view.export.CsvWriter;
import com.serotonin.web.i18n.I18NUtils;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
public class EventCsvStreamer {
    public EventCsvStreamer(PrintWriter out, List<EventInstance> events, ResourceBundle bundle) {
        CsvWriter csvWriter = new CsvWriter();
        String[] data = new String[7];

        // Write the headers.
        data[0] = I18NUtils.getMessage(bundle, "reports.eventList.id");
        data[1] = I18NUtils.getMessage(bundle, "common.alarmLevel");
        data[2] = I18NUtils.getMessage(bundle, "common.activeTime");
        data[3] = I18NUtils.getMessage(bundle, "reports.eventList.message");
        data[4] = I18NUtils.getMessage(bundle, "reports.eventList.status");
        data[5] = I18NUtils.getMessage(bundle, "reports.eventList.ackTime");
        data[6] = I18NUtils.getMessage(bundle, "reports.eventList.ackUser");

        out.write(csvWriter.encodeRow(data));

        for (EventInstance event : events) {
            data[0] = Integer.toString(event.getId());
            data[1] = AlarmLevels.getAlarmLevelMessage(event.getAlarmLevel()).getLocalizedMessage(bundle);
            data[2] = event.getFullPrettyActiveTimestamp();
            data[3] = event.getMessage().getLocalizedMessage(bundle);

            if (event.isActive())
                data[4] = I18NUtils.getMessage(bundle, "common.active");
            else if (!event.isRtnApplicable())
                data[4] = "";
            else
                data[4] = event.getFullPrettyRtnTimestamp() + " - " + event.getRtnMessage().getLocalizedMessage(bundle);

            if (event.isAcknowledged()) {
                data[5] = event.getFullPrettyAcknowledgedTimestamp();

                LocalizableMessage ack = event.getExportAckMessage();
                if (ack == null)
                    data[6] = "";
                else
                    data[6] = ack.getLocalizedMessage(bundle);
            }
            else {
                data[5] = "";
                data[6] = "";
            }

            out.write(csvWriter.encodeRow(data));
        }

        out.flush();
        out.close();
    }
}
