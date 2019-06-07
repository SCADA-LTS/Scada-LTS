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
package com.serotonin.mango.rt.dataSource.http;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.io.StreamUtils;
import com.serotonin.mango.Common;
import com.serotonin.mango.ImageSaveException;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataImage.types.ImageValue;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.rt.dataSource.PollingDataSource;
import com.serotonin.mango.rt.maint.work.WorkItem;
import com.serotonin.mango.vo.dataSource.http.HttpImageDataSourceVO;
import com.serotonin.mango.vo.dataSource.http.HttpImagePointLocatorVO;
import com.serotonin.util.image.BoxScaledImage;
import com.serotonin.util.image.ImageUtils;
import com.serotonin.util.image.JpegImageFormat;
import com.serotonin.util.image.PercentScaledImage;
import com.serotonin.web.i18n.LocalizableException;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
public class HttpImageDataSourceRT extends PollingDataSource {
    static final Log LOG = LogFactory.getLog(HttpImageDataSourceRT.class);

    public static final int DATA_RETRIEVAL_FAILURE_EVENT = 1;
    public static final int FILE_SAVE_EXCEPTION_EVENT = 2;

    public HttpImageDataSourceRT(HttpImageDataSourceVO vo) {
        super(vo);
        setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(), false);
    }

    @Override
    public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source) {
        // no op
    }

    @Override
    protected void doPoll(long time) {
        ImageRetrieverMonitor monitor = new ImageRetrieverMonitor();

        // Add all of the retrievers to the monitor.
        for (DataPointRT dp : dataPoints) {
            ImageRetriever retriever = new ImageRetriever(monitor, dp, time);
            monitor.addRetriever(retriever);
        }

        while (!monitor.isEmpty()) {
            synchronized (monitor) {
                try {
                    monitor.wait(1000);
                }
                catch (InterruptedException e) {
                    // no op
                }
            }
        }

        // Check the results.
        if (monitor.getRetrievalFailure() != null)
            raiseEvent(DATA_RETRIEVAL_FAILURE_EVENT, time, true, monitor.getRetrievalFailure());
        else
            returnToNormal(DATA_RETRIEVAL_FAILURE_EVENT, time);

        if (monitor.getSaveFailure() != null)
            raiseEvent(FILE_SAVE_EXCEPTION_EVENT, time, true, monitor.getSaveFailure());
        else
            returnToNormal(FILE_SAVE_EXCEPTION_EVENT, time);
    }

    class ImageRetrieverMonitor {
        private final List<ImageRetriever> retrievers = new ArrayList<ImageRetriever>();
        private LocalizableMessage retrievalFailure;
        private LocalizableMessage saveFailure;

        synchronized void addRetriever(ImageRetriever retriever) {
            retrievers.add(retriever);
            Common.ctx.getBackgroundProcessing().addWorkItem(retriever);
        }

        synchronized void removeRetriever(ImageRetriever retriever) {
            retrievers.remove(retriever);

            if (retrievalFailure == null && retriever.getRetrievalFailure() != null)
                retrievalFailure = retriever.getRetrievalFailure();

            if (saveFailure == null && retriever.getSaveFailure() != null)
                saveFailure = retriever.getSaveFailure();

            if (retrievers.isEmpty()) {
                synchronized (this) {
                    notifyAll();
                }
            }
        }

        public LocalizableMessage getRetrievalFailure() {
            return retrievalFailure;
        }

        public LocalizableMessage getSaveFailure() {
            return saveFailure;
        }

        public boolean isEmpty() {
            return retrievers.isEmpty();
        }
    }

    class ImageRetriever implements WorkItem {
        private final ImageRetrieverMonitor monitor;
        private final DataPointRT dp;
        private final long time;
        private LocalizableMessage retrievalFailure;
        private LocalizableMessage saveFailure;

        ImageRetriever(ImageRetrieverMonitor monitor, DataPointRT dp, long time) {
            this.monitor = monitor;
            this.dp = dp;
            this.time = time;
        }

        public void execute() {
            try {
                executeImpl();
            }
            finally {
                monitor.removeRetriever(this);
            }
        }

        private void executeImpl() {
            HttpImagePointLocatorVO vo = dp.getVO().getPointLocator();

            byte[] data;
            try {
                data = getData(vo.getUrl(), vo.getTimeoutSeconds(), vo.getRetries(), vo.getReadLimit());
            }
            catch (Exception e) {
                if (e instanceof LocalizableException)
                    retrievalFailure = ((LocalizableException) e).getLocalizableMessage();
                else
                    retrievalFailure = new LocalizableMessage("event.httpImage.retrievalError", vo.getUrl(),
                            e.getMessage());
                LOG.info("Error retrieving page '" + vo.getUrl() + "'", e);
                return;
            }

            try {
                if (vo.getScaleType() == HttpImagePointLocatorVO.SCALE_TYPE_PERCENT) {
                    // Percentage scale the image.
                    PercentScaledImage scaler = new PercentScaledImage(((float) vo.getScalePercent()) / 100);
                    data = ImageUtils.scaleImage(scaler, data, new JpegImageFormat(0.85f));
                }
                else if (vo.getScaleType() == HttpImagePointLocatorVO.SCALE_TYPE_BOX) {
                    // Box scale the image.
                    BoxScaledImage scaler = new BoxScaledImage(vo.getScaleWidth(), vo.getScaleHeight());
                    data = ImageUtils.scaleImage(scaler, data, new JpegImageFormat(0.85f));
                }
            }
            catch (Exception e) {
                saveFailure = new LocalizableMessage("event.httpImage.scalingError", e.getMessage());
                LOG.info("Error scaling image", e);
                return;
            }

            // Save the new image
            try {
                dp.updatePointValue(new PointValueTime(new ImageValue(data, ImageValue.TYPE_JPG), time), false);
            }
            catch (ImageSaveException e) {
                saveFailure = new LocalizableMessage("event.httpImage.saveError", e.getMessage());
                LOG.info("Error saving image data", e);
                return;
            }
        }

        public LocalizableMessage getRetrievalFailure() {
            return retrievalFailure;
        }

        public LocalizableMessage getSaveFailure() {
            return saveFailure;
        }

        public int getPriority() {
            return WorkItem.PRIORITY_HIGH;
        }
    }

    public static byte[] getData(String url, int timeoutSeconds, int retries, int readLimitKb)
            throws LocalizableException {
        byte[] data;
        while (true) {
            HttpClient client = Common.getHttpClient(timeoutSeconds * 1000);
            GetMethod method = null;
            LocalizableMessage message;

            try {
                method = new GetMethod(url);
                int responseCode = client.executeMethod(method);

                if (responseCode == HttpStatus.SC_OK) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    StreamUtils.transfer(method.getResponseBodyAsStream(), baos, readLimitKb * 1024);
                    data = baos.toByteArray();
                    break;
                }
                message = new LocalizableMessage("event.http.response", url, responseCode);
            }
            catch (Exception e) {
                message = DataSourceRT.getExceptionMessage(e);
            }
            finally {
                if (method != null)
                    method.releaseConnection();
            }

            if (retries <= 0)
                throw new LocalizableException(message);
            retries--;

            // Take a little break instead of trying again immediately.
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                // no op
            }
        }

        return data;
    }
}
