package br.org.scadabr.rt.dataSource.dnp3;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.org.scadabr.dnp34j.master.common.AppFeatures;
import br.org.scadabr.dnp34j.master.session.DNPUser;
import br.org.scadabr.dnp34j.master.session.config.DNPConfig;
import br.org.scadabr.dnp34j.master.session.config.EthernetParameters;
import br.org.scadabr.dnp34j.master.session.config.SerialParameters;
import br.org.scadabr.dnp34j.master.session.database.DataElement;

public class DNP3Master {
	private Log log = LogFactory.getLog(DNP3Master.class);

	private DNPUser user;
	private int relativePollingPeriod = 10;
	private int pollingCount = 0;
	private volatile int timeoutCount = 0;
	private final int timeoutsToReconnect = 3;

	public void initEthernet(int sourceAddress, int slaveAddress, String host,
			int port, int relativePollingPeriod) throws Exception {
		this.relativePollingPeriod = relativePollingPeriod;
		EthernetParameters parameters = new EthernetParameters(host, port);
		DNPConfig config = new DNPConfig(parameters, sourceAddress,
				slaveAddress);
		user = new DNPUser(config);

		user.init();
	}

	public void initSerial(int sourceAddress, int slaveAddress, String com,
			int baudrate, int relativePollingPeriod) throws Exception {
		this.relativePollingPeriod = relativePollingPeriod;
		SerialParameters parameters = new SerialParameters(com, baudrate);
		DNPConfig config = new DNPConfig(parameters, sourceAddress,
				slaveAddress);
		user = new DNPUser(config);
		try {
			user.init();
		} catch (Error e) {
			log.fatal(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private boolean reconnecting = false;

	public void doPoll() throws Exception {
		if (reconnecting) {
			log.debug("[DNP3Master] Trying to reconnect...");
			timeoutCount = 0;
			try {
				try {
					user.init();
					reconnecting = false;
					log.debug("[DNP3Master] Reconnected!");
				} catch (Exception | Error e) {
					log.warn(e.getMessage(), e);
					terminate();
				}
			} catch (Exception e) {
				throw e;
			}
		} else {
			if (reconnectNeeded()) {
				reconnecting = true;
				log.debug("[DNP3Master] Conexão falhou. Terminar Conexão.");
				terminate();
				log.debug("[DNP3Master] Conexão terminada.");
				throw new Exception("[DNP3Master] Poll failed!");
			} else {
				try {
					log.debug("[DNP3Master] Poll " + pollingCount + " / "
							+ relativePollingPeriod);
					if (pollingCount == 0) {
						user.sendSynch(user.buildReadStaticDataMsg());
						pollingCount++;
					} else {
						user.sendSynch(user.buildReadEventDataMsg());
						pollingCount++;
						if (pollingCount > relativePollingPeriod)
							pollingCount = 0;
					}
					timeoutCount = 0;
				} catch (Exception e) {
					log.debug("[DNP3Master] Poll failed! (Error: "
							+ e.getMessage() + ")");
					timeoutCount++;
				}

			}
		}

		Thread.sleep(150);
	}

	private boolean reconnectNeeded() {
		if (timeoutCount >= timeoutsToReconnect) {
			return true;
		}
		return false;
	}

	public List<DataElement> read(int group, int index) {
		return user.getDatabase().read(index, group);
	}

	public void terminate() throws Exception {
		try {
			user.stop();
		} catch (Exception ex) {
			if(ex instanceof NullPointerException)
				throw new Exception(this.getClass().getSimpleName() + " error terminate. It probably failed to initialize." , ex);
			throw ex;
		}
	}

	public void sendAnalogCommand(int index, int value) throws Exception {
		user.sendSynch(user.buildAnalogControlCommand(
				AppFeatures.DIRECT_OPERATE, index, value));
	}

	public void controlCommand(String command, int index,
			int defaultControlMode, int defaultTimeOn, int defaultTimeOff)
			throws Exception {
		command = command.replaceAll(" ", "").toLowerCase();

		byte controlCode = 0x00;
		int timeOn = defaultTimeOn;
		int timeOff = defaultTimeOff;

		if (isValidDefaultCommand(command)) {
			controlCode = getControlCode(getBooleanValue(command),
					defaultControlMode);
		} else if (verifyControlCommandString(command)) {
			controlCode = getControlCode(command);
		} else if (isValidComplexCommand(command)) {
			String[] parameters = command.split(",");
			controlCode = getControlCode(parameters[0]);
			timeOn = Integer.parseInt(parameters[1]);
			timeOff = Integer.parseInt(parameters[2]);
		} else {
			throw new Exception(
					"Invalid DNP3 Write Command! Valid Commands: 1/0, true/false, pon/poff, lon/loff,close/trip...");
		}

		user.sendSynch(user
				.buildBinaryControlCommand(AppFeatures.DIRECT_OPERATE, index,
						controlCode, timeOn, timeOff));
	}

	public static final String CLOSE = "close";
	public static final byte CLOSE_CODE = 64;
	public static final String TRIP = "trip";
	public static final byte TRIP_CODE = (byte) 128;
	public static final String PULSE_ON = "pon";
	public static final byte PULSE_ON_CODE = 01;
	public static final String PULSE_OFF = "poff";
	public static final byte PULSE_OFF_CODE = 02;
	public static final String LATCH_ON = "lon";
	public static final byte LATCH_ON_CODE = 03;
	public static final String LATCH_OFF = "loff";
	public static final byte LATCH_OFF_CODE = 04;

	private byte getControlCode(String command) {
		if (command.equals(CLOSE)) {
			return CLOSE_CODE;
		} else if (command.equals(TRIP)) {
			return TRIP_CODE;
		} else if (command.equals(PULSE_ON)) {
			return (byte) PULSE_ON_CODE;
		} else if (command.equals(PULSE_OFF)) {
			return (byte) PULSE_OFF_CODE;
		} else if (command.equals(LATCH_ON)) {
			return (byte) LATCH_ON_CODE;
		} else if (command.equals(LATCH_OFF)) {
			return (byte) LATCH_OFF_CODE;
		}
		return 0x00;
	}

	public static final int CLOSE_TRIP = 1;
	public static final int PULSE = 2;
	public static final int LATCH = 3;

	private byte getControlCode(boolean command, int controlMode) {
		if (controlMode == CLOSE_TRIP) {
			if (command)
				return CLOSE_CODE;
			else
				return TRIP_CODE;
		} else if (controlMode == PULSE) {
			if (command)
				return PULSE_ON_CODE;
			else
				return PULSE_OFF_CODE;
		} else if (controlMode == LATCH) {
			if (command)
				return LATCH_ON_CODE;
			else
				return LATCH_OFF_CODE;
		}
		return 0x00;
	}

	private boolean getBooleanValue(String command) {
		if (command.equals("1") || command.equals("true"))
			return true;
		if (command.equals("0") || command.equals("false"))
			return false;
		return false;
	}

	private boolean isValidDefaultCommand(String command) {
		if (command.equals("1") || command.equals("0"))
			return true;
		if (command.equals("true") || command.equals("false"))
			return true;
		return false;
	}

	private boolean isValidComplexCommand(String command) {
		String[] parameters = command.split(",");
		if (verifyParameters(parameters))
			return true;
		return false;
	}

	private boolean verifyParameters(String[] parameters) {
		if (parameters.length != 3)
			return false;
		if (!verifyControlCommandString(parameters[0]))
			return false;
		if (!verifyTimeString(parameters[1]))
			return false;
		if (!verifyTimeString(parameters[2]))
			return false;
		return true;
	}

	private boolean verifyControlCommandString(String controlCommand) {
		if (controlCommand.equals(LATCH_OFF) || controlCommand.equals(LATCH_ON))
			return true;
		if (controlCommand.equals(PULSE_OFF) || controlCommand.equals(PULSE_ON))
			return true;
		if (controlCommand.equals(CLOSE) || controlCommand.equals(TRIP))
			return true;
		return false;
	}

	private boolean verifyTimeString(String time) {
		try {
			Integer.parseInt(time);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
