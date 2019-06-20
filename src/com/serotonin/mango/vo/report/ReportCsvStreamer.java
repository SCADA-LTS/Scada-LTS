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
import java.util.ResourceBundle;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.serotonin.mango.view.export.CsvWriter;
import com.serotonin.mango.view.text.TextRenderer;
import com.serotonin.web.i18n.I18NUtils;

/**
 * @author Matthew Lohbihler
 */
public class ReportCsvStreamer implements ReportDataStreamHandler {
    private final PrintWriter out;

    // Working fields
    private TextRenderer textRenderer;
    private final String[] data = new String[5];
    private final DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss");
    private final CsvWriter csvWriter = new CsvWriter();

    public ReportCsvStreamer(PrintWriter out, ResourceBundle bundle) {
        this.out = out;

        // Write the headers.
        data[0] = I18NUtils.getMessage(bundle, "reports.pointName");
        data[1] = I18NUtils.getMessage(bundle, "common.time");
        data[2] = I18NUtils.getMessage(bundle, "common.value");
        data[3] = I18NUtils.getMessage(bundle, "reports.rendered");
        data[4] = I18NUtils.getMessage(bundle, "common.annotation");
        out.write(csvWriter.encodeRow(data));
    }

    public void startPoint(ReportPointInfo pointInfo) {
        data[0] = pointInfo.getExtendedName();
        textRenderer = pointInfo.getTextRenderer();
    }

    public void pointData(ReportDataValue rdv) {
        data[1] = dtf.print(new DateTime(rdv.getTime()));

        if (rdv.getValue() == null)
            data[2] = data[3] = null;
        else {
            data[2] = rdv.getValue().toString();
            data[3] = textRenderer.getText(rdv.getValue(), TextRenderer.HINT_FULL);
        }

        data[4] = rdv.getAnnotation();

        out.write(csvWriter.encodeRow(data));
    }

    public void done() {
        out.flush();
        out.close();
    }
}
