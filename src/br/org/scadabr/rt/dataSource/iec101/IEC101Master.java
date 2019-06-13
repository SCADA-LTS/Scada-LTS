package br.org.scadabr.rt.dataSource.iec101;

import java.util.List;

import br.org.scadabr.protocol.iec101.session.CompositeMessage;
import br.org.scadabr.protocol.iec101.session.IECConfig;
import br.org.scadabr.protocol.iec101.session.IECUser;
import br.org.scadabr.protocol.iec101.session.database.DataElement;
import br.org.scadabr.protocol.iec101.session.database.Database;

public class IEC101Master {
	private IECUser user;
	private IECConfig configuration;
	private int relativePollingPeriod = 10;
	private int pollingCount = 0;
	private int timeoutCount = 0;
	private int timeoutsToReconnect = 3;

	public static final int SINGLE_POINT_INFORMATION = Database.SINGLE_POINT_INFORMATION;
	public static final int DOUBLE_POINT_INFORMATION = Database.DOUBLE_POINT_INFORMATION;
	public static final int STEP_POSITION_INFORMATION = Database.STEP_POSITION;
	public static final int NORMALIZED_MEASURE = Database.NORMALIZED_MEASURE;

	public static final int SELECT_AND_EXECUTE = 1;
	public static final int EXECUTE_ONLY = 1;
	public static final int DEFAULT = 0;
	public static final int SHORT_PULSE = 1;
	public static final int LONG_PULSE = 2;
	public static final int PERSISTENT = 3;

	public void init(int relativePollingPeriod) throws Exception {
		user = new IECUser(configuration);
		int response = user.init();
		if (response != CompositeMessage.SUCCESS)
			throw new Exception("Init failed!");
	}

	private boolean reconnecting = false;

	public void doPoll() throws Exception {
		int response;
		if (reconnecting) {
			timeoutCount = 0;
			try {
				response = user.init();
				if (response == CompositeMessage.SUCCESS) {
					reconnecting = false;
				} else {
					terminate();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			if (reconnectNeeded()) {
				reconnecting = true;
				terminate();
				throw new Exception("Link failed. Trying to reconnect.");
			} else {
				if (pollingCount == 0) {
					response = user.sendGeneralInterrogation();
					pollingCount++;
				} else {
					response = user.class2Request();
					pollingCount++;
					if (pollingCount > relativePollingPeriod)
						pollingCount = 0;
				}

				if (response == CompositeMessage.TIMED_OUT) {
					timeoutCount++;
				}

				else
					timeoutCount = 0;
			}
		}
		Thread.sleep(100);
	}

	private boolean reconnectNeeded() {
		if (timeoutCount >= timeoutsToReconnect) {
			return true;
		}
		return false;
	}

	public List<DataElement> read(int address, int typeIdentification) {
		return user.getDatabase().read(address, typeIdentification);
	}

	public void singleCommand(int ioa, boolean select, byte qualifier,
			boolean value) throws Exception {
		user.singleCommand(ioa, select, qualifier, value);

	}

	public void doubleCommand(int ioa, boolean select, byte qualifier,
			boolean value) throws Exception {
		user.doubleCommand(ioa, select, qualifier, value);
	}

	public void setPointCommand(int ioa, boolean select, byte qualifier,
			int value) throws Exception {
		user.setPointCommand(ioa, select, qualifier, value);
	}

	public void terminate() throws Exception {
		user.stop();
	}

	public IECConfig getConfiguration() {
		return configuration;
	}

	public void setConfiguration(IECConfig configuration) {
		this.configuration = configuration;
	}

}
