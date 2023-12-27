package br.org.scadabr.rt.scripting.context;

import com.serotonin.mango.vo.*;
import br.org.scadabr.rt.scripting.context.properties.DataPointDiscardValuesProperties;
import br.org.scadabr.rt.scripting.context.properties.DataPointLoggingTypeProperties;
import br.org.scadabr.rt.scripting.context.properties.DataPointPurgeTypeProperties;
import br.org.scadabr.rt.scripting.context.properties.DataPointUpdate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.vo.permission.Permissions;
import org.mozilla.javascript.NativeObject;

public class DPCommandsScriptContextObject extends ScriptContextObject {
	public static final Type TYPE = Type.DATAPOINT_COMMANDS;
	private Log LOG = LogFactory.getLog(DPCommandsScriptContextObject.class);

	@Override
	public Type getType() {
		return TYPE;
	}

	public void writeDataPoint(String xid, String stringValue) {
		DataPointVO dataPoint = new DataPointDao().getDataPoint(xid);
		if (dataPoint != null) {
			Permissions.ensureDataPointSetPermission(user, dataPoint);
			RuntimeManager runtimeManager = Common.ctx.getRuntimeManager();
			MangoValue value = MangoValue.stringToValue(stringValue, dataPoint
					.getPointLocator().getDataTypeId());
			LOG.debug("Start set point call...");
			try {
				runtimeManager.setDataPointValue(dataPoint.getId(), value,
						this.user);
				LOG.debug("Wrote the point!");
			} catch (Exception e) {
				LOG.debug("Error while setting point - " + e.getMessage());
			}
		}
	}

	public void enableDataPoint(String xid) {
		DataPointVO dataPoint = new DataPointDao().getDataPoint(xid);
		if (dataPoint != null) {
			Permissions.ensureDataPointReadPermission(user, dataPoint);
			RuntimeManager runtimeManager = Common.ctx.getRuntimeManager();
			dataPoint.setEnabled(true);
			runtimeManager.saveDataPoint(dataPoint);
		}

	}

	public void disableDataPoint(String xid) {
		DataPointVO dataPoint = new DataPointDao().getDataPoint(xid);
		if (dataPoint != null) {
			Permissions.ensureDataPointReadPermission(user, dataPoint);
			RuntimeManager runtimeManager = Common.ctx.getRuntimeManager();
			dataPoint.setEnabled(false);
			runtimeManager.saveDataPoint(dataPoint);
		}

	}

	public void setLoggingTypeAll(String xid) {
		updateDataPoint(xid, DataPointLoggingTypeProperties.all());
	}

	public void setLoggingTypeInterval(String xid, String intervalLoggingPeriodType, int intervalLoggingPeriod,
									   String intervalLoggingType) {
		if(intervalLoggingPeriod <= 0) {
			throw new IllegalArgumentException("intervalLoggingPeriod must be > 0");
		}
		DataPointUpdate dataPointUpdate;
		try {
			IntervalLoggingPeriodType loggingPeriodType = IntervalLoggingPeriodType.valueOf(intervalLoggingPeriodType.toUpperCase());
			IntervalLoggingType loggingType = IntervalLoggingType.valueOf(intervalLoggingType.toUpperCase());
			dataPointUpdate = DataPointLoggingTypeProperties.interval(loggingPeriodType, intervalLoggingPeriod, loggingType);
		} catch (Exception ex) {
			throw new IllegalArgumentException(ex.getMessage());
		}
		updateDataPoint(xid, dataPointUpdate);
	}

	public void setLoggingTypeNone(String xid) {
		updateDataPoint(xid, DataPointLoggingTypeProperties.none());
	}

	public void setLoggingTypeOnChange(String xid, double tolerance) {
		if(tolerance < 0) {
			throw new IllegalArgumentException("tolerance must be >= 0.0");
		}
		updateDataPoint(xid, DataPointLoggingTypeProperties.onChange(tolerance));
	}

	public void setLoggingTypeOnTsChange(String xid) {
		updateDataPoint(xid, DataPointLoggingTypeProperties.onTsChange());
	}

	public void setLoggingType(String xid, NativeObject object) {
		DataPointUpdate dataPointUpdate;
		try {
			dataPointUpdate = DataPointLoggingTypeProperties.byNativeObject(object);
		} catch (Exception ex) {
			throw new IllegalArgumentException(ex.getMessage());
		}
		updateDataPoint(xid, dataPointUpdate);
	}

	public void setPurgeType(String xid, NativeObject object) {
		DataPointUpdate dataPointUpdate;
		try {
			dataPointUpdate = DataPointPurgeTypeProperties.byNativeObject(object);
		} catch (Exception ex) {
			throw new IllegalArgumentException(ex.getMessage());
		}
		updateDataPoint(xid, dataPointUpdate);
	}

	public void setDiscardValues(String xid, NativeObject object) {
		DataPointUpdate dataPointUpdate;
		try {
			dataPointUpdate = DataPointDiscardValuesProperties.byNativeObject(object);
		} catch (Exception ex) {
			throw new IllegalArgumentException(ex.getMessage());
		}
		updateDataPoint(xid, dataPointUpdate);
	}

	public void setDefaultCacheSize(String xid, int defaultCacheSize) {
		if(defaultCacheSize < 0) {
			throw new IllegalArgumentException("defaultCacheSize must be > 0");
		}
		updateDataPoint(xid, dataPointVO -> dataPointVO.setDefaultCacheSize(defaultCacheSize));
	}

	private void updateDataPoint(String xid, DataPointUpdate dataPointUpdate) {
		DataPointVO dataPoint = new DataPointDao().getDataPoint(xid);
		if (dataPoint != null) {
			Permissions.ensureDataPointUpdatePermission(user, dataPoint);
			RuntimeManager runtimeManager = Common.ctx.getRuntimeManager();
			dataPointUpdate.updateDataPoint(dataPoint);
			runtimeManager.saveDataPoint(dataPoint);
		}
	}
}
