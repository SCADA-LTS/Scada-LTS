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
package com.serotonin.mango.web.servlet;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;

import com.serotonin.mango.rt.dataImage.PointValueFacade;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.ImageValue;
import com.serotonin.util.image.BoxScaledImage;
import com.serotonin.util.image.ImageUtils;
import com.serotonin.util.image.JpegImageFormat;
import com.serotonin.util.image.PercentScaledImage;

public class ImageValueServlet extends BaseInfoServlet {
    private static final long serialVersionUID = -1;

    public static final String servletPath = "imageValue/";
    public static final String historyPrefix = "hst";

    /**
     * @TODO(security): Validate the point access against the user. If anonymous, make sure the view allows public
     *                  access to the point.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String imageInfo = request.getPathInfo();

        // The imageInfo contains the timestamp of the last point value and the data point id. The intention is to
        // create a name for the virtual image such that the browser will cache the data and only come here when the
        // data has change. The format of the name is:
        // /{last timestamp}_{data point id}.${value extension}

        try {
            // Remove the / and the extension
            int dot = imageInfo.indexOf('.');
            imageInfo = imageInfo.substring(1, dot);

            // Split by underscore.
            String[] imageBits = imageInfo.split("_");

            // Get the data.
            String timestamp = imageBits[0];
            int dataPointId = Integer.parseInt(imageBits[1]);
            int scalePercent = getIntRequestParameter(request, "p", -1);
            int width = getIntRequestParameter(request, "w", -1);
            int height = getIntRequestParameter(request, "h", -1);

            // DataPointRT dp = Common.ctx.getRuntimeManager().getDataPoint(dataPointId);
            // Permissions.ensureDataPointReadPermission(Common.getUser(request), dp.getVO());

            PointValueFacade pointValueFacade = new PointValueFacade(dataPointId);
            PointValueTime pvt = null;
            if (timestamp.startsWith(historyPrefix)) {
                // Find the point with the given timestamp
                long time = Long.parseLong(timestamp.substring(historyPrefix.length()));
                pvt = pointValueFacade.getPointValueAt(time);
            }
            else
                // Use the latest value
                pvt = pointValueFacade.getPointValue();

            if (pvt == null || pvt.getValue() == null || !(pvt.getValue() instanceof ImageValue))
                response.sendError(HttpStatus.SC_NOT_FOUND);
            else {
                ImageValue imageValue = (ImageValue) pvt.getValue();
                byte[] data = imageValue.getImageData();

                if (scalePercent != -1) {
                    // Scale the image
                    PercentScaledImage scaler = new PercentScaledImage(((float) scalePercent) / 100);
                    data = ImageUtils.scaleImage(scaler, data, new JpegImageFormat(0.85f));
                }
                else if (width != -1 && height != -1) {
                    // Scale the image
                    BoxScaledImage scaler = new BoxScaledImage(width, height);
                    data = ImageUtils.scaleImage(scaler, data, new JpegImageFormat(0.85f));
                }

                response.getOutputStream().write(data);
            }
        }
        catch (FileNotFoundException e) {
            // no op
        }
        catch (InterruptedException e) {
            // no op
        }
        catch (StringIndexOutOfBoundsException e) {
            // no op
        }
        catch (NumberFormatException e) {
            // no op
        }
        catch (ArrayIndexOutOfBoundsException e) {
            // no op
        }
        catch (IllegalArgumentException e) {
            // no op
        }
    }
}
