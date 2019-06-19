package org.scada_lts.danibeni.rt.dataSource.customSerial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataSource.DataSourceUtils;
import com.serotonin.mango.rt.dataSource.PollingDataSource;
import com.serotonin.web.i18n.LocalizableException;
import com.serotonin.web.i18n.LocalizableMessage;

import org.scada_lts.danibeni.vo.dataSource.customSerial.CustomSerialDataSourceVO;
import org.scada_lts.danibeni.vo.dataSource.customSerial.CustomSerialPointLocatorVO;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

public class CustomSerialDataSource extends PollingDataSource {

	private final Log LOG = LogFactory.getLog(CustomSerialDataSource.class);
	public static final int POINT_READ_EXCEPTION_EVENT = 1;
	public static final int DATA_SOURCE_EXCEPTION_EVENT = 2;
	private final CustomSerialDataSourceVO<?> vo;
	private Enumeration portList;
	private InputStream inSerialStream;
	private OutputStream outSerialStream;
	private SerialPort sPort;
	private int attempts_reconnect;

	/**
	 * Constructor for Custom Serial datasource runtime objects
	 * 
	 * @param vo
	 *            Custom Serial virtual object with properties, configuration
	 *            methods, etc
	 */
	public CustomSerialDataSource(CustomSerialDataSourceVO<?> vo) {
		super(vo);
		this.vo = vo;
		attempts_reconnect = 0;
		setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(),
				vo.isQuantize());

