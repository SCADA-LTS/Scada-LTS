package br.org.scadabr.rt.dataSource.opc;

import java.util.ArrayList;
import java.util.logging.Level;

import com.serotonin.mango.util.LoggingUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinterop.dcom.common.JISystem;

import br.org.scadabr.OPCMaster;
import br.org.scadabr.RealOPCMaster;
import br.org.scadabr.vo.dataSource.opc.OPCDataSourceVO;
import br.org.scadabr.vo.dataSource.opc.OPCPointLocatorVO;

import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataSource.PollingDataSource;
import com.serotonin.web.i18n.LocalizableMessage;

public class OPCDataSource extends PollingDataSource {

	private final Log LOG = LogFactory.getLog(OPCDataSource.class);
	public static final int POINT_READ_EXCEPTION_EVENT = 1;
	public static final int DATA_SOURCE_EXCEPTION_EVENT = 2;
	public static final int POINT_WRITE_EXCEPTION_EVENT = 3;
	private OPCMaster opcMaster;
	private final OPCDataSourceVO<?> vo;
	private int timeoutCount = 0;
	private int timeoutsToReconnect = 3;

	public OPCDataSource(OPCDataSourceVO<?> vo) {
		super(vo);
		this.vo = vo;
		setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(),
				vo.isQuantize());

		this.opcMaster = new RealOPCMaster();
		JISystem.getLogger().setLevel(Level.OFF);
	}

	@Override
	protected void doPoll(long time) {
		ArrayList<String> enabledTags = new ArrayList<String>();

		for (DataPointRT dataPoint : dataPoints) {
			OPCPointLocatorVO dataPointVO = dataPoint.getVO().getPointLocator();
			enabledTags.add(dataPointVO.getTag());
		}

		try {

			if (timeoutCount >= timeoutsToReconnect) {
				LOG.error("[OPC] Trying to reconnect ! :" + LoggingUtils.dataSourceInfo(this));
				timeoutCount = 0;
				initialize();
			} else {
				opcMaster.configureGroup(enabledTags);
				opcMaster.doPoll();
				returnToNormal(DATA_SOURCE_EXCEPTION_EVENT, time);
			}

		} catch (Throwable e) {
			LOG.error("[OPC] Poll Failed ! :" + LoggingUtils.info(e, this));
			raiseEvent(
					DATA_SOURCE_EXCEPTION_EVENT,
					time,
					true,
					new LocalizableMessage("event.exception2", vo.getName(), e
							.getMessage()));
			timeoutCount++;
			return;
		}

		for (DataPointRT dataPoint : dataPoints) {
			OPCPointLocatorVO dataPointVO = dataPoint.getVO().getPointLocator();
			MangoValue mangoValue = null;
			String value = "0";

			try {
				value = opcMaster.getValue(dataPointVO.getTag());

				mangoValue = MangoValue.stringToValue(value,
						dataPointVO.getDataTypeId());
				dataPoint
						.updatePointValue(new PointValueTime(mangoValue, time));
				returnToNormal(POINT_READ_EXCEPTION_EVENT, time, dataPoint);
			} catch (Throwable e) {
				raiseEvent(POINT_READ_EXCEPTION_EVENT, time, true,
						new LocalizableMessage("event.exception2",
								vo.getName(), e.getMessage()), dataPoint);
			}
		}
	}

	@Override
	public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime,
			SetPointSource source) {
		String tag = ((OPCPointLocatorVO) dataPoint.getVO().getPointLocator())
				.getTag();
		Object value = null;
		if (dataPoint.getDataTypeId() == DataTypes.NUMERIC)
			value = valueTime.getDoubleValue();
		else if (dataPoint.getDataTypeId() == DataTypes.BINARY)
			value = valueTime.getBooleanValue();
		else if (dataPoint.getDataTypeId() == DataTypes.MULTISTATE)
			value = valueTime.getIntegerValue();
		else
			value = valueTime.getStringValue();

		try {
			opcMaster.write(tag, value);
			returnToNormal(POINT_WRITE_EXCEPTION_EVENT, System.currentTimeMillis(), dataPoint);
		} catch (Throwable e) {
			LOG.error(LoggingUtils.info(e, this, dataPoint), e);
			raiseEvent(
					POINT_WRITE_EXCEPTION_EVENT,
					System.currentTimeMillis(),
					true,
					new LocalizableMessage("event.exception2", vo.getName(), e
							.getMessage()), dataPoint);
		}
	}

	public void initialize() {

		opcMaster.setHost(vo.getHost());
		opcMaster.setDomain(vo.getDomain());
		opcMaster.setUser(vo.getUser());
		opcMaster.setPassword(vo.getPassword());
		opcMaster.setServer(vo.getServer());
		opcMaster.setDataSourceXid(vo.getXid());

		try {
			opcMaster.init();
			returnToNormal(DATA_SOURCE_EXCEPTION_EVENT,
					System.currentTimeMillis());
		} catch (Throwable e) {
			String message = e.getMessage();
			if(e.getMessage() != null && e.getMessage().contains("Unknown Error")) {
				message = "The OPC DA Server for the data source settings may not be found. ";
			}
			message = "Error while initializing data source: " +  message;
			LOG.error(message + e.getMessage(), e);
			raiseEvent(
					DATA_SOURCE_EXCEPTION_EVENT,
					System.currentTimeMillis(),
					true,
					new LocalizableMessage("event.exception2", vo.getName(), message));
			return;
		}
		super.initialize();
	}

	@Override
	public void terminate() {
		super.terminate();
		try {
			if(opcMaster != null)
				opcMaster.terminate();
			returnToNormal(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis());
		} catch (Throwable e) {
			String message = e.getMessage();
			if(e instanceof NullPointerException) {
				message = "The client may not have been properly initialized. ";
			}
			message = "Error while terminating data source: " +  message;
			LOG.error(message + e.getMessage(), e);
			raiseEvent(
					DATA_SOURCE_EXCEPTION_EVENT,
					System.currentTimeMillis(),
					true,
					new LocalizableMessage("event.exception2", vo.getName(), message));
		}
	}

}
