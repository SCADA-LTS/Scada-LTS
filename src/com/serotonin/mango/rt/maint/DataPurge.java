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
package com.serotonin.mango.rt.maint;

import java.io.File;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.db.dao.EventDao;
import com.serotonin.mango.db.dao.PointValueDao;
import com.serotonin.mango.db.dao.ReportDao;
import com.serotonin.mango.db.dao.SystemSettingsDao;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.rt.dataImage.types.ImageValue;
import com.serotonin.mango.util.DateUtils;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.timer.CronTimerTrigger;
import com.serotonin.timer.TimerTask;

public class DataPurge {
    private final Log log = LogFactory.getLog(DataPurge.class);
    private long runtime;

    private final RuntimeManager rm = Common.ctx.getRuntimeManager();

    public static void schedule() {
        try {
            Common.timer.schedule(new DataPurgeTask());
        }
        catch (ParseException e) {
            throw new ShouldNeverHappenException(e);
        }
    }

    synchronized public void execute(long runtime) {
        this.runtime = runtime;
        executeImpl();
    }

    private void executeImpl() {
        log.info("Data purge started");

        // Get the data point information.
        DataPointDao dataPointDao = new DataPointDao();
        List<DataPointVO> dataPoints = dataPointDao.getDataPoints(null, false);
        int deleteCount = 0;
        for (DataPointVO dataPoint : dataPoints)
            deleteCount += purgePoint(dataPoint);
        // if (deleteCount > 0)
        // new PointValueDao().compressTables();

        log.info("Data purge ended, " + deleteCount + " point values deleted");

        // File data purge
        filedataPurge();

        // Event purge
        eventPurge();

        // Report instance purge
        reportPurge();
    }

    private long purgePoint(DataPointVO dataPoint) {
        if (dataPoint.getLoggingType() == DataPointVO.LoggingTypes.NONE)
            // If there is no logging, then there should be no data, unless logging was just changed to none. In either
            // case, it's ok to delete everything.
            return rm.purgeDataPointValues(dataPoint.getId());

        // No matter when this purge actually runs, we want it to act like it's midnight.
        DateTime cutoff = new DateTime(runtime);
        cutoff = DateUtils.truncateDateTime(cutoff, Common.TimePeriods.DAYS);
        cutoff = DateUtils.minus(cutoff, dataPoint.getPurgeType(), dataPoint.getPurgePeriod());

        return rm.purgeDataPointValues(dataPoint.getId(), cutoff.getMillis());
    }

    private void filedataPurge() {
        int deleteCount = 0;

        // Find all ids for which there should be a corresponding file
        List<Long> validIds = new PointValueDao().getFiledataIds();

        // Get all of the existing filenames.
        File dir = new File(Common.getFiledataPath());
        String[] files = dir.list();
        if (files != null) {
            for (String filename : files) {
                long pointId = ImageValue.parseIdFromFilename(filename);
                if (Collections.binarySearch(validIds, pointId) < 0) {
                    // Not found, so the point was deleted from the database. Delete the file.
                    new File(dir, filename).delete();
                    deleteCount++;
                }
            }
        }

        if (deleteCount > 0)
            log.info("Filedata purge ended, " + deleteCount + " files deleted");
    }

    private void eventPurge() {
        DateTime cutoff = DateUtils.truncateDateTime(new DateTime(runtime), Common.TimePeriods.DAYS);
        cutoff = DateUtils.minus(cutoff, SystemSettingsDao.getIntValue(SystemSettingsDao.EVENT_PURGE_PERIOD_TYPE),
                SystemSettingsDao.getIntValue(SystemSettingsDao.EVENT_PURGE_PERIODS));

        int deleteCount = new EventDao().purgeEventsBefore(cutoff.getMillis());
        if (deleteCount > 0)
            log.info("Event purge ended, " + deleteCount + " events deleted");
    }

    private void reportPurge() {
        DateTime cutoff = DateUtils.truncateDateTime(new DateTime(runtime), Common.TimePeriods.DAYS);
        cutoff = DateUtils.minus(cutoff, SystemSettingsDao.getIntValue(SystemSettingsDao.REPORT_PURGE_PERIOD_TYPE),
                SystemSettingsDao.getIntValue(SystemSettingsDao.REPORT_PURGE_PERIODS));

        int deleteCount = new ReportDao().purgeReportsBefore(cutoff.getMillis());
        if (deleteCount > 0)
            log.info("Report purge ended, " + deleteCount + " report instances deleted");
    }

    static class DataPurgeTask extends TimerTask {
        DataPurgeTask() throws ParseException {
            // Test trigger for running every 5 minutes.
            //super(new CronTimerTrigger("0 0/5 * * * ?"));
            // Trigger to run at 3:05am every day
            super(new CronTimerTrigger("0 5 3 * * ?"));
        }

        @Override
        public void run(long runtime) {
            new DataPurge().execute(runtime);
        }
    }
}
