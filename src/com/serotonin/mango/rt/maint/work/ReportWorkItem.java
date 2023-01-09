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
package com.serotonin.mango.rt.maint.work;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


import com.serotonin.mango.util.SendUtils;
import com.serotonin.mango.web.email.AbstractEmailAttachment;
import com.serotonin.mango.web.email.AbstractEmailInline;
import com.serotonin.mango.web.email.IMsgContent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;

import com.serotonin.InvalidArgumentException;
import com.serotonin.io.StreamUtils;
import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.db.dao.MailingListDao;
import com.serotonin.mango.db.dao.ReportDao;
import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.mango.vo.report.ImageChartUtils;
import com.serotonin.mango.vo.report.ReportChartCreator;
import com.serotonin.mango.vo.report.ReportChartCreator.PointStatistics;
import com.serotonin.mango.vo.report.ReportInstance;
import com.serotonin.mango.vo.report.ReportPointVO;
import com.serotonin.mango.vo.report.ReportVO;
import com.serotonin.util.ColorUtils;
import com.serotonin.util.StringUtils;
import com.serotonin.web.i18n.LocalizableMessage;
import org.scada_lts.dao.report.ReportInstancePointDAO;

/**
 * @author Matthew Lohbihler
 */
public class ReportWorkItem extends AbstractBeforeAfterWorkItem {
	static final Log LOG = LogFactory.getLog(ReportWorkItem.class);

	public int getPriority() {
		return WorkItem.PRIORITY_LOW;
	}

	public static void queueReport(ReportVO report) {
		LOG.info("Queuing report with id " + report.getId());

		// Verify that the user is not disabled.
		User user = new UserDao().getUser(report.getUserId());
		if (user.isDisabled())
			return;

		// User is ok. Continue...
		ReportWorkItem item = new ReportWorkItem();

		// Create the database record in process.
		item.reportConfig = report;
		ReportInstance reportInstance = new ReportInstance(report);

		item.user = user;
		item.reportDao = new ReportDao();
		item.reportDao.saveReportInstance(reportInstance);

		// Start the report work item out of process.
		item.reportInstance = reportInstance;
		Common.ctx.getBackgroundProcessing().addWorkItem(item);

		LOG.info("Queued report with id " + report.getId() + ", instance id "
				+ reportInstance.getId());
	}

	ReportVO reportConfig;
	private User user;
	private ReportDao reportDao;
	private ReportInstance reportInstance;
	List<File> filesToDelete = new ArrayList<File>();

	@Override
	public void work() {
		LOG.info("Running report with id " + reportConfig.getId()
				+ ", instance id " + reportInstance.getId());

		reportInstance.setRunStartTime(System.currentTimeMillis());
		reportDao.saveReportInstance(reportInstance);
		ResourceBundle bundle = Common.getBundle();

		// Create a list of DataPointVOs to which the user has permission.
		DataPointDao dataPointDao = new DataPointDao();
		List<ReportInstancePointDAO.PointInfo> points = new ArrayList<ReportInstancePointDAO.PointInfo>(
				reportConfig.getPoints().size());
		for (ReportPointVO reportPoint : reportConfig.getPoints()) {
			DataPointVO point = dataPointDao.getDataPoint(reportPoint
					.getPointId());
			if (point != null
					&& Permissions.hasDataPointReadPermission(user, point)) {
				String colour = null;
				try {
					if (!StringUtils.isEmpty(reportPoint.getColour()))
						colour = ColorUtils
								.toHexString(reportPoint.getColour())
								.substring(1);
				} catch (InvalidArgumentException e) {
					// Should never happen since the colour would have been
					// validated on save, so just let it go
					// as null.
				}
				points.add(new ReportInstancePointDAO.PointInfo(point, colour, reportPoint
						.isConsolidatedChart()));
			}
		}

		int recordCount = 0;
		try {
			if (!points.isEmpty())
				recordCount = reportDao.runReport(reportInstance, points,
						bundle);
		} catch (RuntimeException e) {
			recordCount = -1;
			throw e;
		} catch (Throwable e) {
			recordCount = -1;
			throw new RuntimeException("Report instance failed", e);
		} finally {
			reportInstance.setRunEndTime(System.currentTimeMillis());
			reportInstance.setRecordCount(recordCount);
			reportDao.saveReportInstance(reportInstance);
		}
	}

	private void addImage(IMsgContent emailContent, String imagePath) {
		emailContent.addInline(new AbstractEmailInline.FileInline(imagePath, Common.ctx
				.getServletContext().getRealPath(imagePath)));
	}

