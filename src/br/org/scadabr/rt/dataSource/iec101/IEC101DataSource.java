package br.org.scadabr.rt.dataSource.iec101;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.org.scadabr.protocol.iec101.session.database.DataElement;
import br.org.scadabr.vo.dataSource.iec101.IEC101DataSourceVO;
import br.org.scadabr.vo.dataSource.iec101.IEC101PointLocatorVO;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataSource.PollingDataSource;
import com.serotonin.web.i18n.LocalizableMessage;

public class IEC101DataSource extends PollingDataSource {
	private final Log LOG = LogFactory.getLog(IEC101DataSource.class);

	public static final int POINT_READ_EXCEPTION_EVENT = 1;
	public static final int POINT_WRITE_EXCEPTION_EVENT = 2;
	public static final int DATA_SOURCE_EXCEPTION_EVENT = 3;

	private IEC101Master iec101Master;
	private final IEC101DataSourceVO<?> vo;

	public IEC101DataSource(IEC101DataSourceVO<?> vo) {
		super(vo);
		this.vo = vo;
		setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(), vo
				.isQuantize());
	}

	@Override
	protected void doPoll(long time) {
		try {
			iec101Master.doPoll();
		} catch (Exception e) {
			raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, new Date().getTime(), true,
					new LocalizableMessage("event.exception2", vo.getName(), e
							.getMessage()));
			e.printStackTrace();
		}

		for (DataPointRT dataPoint : dataPoints) {
			IEC101PointLocatorVO pointLocator = dataPoint.getVO()
					.getPointLocator();

			List<DataElement> elements = iec101Master.read(pointLocator
					.getObjectAddress(), pointLocator.getIec101DataType());

			for (DataElement dataElement : elements) {
				MangoValue value = MangoValue.stringToValue(dataElement
						.getValue(), pointLocator.getDataTypeId());
				Calendar ts = Calendar.getInstance();
				ts.setTimeInMillis(dataElement.getTimestamp());
				dataPoint.updatePointValue(new PointValueTime(value, ts
						.getTimeInMillis()));
			}
		}
	}

	@Override
	public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime,
			SetPointSource source) {
		IEC101PointLocatorVO pointLocator = dataPoint.getVO().getPointLocator();

		boolean select = false;
		int ioa = pointLocator.getObjectAddress() + pointLocator.getOffset();
		byte qualifier = (byte) pointLocator.getQualifier();

		try {
			if (pointLocator.getIec101DataType() == IEC101Master.SINGLE_POINT_INFORMATION) {
				iec101Master.singleCommand(ioa, select, qualifier, valueTime
						.getBooleanValue());
			} else if (pointLocator.getIec101DataType() == IEC101Master.DOUBLE_POINT_INFORMATION) {
				String value = valueTime.getStringValue();
				boolean val = parseDoubleToBoolean(value);
				iec101Master.doubleCommand(ioa, select, qualifier, val);
			} else if (pointLocator.getIec101DataType() == IEC101Master.NORMALIZED_MEASURE) {
				iec101Master.setPointCommand(ioa, select, qualifier, valueTime
						.getIntegerValue());
			}
		} catch (Exception e) {
			raiseEvent(POINT_WRITE_EXCEPTION_EVENT, new Date().getTime(), true,
					new LocalizableMessage("event.exception2", vo.getName(), e
							.getMessage()));
			e.printStackTrace();
		}
	}

	private boolean parseDoubleToBoolean(String doubleValue) throws Exception {
		if (doubleValue != null) {
			if (doubleValue.trim().toLowerCase().equals("1")
					|| doubleValue.trim().toLowerCase().equals("on"))
				return true;
			else if (doubleValue.trim().toLowerCase().equals("0")
					|| doubleValue.trim().toLowerCase().equals("off"))
				return false;
			else
				throw new Exception("Invalid Write Value!");

		}
		throw new Exception("Invalid Write Value!");
	}

	protected void initialize(IEC101Master iec101Master) {
		this.iec101Master = iec101Master;
		try {
			iec101Master.init(vo.getGiRelativePeriod());
		} catch (Exception e) {
			raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, new Date().getTime(), true,
					new LocalizableMessage("event.exception2", vo.getName(), e
							.getMessage()));
			e.printStackTrace();
		}
		super.initialize();
	}

	@Override
	public void terminate() {
		super.terminate();
		try {
			iec101Master.terminate();
		} catch (Exception e) {
			raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, new Date().getTime(), true,
					new LocalizableMessage("event.exception2", vo.getName(), e
							.getMessage()));
			e.printStackTrace();
		}
	}

}
