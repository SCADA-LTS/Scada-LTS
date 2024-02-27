package cc.radiuino.scadabr.rt.datasource.radiuino;

import com.serotonin.mango.util.LoggingUtils;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cc.radiuino.scadabr.vo.datasource.radiuino.RadiuinoDataSourceVO;
import cc.radiuino.scadabr.vo.datasource.radiuino.RadiuinoDataType;
import cc.radiuino.scadabr.vo.datasource.radiuino.RadiuinoPointLocatorVO;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataSource.EventDataSource;
import com.serotonin.web.i18n.LocalizableMessage;
import org.scada_lts.serial.gnu.io.ScadaCommPortIdentifier;

public class RadiuinoEventDataSource extends EventDataSource implements
		SerialPortEventListener {

	private final Log LOG = LogFactory.getLog(RadiuinoEventDataSource.class);
	public static final int POINT_READ_EXCEPTION_EVENT = 1;
	public static final int DATA_SOURCE_EXCEPTION_EVENT = 2;
	private static final int INDICE_ENDERECO_ORIGEM = 10;
	private static final int INDICE_ENDERECO_DESTINO = 8;
	private final RadiuinoDataSourceVO<?> vo;
	private Enumeration portList;
	private InputStream inSerialStream;
	private OutputStream outSerialStream;
	private SerialPort sPort;

	private byte[] buffer = new byte[1];
	private int iBuffer = 0;
	private byte[] pacote = new byte[52];

	private byte esperandoPacoteEndereco = 0;
	private boolean esperandoPacote = false;

	public RadiuinoEventDataSource(RadiuinoDataSourceVO<?> vo) {
		super(vo);
		this.vo = vo;
	}

	private boolean reconnect() {
		LOG.debug("Tentando reconectar a porta serial.");
		try {
			while (true) {
				Thread.sleep(5000);
				portList = ScadaCommPortIdentifier.getPortIdentifiers();
				SerialPort p = getPort(vo.getCommPortId(), vo.getTimeout());
				if (p != null) {
					LOG.debug("Conexao estabelecida com a porta serial");
					configurePort(getsPort());
					beginPolling();
					return true;
				}
			}
		} catch (Throwable e) {
			LOG.error("Erro ao conectar na porta serial: " + LoggingUtils.info(e, this));
			return false;
		}

	}

	@Override
	public void initialize() {

		LOG.debug("Inicializando o Radiuino Polling Data Source.");

		try {
			portList = ScadaCommPortIdentifier.getPortIdentifiers();
			getPort(vo.getCommPortId(), vo.getTimeout());
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

	@Override
	public void terminate() {
		super.terminate();
		try {
			SerialPort serialPort = getsPort();
			if(serialPort != null)
				serialPort.close();
			returnToNormal(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis());
		} catch (Throwable e) {
			raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(), true,
					new LocalizableMessage("event.exception2",
							vo.getName(), e.getMessage()));
		}

		LOG.debug("Terminando o Radiuino Polling Data Source.");
	}

	@Override
	public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime,
			SetPointSource source) {

		LOG.debug("Setando o valor do data point Radiuino."
				+ (dataPoint != null ? dataPoint.toString() : "") + " "
				+ (valueTime != null ? valueTime.toString() : ""));

		try {

			// Enviar os pacotes para cada sensor e fazer o parse da resposta
			RadiuinoPointLocatorVO dataPointVO = dataPoint.getVO()
					.getPointLocator();

			byte[] pacoteEnvio = new byte[52];

			// Sleep mode
			// if (dataPointVO.isSleepMode()) {
			// pacoteEnvio[4] = (byte) 1;
			// int tempo = dataPointVO.getSleepTime();
			// pacoteEnvio[5] = (byte) Math.round(tempo / 256);
			// pacoteEnvio[6] = (byte) (tempo % 256);
			// }
			String mapaBytes = dataPointVO.getMapaBytesEnvio();
			for (int i = 0; i < 52; i++) {
				if (i * 3 + 3 < mapaBytes.length()) {
					String b = mapaBytes.substring(i * 3, i * 3 + 3);
					if (!b.equalsIgnoreCase("   ")) {
						pacoteEnvio[i] = (byte) (pacoteEnvio[i] ^ Byte
								.parseByte(b));
					}
				}
			}

			pacoteEnvio[INDICE_ENDERECO_DESTINO] = (byte) dataPointVO
					.getEnderecoSensor();

			if (dataPointVO.getRadiuinoDataType() == RadiuinoDataType.BINARY) {
				pacoteEnvio[dataPointVO.getIndiceByte()] = (byte) (valueTime
						.getBooleanValue() ? 1 : 0);
			} else if (dataPointVO.getRadiuinoDataType() == RadiuinoDataType.ONE_BYTE_INT_UNSIGNED) {
				pacoteEnvio[dataPointVO.getIndiceByte()] = (byte) (valueTime
						.getIntegerValue());
			} else if (dataPointVO.getRadiuinoDataType() == RadiuinoDataType.TWO_BYTE_INT_UNSIGNED) {
				pacoteEnvio[dataPointVO.getIndiceByte()] = (byte) (valueTime
						.getIntegerValue() >> 8);
				pacoteEnvio[dataPointVO.getIndiceByte() + 1] = (byte) (valueTime
						.getIntegerValue());
			} else if (dataPointVO.getRadiuinoDataType() == RadiuinoDataType.FOUR_BYTE_INT_UNSIGNED) {
				pacoteEnvio[dataPointVO.getIndiceByte()] = (byte) (valueTime
						.getIntegerValue() >> 24);
				pacoteEnvio[dataPointVO.getIndiceByte() + 1] = (byte) (valueTime
						.getIntegerValue() >> 16);
				pacoteEnvio[dataPointVO.getIndiceByte()] = (byte) (valueTime
						.getIntegerValue() >> 8);
				pacoteEnvio[dataPointVO.getIndiceByte() + 1] = (byte) (valueTime
						.getIntegerValue());
			} else if (dataPointVO.getRadiuinoDataType() == RadiuinoDataType.RSSI) {
			}

			enviarPacote(pacoteEnvio, 1);

		} catch (IOException io) {
			LOG.error("Erro ao setar valor no data point Radiuino: " + LoggingUtils.info(io, this));
			try {
				getsPort().close();
				reconnect();
			} catch (Throwable e) {
				LOG.error(LoggingUtils.info(e, this));
			}
		} catch (Exception e) {
			LOG.error("Erro geral ao setar valor no datasource: " + LoggingUtils.info(e, this));
		}

	}

	private void enviarPacote(final byte[] pacoteEnvio, final int retries)
			throws IOException {
		LOG.debug("Enviar pacote Radiuino pela porta serial.");

		int timeout = vo.getTimeout();

		synchronized (this) {

			iBuffer = 0;

			for (int i = 0; i < vo.getRetries(); i++) {
				if (!esperandoPacote) {
					LOG.debug("Enviando pacote para "
							+ pacoteEnvio[INDICE_ENDERECO_DESTINO]
							+ " tentativa " + i);

					getOutSerialStream().write(pacoteEnvio);
					esperandoPacoteEndereco = pacoteEnvio[INDICE_ENDERECO_DESTINO];
					esperandoPacote = true;
				} else {
					break;
				}

				long startTime = new Date().getTime();
				LOG.debug("Esperando pacote " + new Date().getTime());
				do {
					if (!esperandoPacote) {
						LOG.debug("Pacote recebido " + new Date().getTime());
						break;
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						LOG.error("Erro ao aguardar por pacote: " + LoggingUtils.info(e, this), e);
					}
				} while ((new Date().getTime() - startTime) < timeout);
				LOG.debug("Ainda esperando pacote " + new Date().getTime());

				if (!esperandoPacote) {
					break;
				} else {
					esperandoPacote = false;
				}
			}
		}
	}

	private void configurePort(SerialPort port) {

		LOG.debug("Configurando porta serial.");

		try {
			setInSerialStream(port.getInputStream());
			setOutSerialStream(port.getOutputStream());
		} catch (Exception e) {
			LOG.error("Erro ao configurar streaming de in e out: " + LoggingUtils.info(e, this));
		}

		port.notifyOnDataAvailable(true);
		try {
			port.addEventListener(this);
			port.enableReceiveTimeout(vo.getTimeout());
		} catch (UnsupportedCommOperationException e1) {
			LOG.error("Comando nao suportado ao abrir a porta serial: " + LoggingUtils.info(e1, this));
		} catch (TooManyListenersException e) {
			LOG.error("Muitos Listeners adicionados a porta serial: " + LoggingUtils.info(e, this));
		}

		try {
			port.setSerialPortParams(vo.getBaudRate(), vo.getDataBits(),
					vo.getStopBits(), vo.getParity());
		} catch (Exception e) {
			LOG.error("Erro ao setar parametros da porta serial: " + LoggingUtils.info(e, this));
		}

	}

	private SerialPort getPort(String port, int timeout) {
		LOG.debug("Tentando capturar a porta serial.");
		SerialPort serialPort = null;
		while (portList.hasMoreElements()) {
			CommPortIdentifier portId = (CommPortIdentifier) portList
					.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (portId.getName().equals(port)) {
					try {
						serialPort = (SerialPort) portId.open(this.getName(),
								timeout);
						setsPort(serialPort);
					} catch (Exception e) {
						LOG.error("Erro ao abrir a porta serial: " + LoggingUtils.info(e, this));
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

	@Override
	public void serialEvent(SerialPortEvent serialPortEvent) {
		int eType = serialPortEvent.getEventType();
		switch (eType) {
		case SerialPortEvent.DATA_AVAILABLE:

			try {
				int i = getInSerialStream().read(buffer);
				if (i == 1) {
					pacote[iBuffer++] = buffer[0];
				}
				if (iBuffer == 52) {
					iBuffer = 0;
					esperandoPacote = false;
					pacoteRecebido();
				}
			} catch (IOException e) {
				LOG.error("Erro ao receber dados da porta serial: " + LoggingUtils.info(e, this));
			}

		default:
			break;
		}
	}

	private void pacoteRecebido() {
		LOG.debug("Fazendo parse do pacote Radiuino para salvar nos datasources correspondentes.");
		int enderecoSensor = pacote[INDICE_ENDERECO_ORIGEM];
		for (DataPointRT dataPoint : dataPoints) {
			RadiuinoPointLocatorVO dataPointVO = dataPoint.getVO()
					.getPointLocator();
			if (enderecoSensor == dataPointVO.getEnderecoSensor()) {
				long time = new Date().getTime();
				try {
					PointValueTime pointValueTime = RadiuinoUtils
							.parsePacoteRadiuino(time, pacote, dataPointVO);
					if (pointValueTime != null)
						dataPoint.updatePointValue(pointValueTime);
					returnToNormal(POINT_READ_EXCEPTION_EVENT, time, dataPoint);
				} catch (Exception e) {
					LOG.error("Erro ao fazer o parse dos dados: " + LoggingUtils.info(e, this, dataPoint));
					raiseEvent(
							POINT_READ_EXCEPTION_EVENT,
							time,
							true,
							new LocalizableMessage("event.exception2", vo
									.getName(), e.getMessage()), dataPoint);
				}

			}
		}
	}

}
