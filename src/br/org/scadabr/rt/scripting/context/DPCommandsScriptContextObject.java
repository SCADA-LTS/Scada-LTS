package br.org.scadabr.rt.scripting.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.permission.Permissions;

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
}
