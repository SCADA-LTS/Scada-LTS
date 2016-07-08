package br.org.scadabr.rt.scripting.context;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.DataSourceDao;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.permission.Permissions;

public class DSCommandsScriptContextObject extends ScriptContextObject {
	public static final Type TYPE = Type.DATASOURCE_COMMANDS;

	@Override
	public Type getType() {
		return TYPE;
	}

	public void enableDataSource(String xid) {
		RuntimeManager runtimeManager = Common.ctx.getRuntimeManager();
		DataSourceVO<?> dataSource = new DataSourceDao().getDataSource(xid);
		if (dataSource != null) {
			Permissions.ensureDataSourcePermission(Common.getUser(), dataSource
					.getId());
			dataSource.setEnabled(true);
			runtimeManager.saveDataSource(dataSource);
		}

	}

	public void disableDataSource(String xid) {
		RuntimeManager runtimeManager = Common.ctx.getRuntimeManager();
		DataSourceVO<?> dataSource = new DataSourceDao().getDataSource(xid);
		if (dataSource != null) {
			Permissions.ensureDataSourcePermission(Common.getUser(), dataSource
					.getId());
			dataSource.setEnabled(false);
			runtimeManager.saveDataSource(dataSource);
		}

	}
}
