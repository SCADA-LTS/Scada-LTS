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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.db.dao.DataSourceDao;
import com.serotonin.mango.db.dao.PublisherDao;
import com.serotonin.mango.db.dao.SystemSettingsDao;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.rt.event.type.SystemEventType;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.publish.PublisherVO;
import com.serotonin.timer.FixedRateTrigger;
import com.serotonin.timer.TimerTask;
import com.serotonin.util.queue.ByteQueue;
import com.serotonin.web.http.HttpUtils;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 * 
 */
public class VersionCheck extends TimerTask {
	private static final long TIMEOUT = 1000 * 60 * 60 * 24 * 2; // Run every
																	// other
																	// day.
	private static final String INSTANCE_ID_FILE = "WEB-INF/instance.txt";

	private static VersionCheck instance;
	private static String instanceId;

	/**
	 * This method will set up the version checking job. It assumes that the
	 * corresponding system setting for running this job is true.
	 */
	public static void start() {
		synchronized (INSTANCE_ID_FILE) {
			stop();
			instance = new VersionCheck();
			Common.timer.schedule(instance);
		}
	}

	public static void stop() {
		synchronized (INSTANCE_ID_FILE) {
			if (instance != null) {
				instance.cancel();
				instance = null;
			}
		}
	}

	private VersionCheck() {
		super(new FixedRateTrigger(100000, TIMEOUT));
	}

	public static String getInstanceId() {
		if (instanceId == null)
			instanceId = calcMachineId();
		return instanceId;
	}

	@Override
	public void run(long fireTime) {
		try {
			String notifLevel = SystemSettingsDao
					.getValue(SystemSettingsDao.NEW_VERSION_NOTIFICATION_LEVEL);
			newVersionCheck(fireTime, notifLevel);
		} catch (SocketTimeoutException e) {
			// Ignore
		} catch (Exception e) {
			SystemEventType.raiseEvent(getEventType(), fireTime, true,
					new LocalizableMessage("event.version.error", e.getClass()
							.getName(), e.getMessage()));
		}
	}

	public static LocalizableMessage newVersionCheck(String notifLevel)
			throws Exception {
		return newVersionCheck(System.currentTimeMillis(), notifLevel);
	}

	private static SystemEventType getEventType() {
		return new SystemEventType(SystemEventType.TYPE_SYSTEM_STARTUP, 0,
				EventType.DuplicateHandling.IGNORE_SAME_MESSAGE);
		// return new SystemEventType(SystemEventType.TYPE_VERSION_CHECK, 0,
		// EventType.DuplicateHandling.IGNORE_SAME_MESSAGE);
	}

	private static LocalizableMessage newVersionCheck(long fireTime,
			String notifLevel) throws Exception {
		String result = newVersionCheckImpl(notifLevel);
		if (result == null) {
			// If the version matches, clear any outstanding event.
			SystemEventType.returnToNormal(getEventType(), fireTime);
			return new LocalizableMessage("event.version.uptodate");
		}

		// If the version doesn't match this version, raise an event.
		LocalizableMessage message = new LocalizableMessage(
				"event.version.available", result);
		SystemEventType.raiseEvent(getEventType(), fireTime, true, message);
		return message;
	}

