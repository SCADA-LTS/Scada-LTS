package org.scada_lts.danibeni.rt.dataSource.socketComm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
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

import org.scada_lts.danibeni.vo.dataSource.socketComm.SocketCommDataSourceVO;
import org.scada_lts.danibeni.vo.dataSource.socketComm.SocketCommPointLocatorVO;

/**
 * 
 * @author danibeni (DBH)
 * 
 */

public class SocketCommDataSource extends PollingDataSource {

	private final Log LOG = LogFactory.getLog(SocketCommDataSource.class);
	public static final int POINT_READ_EXCEPTION_EVENT = 1;
	public static final int DATA_SOURCE_EXCEPTION_EVENT = 2;
	private final SocketCommDataSourceVO<?> vo;
	private PrintWriter out_stream;
	private BufferedReader in_stream;
	private Socket socket;
	private int attempts_reconnect;

	/**
	 * Constructor for socket communication datasource runtime objects
	 * 
	 * @param vo
	 *            Socket communication virtual object with properties,
	 *            configuration methods, etc
	 */
	public SocketCommDataSource(SocketCommDataSourceVO<?> vo) {
		super(vo);
		this.vo = vo;
		attempts_reconnect = 0;
		setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(),
				vo.isOneByOne());
	}

	/**
	 * @brief Initialize the data source by opening a new connection to the
	 *        server
	 * 
	 */
	@Override
	public void initialize() {
		
	}

	/**
	 * Periodic polling process to communicate with the server configured for
	 * this socket communication datasource.
	 * 
	 * If no data is available at the beginning of the polling process, it is
	 * supposed that the server only sends data in response to a command. So the
	 * command configured in each data point is sent with the selected frame
	 * format and its corresponding response is parsed, depending on the
	 * corresponding regular expression.
	 * 
	 * Otherwise, if the server sends data periodically, these data are selected
	 * based on the configured frame format and directly parsed to the data
	 * points configured depending on the corresponding regular expression.
	 * 
	 * @param time Clock time when the polling is done.
	 */
	@Override
	synchronized protected void doPoll(long time) {
		SocketCommPointLocatorVO dataPointVO;
		String response = "";
		String command_str = null;
		String point_name = null;
		boolean is_expected_resp = true;
		boolean is_data_available = false;
		int comm_errors = 0;

		try {
			// DBH: Open new socket to server data source
			openConnection();
			try {
				// DBH: Get direct data from server, if any (no command
				// sent).
				response = getServerResponse("");
				is_data_available = true;

				/*
				 * DBH: Insert the first data (server response data until reach
				 * the frame format condition) into the corresponding data point
				 * value.
				 */
				for (DataPointRT dataPoint : dataPoints) {
					dataPointVO = dataPoint.getVO().getPointLocator();
					// DBH: When direct data is provided from server, only the
					// data points with no command configured are updated.
					if (dataPointVO.getCommandNumber() == 0) {
						insertValueIntoDatapoint(dataPoint, response, time);
					}
				}

			} catch (LocalizableException le) {
				// DBH: If the server does not provide data without a request, a
				// programmed timeout arises.
				LOG.info("The server is not sending data directly, or sent data do not contain stop characters (incomplete reponse).");
			}

			/*
			 * DBH: After receiving direct data from the server, all data points
			 * with at least one command are processed. The commands are built,
			 * sent and the responses from the server are compared to the
			 * expected ones.
			 */
			for (DataPointRT dataPoint : dataPoints) {
				dataPointVO = dataPoint.getVO().getPointLocator();
				point_name = dataPoint.getVO().getName();
				if (dataPointVO.getCommandNumber() > 0) {
					// DBH: Build first command and process request to the
					// server.

					
						command_str = buildCommand(
								dataPointVO.getFirstCommand(),
								dataPointVO.getFirstCommandHexASCII(),
								vo.getCommandFormat());
						response = processRequest(command_str, point_name);
						is_data_available = true;
						comm_errors = 0;
						/*
						 * DBH: If there is a second command, verify that the
						 * response from the first command is the expected one,
						 * and only send the second command if the response to
						 * the first command is the expected one.
						 */

						if (dataPointVO.getCommandNumber() > 1) {
							if (is_expected_resp = isExpectedResponse(response,
									dataPointVO.getFirstExpectedResponse(),
									dataPointVO
											.getFirstExpectedResponseHexASCII())) {

								command_str = buildCommand(
										dataPointVO.getSecondCommand(),
										dataPointVO.getSecondCommandHexASCII());
								response = processRequest(command_str,
										point_name);
								/*
								 * DBH: If the data point is configured with a
								 * third command, verify that the response from
								 * the second command is the expected one, and
								 * only send the third command if the response
								 * to the second command is the expected one.
								 */

								if (dataPointVO.getCommandNumber() > 2) {
									if (is_expected_resp = isExpectedResponse(
											response,
											dataPointVO
													.getSecondExpectedResponse(),
											dataPointVO
													.getSecondExpectedResponseHexASCII())) {
										command_str = buildCommand(
												dataPointVO.getThirdCommand(),
												dataPointVO
														.getThirdCommandHexASCII());
										response = processRequest(command_str,
												point_name);
									} else {
										LOG.warn("Response from server to command ["
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
														"event.socketComm.noExpectedResponse",
														dataPointVO
																.getSecondCommand(),
														dataPointVO
																.getSecondCommandHexASCII(),
														dataPointVO
																.getSecondExpectedResponse()));
									}
								}
							} else {
								LOG.warn("Response from server to command ["
										+ dataPointVO.getFirstCommand()
										+ " - "
										+ dataPointVO.getFirstCommandHexASCII()
										+ "] does not coincide with the expected one ["
										+ dataPointVO
												.getFirstExpectedResponse()
										+ "]");
								raiseEvent(
										POINT_READ_EXCEPTION_EVENT,
										time,
										true,
										new LocalizableMessage(
												"event.socketComm.noExpectedResponse",
												dataPointVO.getFirstCommand(),
												dataPointVO
														.getFirstCommandHexASCII(),
												dataPointVO
														.getFirstExpectedResponse()));
							}
						}
						/*
						 * DBH: Insert the first data (server response data
						 * until reach the format condition) into the
						 * corresponding data point value
						 */
						if (is_data_available) {
							if (is_expected_resp) {
								try {
									insertValueIntoDatapoint(dataPoint,
											response, time);
									returnToNormal(POINT_READ_EXCEPTION_EVENT,
											time);

								} catch (LocalizableException le) {
									LOG.warn("The value ["
											+ response
											+ "] could NOT be inserted into the data point");
									raiseEvent(POINT_READ_EXCEPTION_EVENT,
											time, true,
											le.getLocalizableMessage());
								}
							}
						} else {
							LOG.warn("No data available from server. Server is not sending data periodically or does not response to commands.");
							raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, time, true,
									new LocalizableMessage(
											"event.socketComm.noDataAvailable"));
						}
				}
			}
			/*
			 * DBH: After getting all the data from the server, no alarms are expected,
			 * so any previous datasource event or alarm is cleared and the socket is
			 * closed until the next polling process
			 */
			returnToNormal(DATA_SOURCE_EXCEPTION_EVENT,
					System.currentTimeMillis());
			closeConnection();
		} catch (LocalizableException le) {
			closeConnection();
			LOG.warn("Problem while a request to the data source was processed. Details: "
					+ le.getLocalizableMessage()
							.getLocalizedMessage(Common.getBundle()));
			raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, time, true,
					le.getLocalizableMessage());
		} catch (IOException ioe) {
			closeConnection();
			LOG.error("Could not get response from server " + vo.getHost()
					+ " listening at port " + vo.getPort());
			raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, 
					System.currentTimeMillis(),
					true, 
					new LocalizableMessage(
							"event.socketComm.connectionError", 
							vo.getHost(),
							vo.getPort(), 
							ioe.getMessage()));
		} catch (Exception e) {
			closeConnection();
			LOG.error(
					"Exception while updating data from server device. Details: ",
					e);
			raiseEvent(
					DATA_SOURCE_EXCEPTION_EVENT,
					time,
					true,
					new LocalizableMessage("event.socketComm.exception", vo
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
		case 4:
			option_str = "\0";
			break;
		default:
			option_str = "";
			break;

		}
		return option_str;
	}

	/**
	 * @brief Build the command to send with an understandable format for the
	 *        server.
	 * 
	 *        The command is built based on the command type and end characters
	 *        configured at the datasource.
	 * 
	 * @param command
	 *            Command introduced by the user into the datasource.
	 * @param hex_or_char
	 *            Value in case that the command type is an ASCII string or Hex
	 *            value.
	 * @param option
	 *            Possible options for end characters to build a formatted
	 *            command.
	 * @return The formatted command to send to the server.
	 */
	private String buildCommand(String command_type, String hex_or_char,
			int option) {
		String command_to_send;

		command_to_send = buildCommand(command_type, hex_or_char);

		return command_to_send + getCommandEndFormat(option);

	}

	/**
	 * @brief Select and build the base command to send to the server based on
	 *        the type of command defined by the datasource.
	 * 
	 * @param command_type
	 *            Command type to send to the server.
	 * @param hex_or_char
	 *            Value in case that the command type is an ASCII string or an
	 *            Hex value.
	 * 
	 * @return String representation of the base command.
	 * 
	 */
	private String buildCommand(String command_type, String hex_or_char) {
		String command_to_send;
		if (command_type.equals(SocketCommDataSourceVO.HEX_STRING)
				|| command_type.equals(SocketCommDataSourceVO.ASCII_STRING)) {
			if (command_type.equals(SocketCommDataSourceVO.HEX_STRING)) {
				command_to_send = convertStringToHex(hex_or_char);
			} else {
				command_to_send = hex_or_char;
			}
		} else {
			if (command_type.equals(SocketCommDataSourceVO.STX_STRING)) {
				command_to_send = Character
						.toString(SocketCommDataSourceVO.STX_HEX);
			} else if (command_type.equals(SocketCommDataSourceVO.ETX_STRING)) {
				command_to_send = Character
						.toString(SocketCommDataSourceVO.ETX_HEX);
			} else if (command_type.equals(SocketCommDataSourceVO.ENQ_STRING)) {
				command_to_send = Character
						.toString(SocketCommDataSourceVO.ENQ_HEX);
			} else if (command_type.equals(SocketCommDataSourceVO.ACK_STRING)) {
				command_to_send = Character
						.toString(SocketCommDataSourceVO.ACK_HEX);
			} else {
				command_to_send = "";
			}
		}
		return command_to_send;
	}

	/**
	 * @brief Process the command request to the server and get the response.
	 * 
	 * @param command_str
	 *            Command to send to the server.
	 * @param point_name
	 *            Name of the actual data point.
	 * @return Server response to the command request.
	 * @throws LocalizableException
	 *             Debugging exception to locate null or empty command
	 * @throws Exception
	 */
	private String processRequest(String command_str, String point_name)
			throws LocalizableException, IOException, Exception {

		String response = "";

		if (!command_str.equals(null)) {
			/*
			 * One by One is used in this datasource to indicate that the server
			 * requires that the client opens and closes the socket connection
			 * for each query to the server.
			 */
			if (vo.isOneByOne() && isConnectionClosed()) {
				openConnection();

			}

			sendRequestToServer(command_str);
			response = getServerResponse(command_str);

			/*
			 * Once the server response has been received, the socket connection
			 * is closed if the One By One parameter is checked indicating that
			 * the connection must be reopen for each configured datapoint.
			 */
			if (vo.isOneByOne() && !isConnectionClosed()) {
				closeConnection();
			}

		} else {

			/*
			 * DBH: No command provided, so a request can not be done.
			 */
			LOG.warn("Command is null or empty at data point: " + point_name);
			throw new LocalizableException(new LocalizableMessage(
					"event.socketComm.noCommand", point_name));

		}

		return response;
	}

	/**
	 * brief Send the command request to the server in order to get the
	 * appropriate response from the server.
	 * 
	 * @param command_str
	 *            The formatted command to send to the server.
	 * 
	 */
	private void sendRequestToServer(String command_str) {
		out_stream.print(command_str);
		out_stream.flush();
	}

	/**
	 * @brief Get the values returned by the server depending on the frame
	 *        format defined at the datasource.
	 * 
	 *        The response to be captured is dependent on the defined stop mode.
	 *        There are two possibilities: - Stop receiving data from server
	 *        when a defined number of characters are received. - Stop receiving
	 *        data from server when a defined ASCII or HEX character is
	 *        received.
	 * 
	 * @param command_str
	 *            The formatted command to send to the server.
	 * @return The response from the server.
	 * @throws LocalizableException
	 *             Debugging exception to locate possible errors related to bad
	 *             stop characters or elapsed timeout without a proper response.
	 * @throws Exception
	 */
	private String getServerResponse(String command_str)
			throws LocalizableException, Exception {
		StringBuilder response = new StringBuilder();
		StringBuilder stop_chars = new StringBuilder();
		int response_char = 0;
		int response_char_size = 0;
		int response_len = 0;
		int stop_chars_len = 0;
		boolean is_stop_receiving = false;


		/*
		 * DBH: The awaited response from the server is delimited depends on the
		 * parameters configured for this purpose at the datasource. There are
		 * two possible options to delimit the desired response: when stop
		 * characters are matched or when a defined number of characters is
		 * received.
		 */
		try {
			if (vo.isSameFormat()) {
				is_stop_receiving = false;
				stop_chars.append(getCommandEndFormat(vo.getCommandFormat()));
				stop_chars_len = stop_chars.length();
				/*
				 * DBH: Read response until the stop characters are reached. If
				 * the stop characters are not reached before the socket timeout
				 * elapses an Exception is thrown , and the response is
				 * discarded
				 */
				while (!is_stop_receiving) {
					response_char = in_stream.read();
					if (response_char != -1) {
						response.append((char) response_char);
						if (response_char == stop_chars.charAt(stop_chars_len - 1)) {
							response_len = response.length();
							int stop_chars_found = 1; 
							while (stop_chars_found < stop_chars_len) {
								if (response.charAt(response_len - 1 - stop_chars_found) != stop_chars.charAt(stop_chars_len - 1 - stop_chars_found)) {
									is_stop_receiving = false;
									break;
								} else {
									stop_chars_found++;
								}
							}
							if (stop_chars_found == stop_chars_len) {
								response.delete(response_len - stop_chars_len, response_len -1);
								is_stop_receiving = true;
							}
						}
					} else {
						is_stop_receiving = true;
						LOG.warn("End of stream reached before reach the limit character defined in the data source");
					}
				}
			} else {
				/*
				 * DBH: Stop mode used to select only the received data until a
				 * determined condition is reached, if the command and response
				 * do not share the same frame format.
				 */
				switch (vo.getStopMode()) {
				case 0:

					// DBH: When the response stop condition is a number of
					// characters
					if (vo.getnChar() > 0) {
						response_char_size = vo.getnChar();
					
						// DBH: Read a number of characters from the response
						for (int i = 0; i < response_char_size; i++) {
							response_char = in_stream.read();
							if (response_char != -1) {
								response.append((char) response_char);
							} else {
								LOG.warn("End of stream reached before reading the number of characters limit defined in the data source");
								throw new LocalizableException(new LocalizableMessage(
										"event.socketComm.uncompleteResponse"));
							}
						}
					}
					break;
				case 1:
					switch (vo.getCharStopMode()) {
					case 0:
						// DBH: When the response stop condition is a concrete
						// ASCII
						// character
						if (!vo.getCharX().equals(null)
								&& !vo.getCharX().equals("")) {
							stop_chars.append(vo.getCharX());
						}
						break;
					case 1:
						// DBH: When the response stop condition is a concrete
						// HEX
						// value
						if (!vo.getHexValue().equals(null)
								&& !vo.getHexValue().equals("")) {
							stop_chars.append((char) Integer.parseInt(
									vo.getHexValue(), 16));
						}
						break;
					default:
						stop_chars.append("");
						break;

					}

					if (stop_chars.toString().isEmpty()) {
						throw new LocalizableException(new LocalizableMessage(
								"event.socketComm.noStopChars"));
					} else {
						stop_chars_len = stop_chars.length();
					}
					/*
					 * DBH: Read response until the stop characters are reached.
					 * If the stop characters are not reached before the socket
					 * timeout elapses an Exception is thrown , and the response
					 * is discarded
					 */
					while (!is_stop_receiving) {
						response_char = in_stream.read();
						if (response_char != -1) {
							response.append((char) response_char);
							if (response_char == stop_chars.charAt(stop_chars_len - 1)) {
								response_len = response.length();
								int stop_chars_found = 1; 
								while (stop_chars_found < stop_chars_len) {
									if (response.charAt(response_len - 1 - stop_chars_found) != stop_chars.charAt(stop_chars_len - 1 - stop_chars_found)) {
										is_stop_receiving = false;
										break;
									} else {
										stop_chars_found++;
									}
								}
								if (stop_chars_found == stop_chars_len) {
									response.delete(response_len - stop_chars_len, response_len -1);
									is_stop_receiving = true;
								}
							}
						} else {
							is_stop_receiving = true;
							LOG.warn("End of stream reached before reach the limit character defined in the data source");
						}
					}
					break;
				default:
					break;
				}
			}
		} catch (SocketTimeoutException ste) {
			throw new LocalizableException(new LocalizableMessage(
				"event.socketComm.uncompleteResponse"));
		}
		return response.toString();
	}

	/**
	 * @brief Check if the server response is the expected one by the data
	 *        point.
	 * 
	 *        Depending on the response type, the expected response could be an
	 *        ASCII string, an Hex value or a non printable character (EXT, ENQ,
	 *        ACK, NACK,...).
	 * 
	 * @param response
	 *            Response to check.
	 * @param response_type
	 *            Type of response expected.
	 * @param hex_or_char
	 *            Value in case that the response type is an ASCII string or Hex
	 *            value.
	 * @return True when the response is the expected one.
	 */
	private boolean isExpectedResponse(String response, String response_type,
			String hex_or_char) {
		boolean is_expected = false;
		String hex_str = "";
		String hex_response = "";

		if (response_type.equals(SocketCommDataSourceVO.HEX_STRING)
				|| response_type.equals(SocketCommDataSourceVO.ASCII_STRING)) {
			// DBH: If response is an ASCII string or an HEX value, compares the
			// response with the expected one
			if (response_type.equals(SocketCommDataSourceVO.HEX_STRING)) {
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
			/*
			 * DBH: If response is a non printable character, check if the
			 * corresponding HEX value of that character matches the expected
			 * non printable character HEX value
			 */
			hex_response = convertStringToHex(response);
			for (int i = 0; i < SocketCommDataSourceVO.NON_PRINTABLE_CHARS.length; i++) {
				if (response_type
						.equals(SocketCommDataSourceVO.NON_PRINTABLE_CHARS[i]
								.getAsciiStr())
						&& Integer.parseInt(hex_response) == SocketCommDataSourceVO.NON_PRINTABLE_CHARS[i]
								.getAsciiHex()) {
					is_expected = true;
					break;
				}
			}
		}
		return is_expected;
	}

	private long getTimestamp(SocketCommPointLocatorVO point, String arquivo)
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

	/**
	 * @brief Insert a data string and update the corresponding data point
	 * 
	 * @param data_point
	 *            Data point to update
	 * @param data_values_str
	 *            Data string to insert into data point
	 * @param time
	 *            Time of data point update
	 * @throws LocalizableException
	 * @throws Exception
	 */
	private void insertValueIntoDatapoint(DataPointRT data_point,
			String data_values_str, long time) throws LocalizableException,
			Exception {
		MangoValue value = null;
		long timestamp = 0;
		SocketCommPointLocatorVO dataPointVO;

		if (!data_values_str.isEmpty()) {

			dataPointVO = data_point.getVO().getPointLocator();
			Pattern valuePattern = Pattern.compile(dataPointVO.getValueRegex());
			value = DataSourceUtils.getValue(valuePattern, data_values_str,
					dataPointVO.getDataType(), "false", data_point.getVO()
							.getTextRenderer(), null, data_point.getVO()
							.getName());
			timestamp = time;
			if (dataPointVO.isCustomTimestamp()) {

				timestamp = getTimestamp(dataPointVO, data_values_str);
			}
			data_point.updatePointValue(new PointValueTime(value, timestamp));

		}
	}

	@Override
	public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime,
			SetPointSource source) {

	}

	/**
	 * @brief Convert a string to HEx value
	 * 
	 * @param str
	 *            String to convert to the corresponding HEX value
	 * @return HEX value for the input string
	 */
	public String convertStringToHex(String str) {

		char[] chars = str.toCharArray();

		StringBuffer hex = new StringBuffer();
		for (int i = 0; i < chars.length; i++) {
			hex.append(Integer.toHexString((int) chars[i]));
		}

		return hex.toString();
	}

	/**
	 * @brief Convert an HEX value to a string
	 * 
	 * @param hex
	 *            HEX value to convert to the corresponding string
	 * @return String for the input HEX value
	 */
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
	synchronized public void terminate() {
		super.terminate();
		closeConnection();
	}

	/**
	 * @brief Open a new socket connection to the server
	 * 
	 * @throws IOException
	 *             Exception thrown after a number of failed connections
	 *             attempts, indicating a problem with the server
	 */
	private void openConnection() throws IOException {
		// Try 'retries' times to get the socket open.
		int retries = vo.getRetries();
		boolean is_connected = false;

		while (!is_connected) {
			try {
				socket = new Socket();
				socket.setSoTimeout(vo.getTimeout());
				socket.connect(
						new InetSocketAddress(vo.getHost(), vo.getPort()),
						vo.getTimeout());
				out_stream = new PrintWriter(socket.getOutputStream(), true);
				in_stream = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				is_connected = true;
			} catch (IOException ioe) {
				closeConnection();
				if (retries == 0)
					throw ioe;
				LOG.warn("Open connection failed, trying again...");
				retries--;

				// Add a small delay
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					// no op
				}
			}
		}
	}

	/**
	 * @brief Close all the objects related to the socket connection to the
	 *        server.
	 */
	private void closeConnection() {
		try {
			if (out_stream != null) {
				out_stream.close();
				out_stream = null;
			}
			if (in_stream != null) {
				in_stream.close();
				in_stream = null;
			}
			if (socket != null) {
				socket.close();
				socket = null;
			}
		} catch (IOException e) {
			raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(),
					true, new LocalizableMessage(
							"event.socketComm.connectionError", e.getMessage()));
		}
	}

	private boolean isConnectionClosed() {
		boolean isClosed = false;
		if (socket == null) {
			isClosed = true;
		}
		return isClosed;

	}

}