	private void addFileAttachment(IMsgContent emailContent, String name,
								   File file) {
		if (file != null) {
			if (reportConfig.isZipData()) {
				try {
					File zipFile = File.createTempFile("tempZIP", ".zip");
					ZipOutputStream zipOut = new ZipOutputStream(
							new FileOutputStream(zipFile));
					zipOut.putNextEntry(new ZipEntry(name));

					FileInputStream in = new FileInputStream(file);
					StreamUtils.transfer(in, zipOut);
					in.close();

					zipOut.closeEntry();
					zipOut.close();

					emailContent
							.addAttachment(new AbstractEmailAttachment.FileAttachment(
									name + ".zip", zipFile));

					filesToDelete.add(zipFile);
				} catch (IOException e) {
					LOG.error("Failed to create zip file", e);
				}
			} else
				emailContent.addAttachment(new AbstractEmailAttachment.FileAttachment(
						name, file));

			filesToDelete.add(file);
		}
	}

	@Override
	public void workFail(Exception exception) {
		LOG.error("Failed generate report with id " + reportConfig.getId()
				+ ", instance id " + reportInstance.getId() + ", error: " +exception.getMessage(), exception);
	}

	@Override
	public void workSuccess() {
		if (reportConfig.isEmail()) {
			ResourceBundle bundle = Common.getBundle();
			String inlinePrefix = "R" + System.currentTimeMillis() + "-"
					+ reportInstance.getId() + "-";

			// We are creating an email from the result. Create the content.
			final ReportChartCreator creator = new ReportChartCreator(bundle);
			creator.createContent(reportInstance, reportDao, inlinePrefix,
					reportConfig.isIncludeData());

			// Create the to list
			Set<String> addresses = new MailingListDao().getRecipientAddresses(
					reportConfig.getRecipients(),
					new DateTime(reportInstance.getReportStartTime()));
			String[] toAddrs = addresses.toArray(new String[0]);

			// Create the email content object.
			IMsgContent emailContent = IMsgContent.newInstance(null,
					creator.getHtml(), Common.UTF8);

			// Add the consolidated chart
			if (creator.getImageData() != null)
				emailContent.addInline(new AbstractEmailInline.ByteArrayInline(
						inlinePrefix + ReportChartCreator.IMAGE_CONTENT_ID,
						creator.getImageData(), ImageChartUtils
						.getContentType()));

			// Add the point charts
			for (PointStatistics pointStatistics : creator.getPointStatistics()) {
				if (pointStatistics.getImageData() != null)
					emailContent.addInline(new AbstractEmailInline.ByteArrayInline(
							inlinePrefix + pointStatistics.getChartName(),
							pointStatistics.getImageData(), ImageChartUtils
							.getContentType()));
			}

			// Add optional images used by the template.
			for (String s : creator.getInlineImageList())
				addImage(emailContent, s);

			// Check if we need to attach the data.
			if (reportConfig.isIncludeData()) {
				addFileAttachment(emailContent, reportInstance.getName()
						+ ".csv", creator.getExportFile());
				addFileAttachment(emailContent, reportInstance.getName()
						+ "Events.csv", creator.getEventFile());
				addFileAttachment(emailContent, reportInstance.getName()
						+ "Comments.csv", creator.getCommentFile());
			}

			AfterWork.WorkFinally workFinally = new AfterWork.WorkFinally() {};
			if (reportConfig.isIncludeData()) {
				// See that the temp file(s) gets deleted after the email is
				// sent.
				workFinally = new AfterWork.WorkFinally() {
					@Override
					public void workFinally(Map<String, Exception> exceptions) {
						for (File file : filesToDelete) {
							if (!file.delete())
								LOG.warn("Temp file " + file.getPath()
										+ " not deleted");
						}
					}

					@Override
					public void workFinallyFail(Exception finallyException, Map<String, Exception> exceptions) {
						LOG.error("Failed deleting temp file for report with id " + reportConfig.getId()
								+ ", instance id " + reportInstance.getId() + ", error: " + finallyException.getMessage(), finallyException);
					}
				};
			}

			LocalizableMessage lm = new LocalizableMessage(
					"ftl.scheduledReport", reportConfig.getName());

			SendUtils.sendMsg(toAddrs, lm.getLocalizedMessage(bundle),
					emailContent, new AfterWork() {
						@Override
						public void workFail(Exception exception) {
							LOG.error("Failed sending report with id " + reportConfig.getId()
									+ ", instance id " + reportInstance.getId() + ", error: " +exception.getMessage(), exception);
						}
					}, workFinally);
			// Delete the report instance.
			// reportDao.deleteReportInstance(reportInstance.getId(),
			// user.getId());
		}
		LOG.info("Finished running report with id " + reportConfig.getId()
				+ ", instance id " + reportInstance.getId());
	}

	@Override
	public void workSuccessFail(Exception exception) {
		LOG.error("Failed sending report with id " + reportConfig.getId()
				+ ", instance id " + reportInstance.getId() + ", error: " +exception.getMessage(), exception);
	}

	@Override
	public String toString() {
		return "ReportWorkItem{" +
				"reportConfig=" + reportConfig +
				", user=" + user +
				", reportInstance=" + reportInstance +
				'}';
	}

	@Override
	public String getDetails() {
		return this.toString();
	}
}