		portList = CommPortIdentifier.getPortIdentifiers();
		sPort = getPort(vo.getCommPortId());
		if (sPort != null) {
			configurePort(getsPort());
			try {
				/*
				 * After configure the serial port, the datasource sleeps for a
				 * time to give the serial device the opportunity to provide
				 * some data before the beginning of the polling process.
				 */
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				LOG.warn("Custom serial thread sleep process interrupted");
			}
		} else {
			LOG.error("Could not connect to serial port.");
			reconnect(vo.getRetries());
		}
	}

	@Override
	public void initialize() {
		super.initialize();

	}

	/**
	 * 
	 * @param port
	 * @return
	 */
	public boolean configurePort(SerialPort port) {

		boolean is_success = false;
		try {
			setInSerialStream(port.getInputStream());
			setOutSerialStream(port.getOutputStream());
			port.notifyOnDataAvailable(true);
			port.setSerialPortParams(vo.getBaudRate(), vo.getDataBits(),
					vo.getStopBits(), vo.getParity());
			is_success = true;
		} catch (Exception e) {
			LOG.error("Could not configure the serial port " + port.getName(),
					e);
			raiseEvent(
					DATA_SOURCE_EXCEPTION_EVENT,
					System.currentTimeMillis(),
					true,
					new LocalizableMessage("event.customserial", e.getMessage()));
		}
		return is_success;

	}

	/**
	 * Get the serial port object that handles the connection with the serial
	 * device used by this datasource
	 * 
	 * @param port
	 *            Identification name for the serial port
	 * 
	 * @return Serial port connection handler
	 */
	public SerialPort getPort(String port) {
		SerialPort serialPort = null;
		while (portList.hasMoreElements()) {
			CommPortIdentifier portId = (CommPortIdentifier) portList
					.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (portId.getName().equals(port)) {
					try {
						serialPort = (SerialPort) portId.open(this.getName(),
								10000);
						setsPort(serialPort);
					} catch (Exception e) {
						LOG.error("Error opening serial port " + port);
					}
				}
			}
		}

		return serialPort;
	}

	/**
	 * Get the output stream for the communication through the actual serial
	 * connection
	 * 
	 * @return Serial communication output stream
	 */
	public OutputStream getOutSerialStream() {
		return outSerialStream;
	}

	/**
	 * Set the output stream for the communication through the actual serial
	 * connection
	 * 
	 * @param outSerialStream
	 *            New serial output stream to establish
	 */
	public void setOutSerialStream(OutputStream outSerialStream) {
		this.outSerialStream = outSerialStream;
	}

	/**
	 * Get the input stream for the communication through the actual serial
	 * connection
	 * 
	 * @return Serial communication input stream
	 */
	public InputStream getInSerialStream() {
		return inSerialStream;
	}

	/**
	 * Set the input stream for the communication through the actual serial
	 * connection
	 * 
	 * @param inSerialStream
	 *            New serial input stream to set
	 */
	public void setInSerialStream(InputStream inSerialStream) {
		this.inSerialStream = inSerialStream;
	}

	/**
	 * Get the actual serial port handler for this datasoruce
	 * 
	 * @return Serial port handler
	 */
	public SerialPort getsPort() {
		return sPort;
	}

	/**
	 * Set the actual serial port handler for this datasource
	 * 
	 * @param sPort
	 *            New serial port handler to set
	 */
	public void setsPort(SerialPort sPort) {
		this.sPort = sPort;
	}

	/**
	 * Allows to retry the connection to the serial datasource until the
	 * connection is established or a maximum retries number is reached.
	 * 
	 * @param retries
	 *            Maximum number of retries to connect to the serial device.
	 * 
	 * @return true if a successful connection is established, false otherwise.
	 */
	private boolean reconnect(int retries) {

		boolean success_reconnect = false;
		try {
			while ((attempts_reconnect < retries) && !success_reconnect) {
				Thread.sleep(5000);
				portList = CommPortIdentifier.getPortIdentifiers();
				sPort = getPort(vo.getCommPortId());
				if (sPort != null) {
					configurePort(getsPort());
					beginPolling();
					success_reconnect = true;
				} else {
					attempts_reconnect++;
				}
			}
		} catch (Exception e) {
			LOG.warn("Could not reconnect to serial device", e);
			success_reconnect = false;
		}

		return success_reconnect;

	}

	/**
	 * Periodic polling process to communicate with the serial device configured
	 * for this custom serial datasource.
	 * 
	 * If no data is available at the beginning of the polling process, it is
	 * supposed that the serial device only sends data in response to a command.
	 * So the command configured in each data point is sent with the selected
	 * frame format and its corresponding response is parsed, depending on the
	 * corresponding regular expression.
	 * 
	 * Otherwise, if the serial device sends data periodically, these data are
	 * selected based on the configured frame format and directly parsed to the
	 * data points configured depending on the corresponding regular expression.
	 * 
	 * @param time
	 *            Clock time when the polling is done.
	 */
	@Override
	protected void doPoll(long time) {
		byte[] response_buffer = new byte[vo.getBufferSize()];
		CustomSerialPointLocatorVO dataPointVO;
		ArrayList<String> response_list = null;
		int response_size = 0;
		String command_str = null;
		String response_str = null;
		String last_data = null;
		boolean is_expected_resp = true;
		boolean is_data_available = false;
		int comm_errors = 0;

		try {
			if (sPort == null) {
				LOG.error("Could not connect to serial port. Maybe the serial port does not exist or not have the right permissions.");
				raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, time, true,
						new LocalizableMessage(
								"event.customSerial.noConnection",
								vo.getName(), vo.getCommPortId()));
			} else {
				if (getInSerialStream().available() > 0) {
					// If any data is received from serial device, the previous
					// event alarms are cleared
					is_data_available = true;
					resetErrorFlags(time);
					Arrays.fill(response_buffer, (byte) 0);

					/*
					 * If there are data available from the serial device
					 * connected to this datasource, these data are stored
					 */
					while (getInSerialStream().available() > 0) {
						response_size = getInSerialStream().read(
								response_buffer);
					}
					/*
					 * It is possible that there is more than one data string
					 * (serial device response data until reach the frame format
					 * condition) from the poll to the serial device. In that
					 * case, the last but one data string is taken, discarding
					 * the last data string that would be an empty string or an
					 * incomplete data string.
					 */
					response_list = splitResponse(new String(response_buffer));
					if (response_list.size() > 1) {
						response_str = getLastButOneData(response_list);
					} else {
						response_str = getFirstData(response_list);
					}

					// Insert the first data (serial device response data until
					// reach the frame format condition) into the corresponding
					// data point value

					for (DataPointRT dataPoint : dataPoints) {
						dataPointVO = dataPoint.getVO().getPointLocator();
						if (dataPointVO.getCommandNumber() == 0) {
							insertValueIntoDatapoint(dataPoint, response_str,
									time);
						}
					}
				}
				/*
				 * If no data from serial device, the command configured into
				 * each data point is sent and the serial device response is
				 * parsed to a point value depending on the regular expression
				 * configured into the corresponding data point
				 */

				for (DataPointRT dataPoint : dataPoints) {
					dataPointVO = dataPoint.getVO().getPointLocator();
					if (dataPointVO.getCommandNumber() > 0) {
						/*
						 * Build first command and process request to the serial
						 * device
						 */
						try {
							command_str = buildCommand(
									dataPointVO.getFirstCommand(),
									dataPointVO.getFirstCommandHexASCII(),
									vo.getCommandFormat());
							response_list = processRequest(command_str);
							is_data_available = true;
							resetErrorFlags(time);
							comm_errors = 0;
							/*
							 * If there is a second command, verify that the
							 * response from the first command is the expected
							 * one, and only send
							 */
							if (dataPointVO.getCommandNumber() > 1) {
								if (is_expected_resp = isExpectedResponse(
										getFirstData(response_list),
										dataPointVO.getFirstExpectedResponse(),
										dataPointVO
												.getFirstExpectedResponseHexASCII())) {

									command_str = buildCommand(
											dataPointVO.getSecondCommand(),
											dataPointVO
													.getSecondCommandHexASCII());
									response_list = processRequest(command_str);

									if (dataPointVO.getCommandNumber() > 2) {
										if (is_expected_resp = isExpectedResponse(
												getFirstData(response_list),
												dataPointVO
														.getSecondExpectedResponse(),
												dataPointVO
														.getSecondExpectedResponseHexASCII())) {
											command_str = buildCommand(
													dataPointVO
															.getThirdCommand(),
													dataPointVO
															.getThirdCommandHexASCII());
											response_list = processRequest(command_str);
										} else {
											LOG.warn("Response from serial device to command ["
													+ dataPointVO
															.getSecondCommand()
													+ " - "
													+ dataPointVO
															.getSecondCommandHexASCII()
													+ "] does not coincide with the expected one ["
													+ dataPointVO
															.getSecondExpectedResponse()
													+ "]");
											raiseEvent(
													POINT_READ_EXCEPTION_EVENT,
													time,
													true,
													new LocalizableMessage(
															"event.customSerial.noExpectedResponse",
															dataPointVO
																	.getSecondCommand(),
															dataPointVO
																	.getSecondCommandHexASCII(),
															dataPointVO
																	.getSecondExpectedResponse()));
										}
									}
								} else {
									LOG.warn("Response from serial device to command ["
											+ dataPointVO.getFirstCommand()
											+ " - "
											+ dataPointVO
													.getFirstCommandHexASCII()
											+ "] does not coincide with the expected one ["
											+ dataPointVO
													.getFirstExpectedResponse()
											+ "]");
									raiseEvent(
											POINT_READ_EXCEPTION_EVENT,
											time,
											true,
											new LocalizableMessage(
													"event.customSerial.noExpectedResponse",
													dataPointVO
															.getFirstCommand(),
													dataPointVO
															.getFirstCommandHexASCII(),
													dataPointVO
															.getFirstExpectedResponse()));
								}
							}

							/*
							 * Insert the first data (serial device response
							 * data until reach the format condition) into the
							 * corresponding data point value
							 */
							if (is_data_available) {
								if (is_expected_resp) {
									try {
										insertValueIntoDatapoint(dataPoint,
												getFirstData(response_list),
												time);
										returnToNormal(
												POINT_READ_EXCEPTION_EVENT,
												time);

									} catch (LocalizableException le) {
										LOG.warn("The value ["
												+ getFirstData(response_list)
												+ "] could NOT be inserted into the data point");
										raiseEvent(POINT_READ_EXCEPTION_EVENT,
												time, true,
												le.getLocalizableMessage());
									}
								}
							} else {
								LOG.warn("No data available from serial device. Data is not received periodically from device or the device does not response to commands.");
								raiseEvent(
										DATA_SOURCE_EXCEPTION_EVENT,
										time,
										true,
										new LocalizableMessage(
												"event.customSerial.noDataAvailable"));
							}
						} catch (LocalizableException le) {
							// The LOG always stores the LocalizableException
							// messages for
							// debugging purposes.
							LOG.warn("Problem while a request to the data source was processed. Details: "
									+ le.getLocalizableMessage()
											.getLocalizedMessage(
													Common.getBundle()));
							raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, time, true,
									le.getLocalizableMessage());
							comm_errors++;
							if ((comm_errors >= CustomSerialDataSourceVO.MAX_COMM_ERRORS)
									|| (comm_errors == dataPoints.size())) {
								throw new IOException();
							}
						}
					}
				}

			}

		} catch (IOException io) {
			LOG.error("Communication with serial device lost. Trying to reconnect...");
			raiseEvent(
					DATA_SOURCE_EXCEPTION_EVENT,
					time,
					true,
					new LocalizableMessage(
							"event.customSerial.communicationLost", vo
									.getName(), io.getMessage()));
			getsPort().close();
			reconnect(vo.getRetries());

		} catch (LocalizableException le) {
			LOG.warn("Could not update data point. Probably, no coincidence between received data and any of the regular expressions was found.");
			raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, time, true,
					le.getLocalizableMessage());
		} catch (Exception e) {
			LOG.error("Exception while updating data from serial device. Details: "
					+ e.getMessage());
			raiseEvent(
					DATA_SOURCE_EXCEPTION_EVENT,
					time,
					true,
					new LocalizableMessage("event.customSerial.exception", vo
							.getName(), e.getMessage()));
		}
	}

	private String getCommandEndFormat(int option) {
		String option_str = new String();

		switch (option) {
		case 0:
			option_str = "";
			break;
		case 1:
			option_str = "\r\n";
			break;
		case 2:
			option_str = "\r";
			break;
		case 3:
			option_str = "\n";
			break;
		default:
			option_str = "";
			break;

		}
		return option_str;
	}

	/**
	 * @brief Configure command to send with an understandable format for the
	 *        serial device.
	 * 
	 *        The possible values are: -The command without any change. -The
	 *        command ended with carry return (CR) and line feed (LF) characters
	 *        -The command ended with carry return (CR) character -The command
	 *        ended with line feed (LF) character -The command represented by
	 *        its hexadecimal value.
	 * 
	 * @param command
	 *            Command introduced by the user into the datasource
	 * @param option
	 *            Possible options to build a formatted command.
	 * @return The formatted command to send to the serial device
	 */
	private String buildCommand(String command_type, String hex_or_char,
			int option) {
		String command_to_send;

		command_to_send = buildCommand(command_type, hex_or_char);

		return command_to_send + getCommandEndFormat(option);

	}

	private String buildCommand(String command_type, String hex_or_char) {
		String command_to_send;
		if (command_type.equals(CustomSerialDataSourceVO.HEX_STRING)
				|| command_type.equals(CustomSerialDataSourceVO.ASCII_STRING)) {
			if (command_type.equals(CustomSerialDataSourceVO.HEX_STRING)) {
				command_to_send = convertStringToHex(hex_or_char);
			} else {
				command_to_send = hex_or_char;
			}
		} else {
			if (command_type.equals(CustomSerialDataSourceVO.STX_STRING)) {
				command_to_send = Character
						.toString(CustomSerialDataSourceVO.STX_HEX);
			} else if (command_type.equals(CustomSerialDataSourceVO.ETX_STRING)) {
				command_to_send = Character
						.toString(CustomSerialDataSourceVO.ETX_HEX);
			} else if (command_type.equals(CustomSerialDataSourceVO.ENQ_STRING)) {
				command_to_send = Character
						.toString(CustomSerialDataSourceVO.ENQ_HEX);
			} else if (command_type.equals(CustomSerialDataSourceVO.ACK_STRING)) {
				command_to_send = Character
						.toString(CustomSerialDataSourceVO.ACK_HEX);
			} else {
				command_to_send = "";
			}
		}
		return command_to_send;
	}

	private ArrayList<String> processRequest(String command_str)
			throws Exception {

		byte[] response_buffer = new byte[vo.getBufferSize()];
		ArrayList<String> response_list = null;
		int response_size = 0;

		if (!command_str.equals(null) && !command_str.equals("")) {

			getOutSerialStream().write(command_str.getBytes());
			/*
			 * Some serial devices need some time to generate an appropriate
			 * response to the command. This wait time is configured by the
			 * datasource
			 */

			Thread.sleep(vo.getTimeout());
			if (getInSerialStream().available() > 0) {
				Arrays.fill(response_buffer, (byte) 0);
				while (getInSerialStream().available() > 0) {
					response_size = getInSerialStream().read(response_buffer);
				}
				/*
				 * Split the response from the serial device based on the frame
				 * format response configured by the datasource
				 */
				response_list = splitResponse(new String(response_buffer));

			} else {
				/*
				 * If the serial device does not respond to the command sent, an
				 * alert is raised
				 */
				LOG.warn("No response from serial device");
				throw new LocalizableException(new LocalizableMessage(
						"event.customSerial.noResponse", vo.getCommPortId(),
						command_str));
			}

		} else {
			// Datasource configured to receive data from serial
			// device, but no data has been received yet
			LOG.warn("No data available from serial device");
			throw new LocalizableException(new LocalizableMessage(
					"event.customSerial.noDataAvailable", vo.getName()));

		}

		return response_list;
	}

	private ArrayList<String> splitResponse(String buffer_str) throws Exception {
		ArrayList<String> response_list = null;

		if (buffer_str.length() > 0) {
			/*
			 * Check if the serial device command and response shares the same
			 * frame format
			 */
			if (vo.isSameFormat()) {
				response_list = new ArrayList<String>(Arrays.asList(buffer_str
						.split(getCommandEndFormat(vo.getCommandFormat()))));
			} else {
				/*
				 * Stop mode used to select only the received data until a
				 * determined condition is reached, if the command and response
				 * do not share the same frame format.
				 */
				switch (vo.getStopMode()) {
				case 0:
					if (vo.getnChar() > 0) {
						response_list = new ArrayList<String>();
						response_list.add(buffer_str.substring(0,
								vo.getnChar() - 1));
						if (buffer_str.length() > vo.getnChar()) {
							response_list.add(buffer_str.substring(
									vo.getnChar(), buffer_str.length() - 1));
						}
					}
					break;
				case 1:
					switch (vo.getCharStopMode()) {
					case 0:
						if (!vo.getCharX().equals(null)
								&& !vo.getCharX().equals("")) {
							response_list = new ArrayList<String>(
									Arrays.asList(buffer_str.split(vo
											.getCharX())));
						}
						break;
					case 1:
						if (!vo.getHexValue().equals(null)
								&& !vo.getHexValue().equals("")) {
							StringBuilder hexStr = new StringBuilder();
							hexStr.append((char) Integer.parseInt(
									vo.getHexValue(), 16));
							response_list = new ArrayList<String>(
									Arrays.asList(buffer_str.split(hexStr
											.toString())));
						}
						break;
					default:
						response_list = null;

					}
					break;
				case 2:
					/*
					 * Option to stop receiving data from serial device when a
					 * configured timeout elapses. NOT IMPLEMENTED YET.
					 */
					response_list = new ArrayList<String>();
					response_list.add(buffer_str);
					break;
				default:
					response_list = null;
				}
			}
		}
		if (response_list.equals(null)) {
			throw new Exception("Response data could not splitted");
		}
		return response_list;
	}

	private boolean isExpectedResponse(String response, String response_type,
			String hex_or_char) {
		boolean is_expected = false;
		String hex_str = "";
		String hex_response = "";

		if (response_type.equals(CustomSerialDataSourceVO.HEX_STRING)
				|| response_type.equals(CustomSerialDataSourceVO.ASCII_STRING)) {
			if (response_type.equals(CustomSerialDataSourceVO.HEX_STRING)) {
				hex_str = convertHexToString(hex_or_char);
				if (response.equals(hex_str)) {
					is_expected = true;
				}
			} else {
				if (response.equals(hex_or_char)) {
					is_expected = true;
				}
			}
		} else {
			hex_response = convertStringToHex(response);
			for (int i = 0; i < CustomSerialDataSourceVO.NON_PRINTABLE_CHARS.length; i++) {
				if (response_type
						.equals(CustomSerialDataSourceVO.NON_PRINTABLE_CHARS[i]
								.getAsciiStr())
						&& Integer.parseInt(hex_response) == CustomSerialDataSourceVO.NON_PRINTABLE_CHARS[i]
								.getAsciiHex()) {
					is_expected = true;
					break;
				}
			}
		}
		return is_expected;
	}

	private long getTimestamp(CustomSerialPointLocatorVO point, String arquivo)
			throws Exception {
		long timestamp = new Date().getTime();
		String dataFormat = point.getTimestampFormat();
		String tsRegex = point.getTimestampRegex();
		Pattern tsPattern = Pattern.compile(tsRegex);
		Matcher tsMatcher = tsPattern.matcher(arquivo);

		boolean found = false;
		while (tsMatcher.find()) {
			found = true;
			String tsValue = tsMatcher.group();
			timestamp = new SimpleDateFormat(dataFormat).parse(tsValue)
					.getTime();
		}

		if (!found) {
			throw new Exception("Timestamp string not found (regex: " + tsRegex
					+ ")");
		}

		return timestamp;
	}

	private void insertValueIntoDatapoint(DataPointRT data_point,
			String data_values_str, long time) throws LocalizableException,
			Exception {
		MangoValue value = null;
		long timestamp = 0;
		CustomSerialPointLocatorVO dataPointVO;

		if (!data_values_str.isEmpty()) {

			dataPointVO = data_point.getVO().getPointLocator();
			try {
				Pattern valuePattern = Pattern.compile(dataPointVO
						.getValueRegex());
				value = DataSourceUtils.getValue(valuePattern, data_values_str,
						dataPointVO.getDataType(), "false", data_point.getVO()
								.getTextRenderer(), null, data_point.getVO()
								.getName());
			} catch (Exception e) {
				throw new LocalizableException(new LocalizableMessage(
						"event.customSerial.unableUpdateDataPoint",
						data_values_str));
			}
			timestamp = time;
			if (dataPointVO.isCustomTimestamp()) {

				timestamp = getTimestamp(dataPointVO, data_values_str);
			}
			data_point.updatePointValue(new PointValueTime(value, timestamp));

		}
	}

	private void resetErrorFlags(long time) {
		returnToNormal(DATA_SOURCE_EXCEPTION_EVENT, time);
	}

	private String getFirstData(ArrayList<String> data_list) {
		return data_list.get(0);
	}

	private String getLastButOneData(ArrayList<String> data_list) {
		return data_list.get(data_list.size() - 2);
	}

	@Override
	public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime,
			SetPointSource source) {

	}

	public String convertStringToHex(String str) {

		char[] chars = str.toCharArray();

		StringBuffer hex = new StringBuffer();
		for (int i = 0; i < chars.length; i++) {
			hex.append(Integer.toHexString((int) chars[i]));
		}

		return hex.toString();
	}

	public String convertHexToString(String hex) {

		StringBuilder sb = new StringBuilder();
		StringBuilder temp = new StringBuilder();

		// Hex value split into two characters 49, 20, 4c...
		for (int i = 0; i < hex.length() - 1; i += 2) {

			// grab the hex in pairs
			String output = hex.substring(i, (i + 2));
			// convert hex to decimal
			int decimal = Integer.parseInt(output, 16);
			// convert the decimal to character
			sb.append((char) decimal);

			temp.append(decimal);
		}

		return sb.toString();
	}

	@Override
	public void terminate() {
		super.terminate();
		if (sPort != null) {
			getsPort().close();
		}
	}

}

