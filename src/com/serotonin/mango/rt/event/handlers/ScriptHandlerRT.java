package com.serotonin.mango.rt.event.handlers;

import javax.script.ScriptException;

import br.org.scadabr.db.dao.ScriptDao;
import br.org.scadabr.vo.scripting.ScriptVO;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.event.EventHandlerVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static com.serotonin.mango.util.LoggingScriptUtils.infoErrorExecutionScript;
import static com.serotonin.mango.util.LoggingScriptUtils.infoErrorInitializationScript;

public class ScriptHandlerRT extends EventHandlerRT {

	private static final Log LOG = LogFactory.getLog(ScriptHandlerRT.class);

	public ScriptHandlerRT(EventHandlerVO vo) {
		this.vo = vo;
	}

	@Override
	public void eventInactive(EventInstance evt) {
		ScriptVO<?> script = null;
		try {
			script = new ScriptDao().getScript(vo
					.getInactiveScriptCommand());
		} catch (Exception ex) {
			LOG.warn(infoErrorInitializationScript(ex, vo, evt));
		}
		if (script != null) {
			try {
				script.createScriptRT().execute();
			} catch (ScriptException e) {
				LOG.warn(infoErrorExecutionScript(e, script), e);
			} catch (Exception e) {
				LOG.warn(infoErrorExecutionScript(e, script));
			}
		}
	}

	@Override
	public void eventRaised(EventInstance evt) {
		ScriptVO<?> script = null;
		try {
			script = new ScriptDao().getScript(vo
					.getActiveScriptCommand());
		} catch (Exception ex) {
			LOG.warn(infoErrorInitializationScript(ex, vo, evt));
			//throw ex;
		}
		if (script != null) {
			try {
				script.createScriptRT().execute();
			} catch (ScriptException e) {
				LOG.warn(infoErrorExecutionScript(e, script), e);
			} catch (Exception e) {
				LOG.warn(infoErrorExecutionScript(e, script));
				//throw e;
			}
		}
	}

}