	private static String newVersionCheckImpl(String notifLevel)
			throws Exception {
		HttpClient httpClient = Common.getHttpClient();

		PostMethod postMethod = new PostMethod(
				Common.getGroveUrl(Common.GroveServlets.VERSION_CHECK));

		postMethod.addParameter("instanceId", getInstanceId());
		postMethod.addParameter("instanceName", SystemSettingsDao
				.getValue(SystemSettingsDao.INSTANCE_DESCRIPTION));
		try {
			postMethod.addParameter("instanceIp", InetAddress.getLocalHost()
					.getHostAddress());
		} catch (UnknownHostException e) {
			postMethod.addParameter("instanceIp", "unknown");
		}

		postMethod.addParameter("instanceVersion", Common.getVersion());

		StringBuilder datasourceTypes = new StringBuilder();
		DataPointDao dataPointDao = new DataPointDao();
		for (DataSourceVO<?> config : new DataSourceDao().getDataSources()) {
			if (config.isEnabled()) {
				int points = 0;
				for (DataPointVO point : dataPointDao.getDataPoints(
						config.getId(), null)) {
					if (point.isEnabled())
						points++;
				}

				if (datasourceTypes.length() > 0)
					datasourceTypes.append(',');
				datasourceTypes.append(config.getType().getId()).append(':')
						.append(points);
			}
		}
		postMethod.addParameter("datasourceTypes", datasourceTypes.toString());

		StringBuilder publisherTypes = new StringBuilder();
		for (PublisherVO<?> config : new PublisherDao().getPublishers()) {
			if (config.isEnabled()) {
				if (publisherTypes.length() > 0)
					publisherTypes.append(',');
				publisherTypes.append(config.getType().getId()).append(':')
						.append(config.getPoints().size());
			}
		}
		postMethod.addParameter("publisherTypes", publisherTypes.toString());

		int responseCode = httpClient.executeMethod(postMethod);
		if (responseCode != HttpStatus.SC_OK)
			throw new HttpException("Invalid response code: " + responseCode);

		Header devHeader = postMethod.getResponseHeader("Mango-dev");
		if (devHeader != null) {
			String devVersion = devHeader.getValue();
			String stage = devVersion.substring(devVersion.length() - 1);
			devVersion = devVersion.substring(0, devVersion.length() - 1);

			// There is a new version development version. Check if we're
			// interested.
			if (Common.getVersion().equals(devVersion))
				// We already have it. Never mind.
				return null;

			// Beta?
			if (SystemSettingsDao.NOTIFICATION_LEVEL_BETA.equals(stage)
					&& SystemSettingsDao.NOTIFICATION_LEVEL_BETA
							.equals(notifLevel))
				return devVersion + " beta";

			// Release candidate?
			if (SystemSettingsDao.NOTIFICATION_LEVEL_RC.equals(stage)
					&& (SystemSettingsDao.NOTIFICATION_LEVEL_BETA
							.equals(notifLevel) || SystemSettingsDao.NOTIFICATION_LEVEL_RC
							.equals(notifLevel)))
				return devVersion + " release candidate";
		}

		// Either there is no dev version available or we're not interested in
		// it. Check the stable version
		String stableVersion = HttpUtils.readResponseBody(postMethod);

		if (Common.getVersion().equals(stableVersion))
			return null;

		return stableVersion;
	}

	private static String calcMachineId() {
		List<NI> nis = new ArrayList<NI>();

		try {
			Enumeration<NetworkInterface> eni = NetworkInterface
					.getNetworkInterfaces();
			while (eni.hasMoreElements()) {
				NetworkInterface netint = eni.nextElement();
				NI ni = new NI();
				ni.name = netint.getName();
				try {
					ni.hwAddress = netint.getHardwareAddress();
				} catch (SocketException e) {
					// ignore this too
				}
				if (ni.name != null && ni.hwAddress != null)
					// Should be for real.
					nis.add(ni);
			}
		} catch (SocketException e) {
			// ignore
		}

		if (nis.isEmpty())
			return null;

		// Sort the NIs just to make sure we always add them in the same order.
		Collections.sort(nis, new Comparator<NI>() {
			@Override
			public int compare(NI ni1, NI ni2) {
				return ni1.name.compareTo(ni2.name);
			}
		});

		ByteQueue queue = new ByteQueue();
		for (NI ni : nis) {
			queue.push(ni.name.getBytes(Common.UTF8_CS));
			queue.push(ni.hwAddress);
		}

		UUID uuid = UUID.nameUUIDFromBytes(queue.popAll());
		return uuid.toString();
	}

	static class NI {
		String name;
		byte[] hwAddress;
	}
}
