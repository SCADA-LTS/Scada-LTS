package br.org.scadabr.rt.dataSource.drStorageHt5b;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.org.scadabr.vo.dataSource.drStorageHt5b.DrStorageHt5bDataSourceVO;
import br.org.scadabr.vo.dataSource.drStorageHt5b.DrStorageHt5bPointLocatorVO;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataSource.PollingDataSource;
import com.serotonin.web.i18n.LocalizableMessage;

public class DrStorageHt5bDataSource extends PollingDataSource {

	private final Log LOG = LogFactory.getLog(DrStorageHt5bDataSource.class);
	public static final int POINT_READ_EXCEPTION_EVENT = 1;
	public static final int DATA_SOURCE_EXCEPTION_EVENT = 2;
	private final DrStorageHt5bDataSourceVO<?> vo;
	private Enumeration portList;
	private InputStream inSerialStream;
	private OutputStream outSerialStream;
	private SerialPort sPort;
	static final String HEXES = "0123456789ABCDEF";
	private ArrayList<Integer> valuesHt5b;

	public DrStorageHt5bDataSource(DrStorageHt5bDataSourceVO<?> vo) {

		super(vo);
		this.vo = vo;
		setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(), vo
				.isQuantize());

		portList = CommPortIdentifier.getPortIdentifiers();
		getPort(vo.getCommPortId());
		configurePort(getsPort());
		setValuesHt5b(new ArrayList<Integer>());
	}

	@Override
	protected void doPoll(long time) {

		try {

			if (getInSerialStream().available() == 0) {

				raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, time, true,
						new LocalizableMessage("event.exception2",
								vo.getName(), "Sem dados disponíveis !"));

			} else if (getInSerialStream().available() > 0) {

				byte[] readBuffer = new byte[11];

				try {

					while (getInSerialStream().available() > 0) {
						getInSerialStream().read(readBuffer);
					}

					// Set all necessary values to valuesHt5b
					toStringHexa(readBuffer);

					String temp = getTemperature(getValuesHt5b());
					String hum = getHumidity(getValuesHt5b());

					for (DataPointRT dataPoint : dataPoints) {

						try {
							DrStorageHt5bPointLocatorVO dataPointVO = dataPoint
									.getVO().getPointLocator();

							if (dataPointVO.getPointType()
									.equals("Temperature")) {
								dataPoint.updatePointValue(new PointValueTime(
										temp, System.currentTimeMillis()));
							}

							if (dataPointVO.getPointType().equals("Humidity")) {
								dataPoint.updatePointValue(new PointValueTime(
										hum, System.currentTimeMillis()));
							}

						} catch (Exception e) {
							raiseEvent(POINT_READ_EXCEPTION_EVENT, time, true,
									new LocalizableMessage("event.exception2",
											vo.getName(), e.getMessage()));
							e.printStackTrace();
						}

					}

					getValuesHt5b().clear();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime,
			SetPointSource source) {
		// TODO Auto-generated method stub

	}

	@Override
	public void terminate() {
		super.terminate();
		getsPort().close();
	}

	private String getTemperature(ArrayList<Integer> ht5bValues) {

		String Temperature = "";

		int Faixa = ht5bValues.get(5);
		int Valor = ht5bValues.get(6);

		if (Faixa == 0) {
			Temperature = Integer.toString(Valor);
			int tam = Temperature.length();

			if (tam == 3) {
				Temperature = Temperature.substring(0, 2) + "."
						+ Temperature.charAt(tam - 1);
			} else if (tam == 2) {
				Temperature = Temperature.substring(0, 1) + "."
						+ Temperature.charAt(tam - 1);
			} else if (tam == 1) {
				Temperature = "0" + "." + Temperature.charAt(tam - 1);
			}

		} else if (Faixa == 1) {
			Temperature = Integer.toString(Valor + 255);
			int tam = Temperature.length();
			Temperature = Temperature.substring(0, 2) + "."
					+ Temperature.charAt(tam - 1);
		} else if (Faixa == 2) {
			Temperature = Integer.toString(Valor + 510);
			int tam = Temperature.length();
			Temperature = Temperature.substring(0, 2) + "."
					+ Temperature.charAt(tam - 1);
		} else if (Faixa == 3) {
			Temperature = Integer.toString(Valor + 765);
			int tam = Temperature.length();
			Temperature = Temperature.substring(0, 2) + "."
					+ Temperature.charAt(tam - 1);
		}

		return Temperature;
	}

	private String getHumidity(ArrayList<Integer> ht5bValues) {

		String Humidity = "";

		int Faixa = ht5bValues.get(2);
		int Valor = ht5bValues.get(3);

		if (Faixa == 0) {
			Humidity = Integer.toString(Valor + 1);
			int tam = Humidity.length();

			if (tam == 3) {
				Humidity = Humidity.substring(0, 2) + "."
						+ Humidity.charAt(tam - 1);
			} else if (tam == 2) {
				Humidity = Humidity.substring(0, 1) + "."
						+ Humidity.charAt(tam - 1);
			} else if (tam == 1) {
				Humidity = "0" + "." + Humidity.charAt(tam - 1);
			}

		} else if (Faixa == 1) {
			Humidity = Integer.toString(Valor + 256);
			int tam = Humidity.length();
			Humidity = Humidity.substring(0, 2) + "."
					+ Humidity.charAt(tam - 1);
		} else if (Faixa == 2) {
			Humidity = Integer.toString(Valor + 511);
			int tam = Humidity.length();
			Humidity = Humidity.substring(0, 2) + "."
					+ Humidity.charAt(tam - 1);
		} else if (Faixa == 3) {
			Humidity = Integer.toString(Valor + 766);
			int tam = Humidity.length();
			Humidity = Humidity.substring(0, 2) + "."
					+ Humidity.charAt(tam - 1);
		}

		return Humidity;
	}

	private void toStringHexa(byte[] bytes) {

		StringBuilder s = new StringBuilder();

		for (int i = 0; i < bytes.length; i++) {

			int parteAlta = ((bytes[i] >> 4) & 0xf) << 4;

			int parteBaixa = bytes[i] & 0xf;

			if (parteAlta == 0)
				s.append('0');

			getValuesHt5b().add(parteAlta | parteBaixa);

			if (i == 6) {
				return;
			}

			s.append(Integer.toHexString(parteAlta | parteBaixa));
		}

	}

	public static String getHexString(byte[] b) throws Exception {
		String result = "";

		System.out.println("INICIO");

		for (int i = 0; i < b.length; i++) {
			System.out.println(Integer.toString((b[i] & 0xff) + 0x100, 16)
					.substring(1));
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}

		System.out.println("FIM");

		return result;
	}

	public static String getHex(byte[] raw) {

		if (raw == null) {
			return null;
		}

		final StringBuilder hex = new StringBuilder(2 * raw.length);

		for (final byte b : raw) {
			hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(
					HEXES.charAt((b & 0x0F)));
		}

		return hex.toString();
	}

	public void configurePort(SerialPort port) {

		try {
			setInSerialStream(port.getInputStream());
			setOutSerialStream(port.getOutputStream());
		} catch (Exception e) {
		}

		port.notifyOnDataAvailable(true);

		try {
			port.setSerialPortParams(vo.getBaudRate(), vo.getDataBits(), vo
					.getStopBits(), vo.getParity());
		} catch (Exception e) {
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
					} catch (Exception e) {
						System.out.println("Error opening port "
								+ serialPort.getName() + "!");
					}
				}
			}
		}

		return serialPort;
	}

	public ArrayList<Integer> getValuesHt5b() {
		return valuesHt5b;
	}

	public void setValuesHt5b(ArrayList<Integer> valuesHt5b) {
		this.valuesHt5b = valuesHt5b;
	}

	public Enumeration getPortList() {
		return portList;
	}

	public void setPortList(Enumeration portList) {
		this.portList = portList;
	}

	public InputStream getInSerialStream() {
		return inSerialStream;
	}

	public void setInSerialStream(InputStream inSerialStream) {
		this.inSerialStream = inSerialStream;
	}

	public OutputStream getOutSerialStream() {
		return outSerialStream;
	}

	public void setOutSerialStream(OutputStream outSerialStream) {
		this.outSerialStream = outSerialStream;
	}

	public SerialPort getsPort() {
		return sPort;
	}

	public void setsPort(SerialPort sPort) {
		this.sPort = sPort;
	}

}
