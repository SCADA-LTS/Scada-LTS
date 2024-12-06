package br.org.scadabr.rt.dataSource.opcua;

import br.org.scadabr.OpcUaMaster;
import br.org.scadabr.vo.dataSource.opcua.OpcUaDataSourceVO;
import br.org.scadabr.vo.dataSource.opcua.OpcUaPointLocatorVO;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataSource.PollingDataSource;
import com.serotonin.mango.util.LoggingUtils;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.web.i18n.LocalizableMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.serorepl.utils.StringUtils;


public class OpcUaDataSourceRT extends PollingDataSource {

	private final Log LOG = LogFactory.getLog(OpcUaDataSourceRT.class);
	public static final int POINT_READ_EXCEPTION_EVENT = 1;
	public static final int DATA_SOURCE_EXCEPTION_EVENT = 2;
	public static final int POINT_WRITE_EXCEPTION_EVENT = 3;
	private OpcUaMaster opcMaster;
	private final OpcUaDataSourceVO<?> vo;
	private int timeoutCount = 0;
	private int timeoutsToReconnect = 3;

	public OpcUaDataSourceRT(OpcUaDataSourceVO<?> vo) {
		super(vo);
		this.vo = vo;
		setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(),
				vo.isQuantize());
	}

	@Override
	protected void doPoll(long time) {

		try {

			if (timeoutCount >= timeoutsToReconnect) {
				LOG.error("[OPC UA] Trying to reconnect ! :" + LoggingUtils.dataSourceInfo(this));
				timeoutCount = 0;
				initialize();
			} else {
				opcMaster.ping();
				returnToNormal(DATA_SOURCE_EXCEPTION_EVENT, time);
			}

		} catch (Throwable e) {
			LOG.error("[OPC UA] Poll Failed ! :" + LoggingUtils.info(e, this));
			raiseEvent(
					DATA_SOURCE_EXCEPTION_EVENT,
					time,
					true,
					new LocalizableMessage("event.exception2", vo.getName(), e
							.getMessage()));
			timeoutCount++;
			return;
		}

		for (DataPointRT dataPoint : getDataPoints()) {
			OpcUaPointLocatorVO pointLocator = dataPoint.getVO().getPointLocator();
			MangoValue mangoValue = null;
			String value = "0";

			try {
				value = String.valueOf(opcMaster.read(pointLocator));
				mangoValue = MangoValue.stringToValue(value, pointLocator.getDataTypeId());
				dataPoint.updatePointValue(new PointValueTime(mangoValue, time));
				returnToNormal(POINT_READ_EXCEPTION_EVENT, time, dataPoint);
			} catch (Throwable e) {
				LOG.error("[OPC UA] Poll Failed ! :" + LoggingUtils.info(e, this) + " - "
						+ LoggingUtils.dataPointInfo(dataPoint) + " - " + LoggingUtils.pointLocatorInfo(pointLocator));
				raiseEvent(POINT_READ_EXCEPTION_EVENT, time, true,
						new LocalizableMessage("event.exception2",
								vo.getName(), e.getMessage()), dataPoint);
            }
		}
	}

	@Override
	public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime,
			SetPointSource source) {
		String tag = ((OpcUaPointLocatorVO) dataPoint.getVO().getPointLocator())
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
			opcMaster.write(dataPoint.getVO().getPointLocator(), valueTime.getValue());
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
		if(opcMaster != null) {
			try {
				opcMaster.terminate();
			} catch (Exception e) {
				LOG.warn(LoggingUtils.info(e, this));
			}
        }

		try {
			this.opcMaster = new OpcUaMaster(vo);
			opcMaster.init();
			returnToNormal(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis());
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

	public OpcUaDataSourceVO<?> getVo() {
		return vo;
	}
}
