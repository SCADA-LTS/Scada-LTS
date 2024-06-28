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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.*;

import com.serotonin.mango.rt.maint.work.WorkItemPriority;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.maint.work.WorkItem;
import com.serotonin.util.ILifecycle;

import static com.serotonin.mango.util.ThreadPoolExecutorUtils.createPool;

/**
 * A cheesy name for a class, i know, but it pretty much says it like it is.
 * This class keeps an inbox of items to process, and oddly enough, processes
 * them. (Oh, and removes them from the inbox when it's done.)
 *
 * @author Matthew Lohbihler
 */
public class BackgroundProcessing implements ILifecycle {
	public static final String JOB_NAME = BackgroundProcessing.class.getName();
	public static final String JOB_GROUP = "maintenance";

	private static final Log LOG = LogFactory.getLog(BackgroundProcessing.class);

	private ThreadPoolExecutor mediumPriorityService;
	private ExecutorService lowPriorityService;

	public void addWorkItem(final WorkItem item) {
		Runnable runnable = new Runnable() {
			public void run() {
				try {
					item.execute();
				} catch (Throwable t) {
					try {
						LOG.error("Error in work item", t);
					} catch (RuntimeException e) {
						t.printStackTrace();
					}
				}
			}
		};

		if (item.getPriorityType() == WorkItemPriority.HIGH)
			Common.timer.execute(runnable);

		else if (item.getPriorityType() == WorkItemPriority.MEDIUM)
			mediumPriorityService.execute(new Runnable() {
				public void run() {
					try {
						item.execute();
					} catch (Throwable t) {
						if (t != null) {
							StringWriter sw = new StringWriter();
							PrintWriter pw = new PrintWriter(sw);
							t.printStackTrace(pw);
							String sStackTrace = sw.toString();
							if ((sStackTrace != null) && (LOG != null)) {
								if (!sStackTrace.contains("java.lang.NullPointerException")) {
									LOG.error("Error in work item: " + sStackTrace);
								}
							}
						}
					}
				}
			});

		else {
			lowPriorityService.execute(runnable);
		}

	}

	public int getMediumPriorityServiceQueueSize() {
		return mediumPriorityService.getQueue().size();
	}

	public void initialize() {
		mediumPriorityService = createPool(WorkItemPriority.MEDIUM);
		mediumPriorityService.allowCoreThreadTimeOut(true);
		lowPriorityService = createPool(WorkItemPriority.LOW);
	}

	public void terminate() {
		// Close the executor services.
		mediumPriorityService.shutdown();
		lowPriorityService.shutdown();
	}

	public void joinTermination() {
		boolean medDone = false;
		boolean lowDone = false;

		try {
			// With 5 second waits and a worst case of both of both high and low
			// priority jobs that just won't finish,
			// this thread will wait a maximum of 6 minutes.
			int rewaits = 36;
			while (rewaits > 0) {
				if (!medDone
						&& mediumPriorityService.awaitTermination(5,
								TimeUnit.SECONDS))
					medDone = true;
				if (!lowDone
						&& lowPriorityService.awaitTermination(5,
								TimeUnit.SECONDS))
					lowDone = true;

				if (lowDone && medDone)
					break;

				if (!lowDone && !medDone)
					LOG.info("BackgroundProcessing waiting for medium ("
							+ mediumPriorityService.getQueue().size()
							+ ") and low priority tasks to complete");
				else if (!medDone)
					LOG.info("BackgroundProcessing waiting for medium priority tasks ("
							+ mediumPriorityService.getQueue().size()
							+ ") to complete");
				else
					LOG.info("BackgroundProcessing waiting for low priority tasks to complete");

				rewaits--;
			}
		} catch (InterruptedException e) {
			LOG.info("", e);
		}
	}
}
