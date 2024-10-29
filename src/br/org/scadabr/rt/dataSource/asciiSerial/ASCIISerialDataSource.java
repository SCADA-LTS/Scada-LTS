package br.org.scadabr.rt.dataSource.asciiSerial;

import com.serotonin.mango.util.LoggingUtils;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.org.scadabr.vo.dataSource.asciiSerial.ASCIISerialDataSourceVO;
import br.org.scadabr.vo.dataSource.asciiSerial.ASCIISerialPointLocatorVO;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataSource.DataSourceUtils;
import com.serotonin.mango.rt.dataSource.PollingDataSource;
import com.serotonin.web.i18n.LocalizableMessage;
import org.scada_lts.serial.gnu.io.ScadaCommPortIdentifier;

public class ASCIISerialDataSource extends PollingDataSource {

	private final Log LOG = LogFactory.getLog(ASCIISerialDataSource.class);
	public static final int POINT_READ_EXCEPTION_EVENT = 1;
	public static final int DATA_SOURCE_EXCEPTION_EVENT = 2;
	private final ASCIISerialDataSourceVO<?> vo;
	private Enumeration portList;
	private InputStream inSerialStream;
	private OutputStream outSerialStream;
	private SerialPort sPort;

	public ASCIISerialDataSource(ASCIISerialDataSourceVO<?> vo) {
		super(vo);
		this.vo = vo;
		setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(),
				vo.isQuantize());

	}

	private boolean reconnect() {

		try {
			while (true) {
				Thread.sleep(5000);
				portList = ScadaCommPortIdentifier.getPortIdentifiers();
				SerialPort p = getPort(vo.getCommPortId());
				if (p != null) {
					configurePort(getsPort());
					beginPolling();
					return true;
				}
			}
		} catch (Throwable e) {
			LOG.warn(LoggingUtils.info(e, this), e);
			return false;
		}

	}

	@Override
	protected void doPoll(long time) {

		try {

			// nao tem dados
			if (getInSerialStream() == null || getInSerialStream().available() == 0) {

				for (DataPointRT dataPoint : dataPoints) {
					ASCIISerialPointLocatorVO dataPointVO = dataPoint.getVO()
							.getPointLocator();
					if (!dataPointVO.getCommand().equals(null)
							&& !dataPointVO.getCommand().equals("")) {
						getOutSerialStream().write(
								dataPointVO.getCommand().getBytes());
					}

					// dataPoint.updatePointValue(new PointValueTime(
					// "Sem dados disponíveis !", new Date().getTime()));
				}
				raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, time, true,
						new LocalizableMessage("event.exception2",
								vo.getName(), "Sem dados disponíveis !"));
			} else if (getInSerialStream().available() > 0) {
				returnToNormal(DATA_SOURCE_EXCEPTION_EVENT, time);
				byte[] readBuffer = new byte[vo.getBufferSize()];
				try {

					while (getInSerialStream().available() > 0) {
						getInSerialStream().read(readBuffer);
					}

					int count = 0;

					String result = new String(readBuffer);
					// System.out.println("Result: " + result);

					// Pos processamento
					String posResults[];

					// String onde vai ser aplicada a Regex
					String posResultFinal = "";

					// Debug posResults
					posResults = result.split(vo.getCharX());
					for (int i = 0; i < posResults.length; i++) {
						// System.out.println("posResult: " + posResults[i]);
					}

					if (!vo.getCharX().equals(null)
							&& !vo.getCharX().equals("")) {
						// System.out.println("Caracterer :" + vo.getCharX());
						posResults = result.split(vo.getCharX());

						if (posResults.length != 0) {
							posResultFinal = posResults[0];
							// System.out.println("posResultFinal: "
							// + posResultFinal);
							// System.out.println("tamanho: " +
							// posResults.length);
							for (int i = 0; i < posResults.length; i++) {
								// System.out.println(posResults[i]);
							}
						}
					}

					for (DataPointRT dataPoint : dataPoints) {
						try {
							ASCIISerialPointLocatorVO dataPointVO = dataPoint
									.getVO().getPointLocator();
							if (!dataPointVO.getCommand().equals(null)
									&& !dataPointVO.getCommand().equals("")) {
								getOutSerialStream().write(
										dataPointVO.getCommand().getBytes());
							}

							// troquei posResult por result
							// alteracao eduardo
							ASCIISerialPointLocatorRT locator = dataPoint
									.getPointLocator();
							if (dataPoint.getVO().getName().equals("Temp")) {
								int a = 1;
								a++;
							}
							DecimalFormat d = new DecimalFormat("0.00");
							MangoValue value = DataSourceUtils.getValue(Pattern
									.compile(dataPointVO.getValueRegex()),
									result, dataPointVO.getDataType(), "false",
									dataPoint.getVO().getTextRenderer(), null,
									dataPoint.getVO().getName());
							// **

							// MangoValue value = getValue(dataPointVO, result);
							long timestamp = time;
							// write to port

							if (dataPointVO.isCustomTimestamp()) {
								try {
									timestamp = getTimestamp(dataPointVO,
											result);
								} catch (Exception e) {
									raiseEvent(
											POINT_READ_EXCEPTION_EVENT,
											time,
											true,
											new LocalizableMessage(
													"event.exception2", vo
															.getName(), e
															.getMessage()), dataPoint);
									timestamp = time;
								}

							}

							dataPoint.updatePointValue(new PointValueTime(
									value, timestamp));
							returnToNormal(POINT_READ_EXCEPTION_EVENT, time, dataPoint);
						} catch (Exception e) {
							LOG.warn(LoggingUtils.info(e, this), e);
							raiseEvent(POINT_READ_EXCEPTION_EVENT, time, true,
									new LocalizableMessage("event.exception2",
											vo.getName(), e.getMessage()), dataPoint);
							// e.printStackTrace();
						}

					}

				} catch (Throwable e) {
					LOG.warn(LoggingUtils.info(e, this), e);
				}
			}

		} catch (IOException io) {
			LOG.warn(LoggingUtils.info(io, this), io);
			try {
				getsPort().close();
				reconnect();
			} catch (Throwable e) {
				LOG.warn(LoggingUtils.info(io, this), e);
			}
		} catch (Throwable e) {
			LOG.warn(LoggingUtils.info(e, this), e);
		}
	}

	@Override
	public void initialize() {
		try {
			portList = ScadaCommPortIdentifier.getPortIdentifiers();
			getPort(vo.getCommPortId());
			configurePort(getsPort());
			returnToNormal(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis());
		} catch (Throwable e) {
			raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(), true,
					new LocalizableMessage("event.exception2",
							vo.getName(), e.getMessage()));
			return;
		}
		super.initialize();
	}

	private MangoValue getValue(ASCIISerialPointLocatorVO point, String arquivo)
			throws Exception {
		String valueRegex = point.getValueRegex();
		Pattern valuePattern = Pattern.compile(valueRegex);
		Matcher matcher = valuePattern.matcher(arquivo);
		// System.out.println("Utilizando string: " + arquivo
		// + "\n Utilizando regex: " + valueRegex);
		MangoValue value = null;
		String strValue = null;
		boolean found = false;
		if (matcher.find()) {
			found = true;
			strValue = matcher.group();
			value = MangoValue.stringToValue(strValue, point.getDataTypeId());
		}
		if (!found) {
			throw new Exception("Value string not found (regex: " + valueRegex
					+ ")");
		}

		return value;
	}

	private long getTimestamp(ASCIISerialPointLocatorVO point, String arquivo)
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

	@Override
	public void terminate() {
		super.terminate();
		try {
			SerialPort serialPort = getsPort();
			if(serialPort != null) {
				serialPort.close();
			}
			returnToNormal(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis());
		} catch (Throwable e) {
			raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(), true,
					new LocalizableMessage("event.exception2",
							vo.getName(), e.getMessage()));
		}
	}

	@Override
	public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime,
			SetPointSource source) {

	}

	public void configurePort(SerialPort port) {

		try {
			setInSerialStream(port.getInputStream());
			setOutSerialStream(port.getOutputStream());
		} catch (Throwable e) {
			LOG.warn(LoggingUtils.info(e, this), e);
		}

		port.notifyOnDataAvailable(true);

		try {
			port.setSerialPortParams(vo.getBaudRate(), vo.getDataBits(),
					vo.getStopBits(), vo.getParity());
		} catch (Throwable e) {
			LOG.warn(LoggingUtils.info(e, this), e);
		}

	}

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
					} catch (Throwable e) {
						LOG.error("Erro ao abrir a porta ! :" + LoggingUtils.info(e, this));
					}
				}
			}
		}

		return serialPort;
	}

	public OutputStream getOutSerialStream() {
		return outSerialStream;
	}

	public void setOutSerialStream(OutputStream outSerialStream) {
		this.outSerialStream = outSerialStream;
	}

	public InputStream getInSerialStream() {
		return inSerialStream;
	}

	public void setInSerialStream(InputStream inSerialStream) {
		this.inSerialStream = inSerialStream;
	}

	public SerialPort getsPort() {
		return sPort;
	}

	public void setsPort(SerialPort sPort) {
		this.sPort = sPort;
	}

}
