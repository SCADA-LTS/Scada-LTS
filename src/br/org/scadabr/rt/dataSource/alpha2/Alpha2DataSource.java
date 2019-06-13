package br.org.scadabr.rt.dataSource.alpha2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.org.scadabr.vo.dataSource.alpha2.Alpha2DataSourceVO;
import br.org.scadabr.vo.dataSource.alpha2.Alpha2PointLocatorVO;

import com.i2msolucoes.alpha24j.DeviceLocator;
import com.i2msolucoes.alpha24j.DeviceValue;
import com.i2msolucoes.alpha24j.DeviceWrite;
import com.i2msolucoes.alpha24j.DeviceLocator.DeviceCodes;
import com.i2msolucoes.alpha24j.exception.COMMException;
import com.i2msolucoes.alpha24j.exception.ErrorMessageReceivedException;
import com.i2msolucoes.alpha24j.exception.InvalidFrameReceivedException;
import com.i2msolucoes.alpha24j.layer.user.Alpha2Master;
import com.serotonin.io.serial.SerialParameters;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataSource.PollingDataSource;
import com.serotonin.web.i18n.LocalizableMessage;

public class Alpha2DataSource extends PollingDataSource {

	public static final int POINT_READ_EXCEPTION_EVENT = 1;
	public static final int POINT_WRITE_EXCEPTION_EVENT = 2;
	public static final int DATA_SOURCE_EXCEPTION_EVENT = 3;

	private final Alpha2DataSourceVO<?> vo;
	private Alpha2Master master;

	public Alpha2DataSource(Alpha2DataSourceVO<?> vo) {
		super(vo);
		this.vo = vo;
		setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(), false);
	}

	@Override
	protected void doPoll(long time) {
		List<DeviceLocator> locators = new ArrayList<DeviceLocator>();

		Map<DeviceLocator, DataPointRT> map = new HashMap<DeviceLocator, DataPointRT>();

		for (DataPointRT pointRT : dataPoints) {
			Alpha2PointLocatorVO dp = pointRT.getVO().getPointLocator();

			if (dp.isReadable()) {
				DeviceLocator locator = new DeviceLocator(DeviceCodes
						.toDeviceCode(dp.getDeviceCodeId()), dp
						.getDeviceNumber());
				locators.add(locator);
				map.put(locator, pointRT);
			}
		}

		try {
			List<DeviceValue> values = read(locators);
			for (DeviceValue deviceValue : values) {
				DataPointRT rt = map.get(deviceValue.getLocator());
				if (rt != null) {
					MangoValue value = MangoValue.stringToValue(deviceValue
							.getValue(), rt.getDataTypeId());
					rt.updatePointValue(new PointValueTime(value, time));
				}
			}

		} catch (Exception e) {
			treatException(POINT_READ_EXCEPTION_EVENT, e, System
					.currentTimeMillis());
		}

	}

	private List<DeviceValue> read(List<DeviceLocator> devices)
			throws COMMException, InvalidFrameReceivedException,
			ErrorMessageReceivedException {

		final int maxNum = Alpha2Master.MAX_DEVICES_PER_READ_MESSAGE;
		List<List<DeviceLocator>> messages = new ArrayList<List<DeviceLocator>>();

		int numMessages = devices.size() % maxNum == 0 ? ((int) (devices.size() / maxNum))
				: ((int) (devices.size() / maxNum)) + 1;

		for (int i = 0; i < numMessages; i++) {
			List<DeviceLocator> temp;
			if (i == numMessages - 1) {
				temp = devices.subList(0 + (i * (maxNum)), devices.size());
			} else {
				temp = devices.subList(0 + (i * (maxNum)), (maxNum)
						+ ((maxNum) * i));

			}
			messages.add(temp);
		}

		List<DeviceValue> values = new ArrayList<DeviceValue>();

		for (List<DeviceLocator> message : messages) {
			values.addAll(master.read(message));
		}

		return values;
	}

	@Override
	public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime,
			SetPointSource source) {
		List<DeviceWrite> devices = new ArrayList<DeviceWrite>();
		Alpha2PointLocatorVO dp = dataPoint.getVO().getPointLocator();

		boolean runStop = false;

		if (dp.getDeviceCodeId() == Alpha2PointLocatorVO.RUN_STOP_CODE) {
			runStop = true;

		} else {
			DeviceLocator locator = new DeviceLocator(DeviceCodes
					.toDeviceCode(dp.getDeviceCodeId()), dp.getDeviceNumber());

			devices.add(new DeviceWrite(locator, valueTime.getIntegerValue()));

		}

		try {
			if (runStop) {
				if (valueTime.getBooleanValue())
					master.runController();
				else
					master.stopController();
			} else
				master.write(devices);
		} catch (Exception e) {
			treatException(POINT_WRITE_EXCEPTION_EVENT, e, System
					.currentTimeMillis());
		}

	}

	@Override
	public void initialize() {
		super.initialize();

		SerialParameters parameters = new SerialParameters();
		parameters.setBaudRate(vo.getBaudRate());
		parameters.setCommPortId(vo.getCommPortId());
		parameters.setParity(vo.getParity());
		parameters.setStopBits(vo.getStopBits());
		parameters.setDataBits(vo.getDataBits());
		master = new Alpha2Master((byte) vo.getStation(), parameters, vo
				.getTimeout(), vo.getRetries());
		try {
			master.init();
			master.lineCheck();
		} catch (Exception e) {
			treatException(DATA_SOURCE_EXCEPTION_EVENT, e, System
					.currentTimeMillis());
		}

	}

	@Override
	public void terminate() {
		super.terminate();
		try {
			master.terminate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void treatException(int exceptionType, Exception e, long time) {
		if (e instanceof COMMException) {
			raiseEvent(exceptionType, time, true, new LocalizableMessage(
					"alpha2.commException", vo.getName(), e.getMessage()));
		} else if (e instanceof InvalidFrameReceivedException) {
			raiseEvent(exceptionType, time, true, new LocalizableMessage(
					"alpha2.invalidFrameException", vo.getName(), e
							.getMessage()));
		} else if (e instanceof ErrorMessageReceivedException) {
			raiseEvent(exceptionType, time, true, new LocalizableMessage(
					"alpha2.errorMessageException", vo.getName(), e
							.getMessage()));
		} else {
			raiseEvent(exceptionType, time, true, new LocalizableMessage(
					"alpha2.unknownException", vo.getName(), e.getMessage()));
		}
	}

	// public static void main(String[] args) {
	// List<String> lista = new ArrayList<String>();
	//
	// for (int i = 0; i < 250; i++) {
	// lista.add("device " + i);
	// }
	//
	// final int maxNum = 82;
	// List<List<String>> messages = new ArrayList<List<String>>();
	//
	// int numMessages = lista.size() % maxNum == 0 ? ((int) (lista.size() /
	// maxNum))
	// : ((int) (lista.size() / maxNum)) + 1;
	//
	// for (int i = 0; i < numMessages; i++) {
	// List<String> temp;
	// if (i == numMessages - 1) {
	// temp = lista.subList(0 + (i * (maxNum)), lista.size());
	// } else {
	// int init = 0 + (i * (maxNum));
	// int end = (maxNum) + ((maxNum) * i);
	// temp = lista.subList(init, end);
	//
	// }
	// messages.add(temp);
	// }
	// System.out.println("Enviar " + messages.size() + " mensagens!");
	// for (List<String> list : messages) {
	// System.out.println("Mensagem: " + list.size());
	// }
	//
	// }

}
