package com.serotonin.mango.rt.event.handlers;

import javax.script.ScriptException;

import br.org.scadabr.vo.scripting.ScriptVO;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.event.EventHandlerVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.ScriptService;

import static com.serotonin.mango.util.LoggingScriptUtils.*;

public class ScriptHandlerRT extends EventHandlerRT {

	private static final Log LOG = LogFactory.getLog(ScriptHandlerRT.class);

	public ScriptHandlerRT(EventHandlerVO vo) {
		this.vo = vo;
	}

	@Override
	public void eventInactive(EventInstance evt) {
		if(vo.getInactiveScriptCommand() == Common.NEW_ID) {
			LOG.trace("Inactive script not set: " + generateContext(evt, vo));
			return;
		}
		ScriptVO<?> script;
		try {
			script = new ScriptService().getScript(vo
					.getInactiveScriptCommand());
		} catch (Exception ex) {
			LOG.warn(infoErrorInitializationScript(ex, vo, evt));
			return;
		}
		if (script != null) {
			try {
				script.createScriptRT().execute();
			} catch (ScriptException e) {
				LOG.warn(infoErrorExecutionScript(e, script), e);
			} catch (Exception e) {
				LOG.warn(infoErrorExecutionScript(e, script));
			}
		} else {
			LOG.warn(infoErrorInitializationScript(null, vo, evt));
		}
	}

	@Override
	public void eventRaised(EventInstance evt) {
		if(vo.getActiveScriptCommand() == Common.NEW_ID) {
			LOG.trace("Active script not set: " + generateContext(evt, vo));
			return;
		}
		ScriptVO<?> script;
		try {
			script = new ScriptService().getScript(vo
					.getActiveScriptCommand());
		} catch (Exception ex) {
			LOG.warn(infoErrorInitializationScript(ex, vo, evt));
			return;
		}
		if (script != null) {
			try {
				script.createScriptRT().execute();
			} catch (ScriptException e) {
				LOG.warn(infoErrorExecutionScript(e, script), e);
			} catch (Exception e) {
				LOG.warn(infoErrorExecutionScript(e, script));
			}
		} else {
			LOG.warn(infoErrorInitializationScript(null, vo, evt));
		}
	}

}
