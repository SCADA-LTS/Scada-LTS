package com.serotonin.mango.rt.event.handlers;

import javax.script.ScriptException;

import br.org.scadabr.vo.scripting.ScriptVO;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.type.SystemEventType;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.web.i18n.LocalizableMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.ScriptService;

import java.util.function.IntSupplier;

import static com.serotonin.mango.util.LoggingScriptUtils.*;

public class ScriptHandlerRT extends EventHandlerRT {

	private static final Log LOG = LogFactory.getLog(ScriptHandlerRT.class);

	public ScriptHandlerRT(EventHandlerVO vo) {
		super(vo, SystemEventType.duplicateIgnoreEventType(SystemEventType.TYPE_SCRIPT_HANDLER_FAILURE, vo.getId()));
	}

	@Override
	public void eventInactive(EventInstance evt) {
		executeScript(evt, vo::getInactiveScriptCommand, getVo());
	}

	@Override
	public void eventRaised(EventInstance evt) {
		executeScript(evt, vo::getActiveScriptCommand, getVo());
	}

	private void executeScript(EventInstance evt, IntSupplier scriptCommand, EventHandlerVO vo) {
		if(scriptCommand.getAsInt() == Common.NEW_ID || scriptCommand.getAsInt() == 0) {
			LOG.warn("Script not set: " + generateContext(evt, vo));
			return;
		}
		ScriptVO<?> script;
		try {
			script = new ScriptService().getScript(scriptCommand.getAsInt());
		} catch (Exception ex) {
			String msg = infoErrorInitializationScript(ex, vo, evt);
			LOG.warn(msg);
			LocalizableMessage message = new LocalizableMessage("event.script.failure", infoErrorInitializationScript(null, vo, evt), ex.getMessage());
			SystemEventType.raiseEvent(getEventType(), System.currentTimeMillis(), true, message);
			return;
		}
		if (script != null) {
			try {
				script.createScriptRT().execute();
				SystemEventType.returnToNormal(getEventType(), System.currentTimeMillis());
			} catch (ScriptException e) {
				LOG.warn(infoErrorExecutionScript(e, script), e);
				LocalizableMessage message = new LocalizableMessage("event.script.failure", infoErrorExecutionScript(null, script), e.getMessage());
				SystemEventType.raiseEvent(getEventType(), System.currentTimeMillis(), true, message);
			} catch (Exception e) {
				LOG.warn(infoErrorExecutionScript(e, script));
				LocalizableMessage message = new LocalizableMessage("event.script.failure", infoErrorExecutionScript(null, script), e.getMessage());
				SystemEventType.raiseEvent(getEventType(), System.currentTimeMillis(), true, message);
			}
		} else {
			String msg = infoErrorInitializationScript(null, vo, evt);
			LOG.warn(msg);
			LocalizableMessage message = new LocalizableMessage("event.script.failure", msg, "-||-");
			SystemEventType.raiseEvent(getEventType(), System.currentTimeMillis(), true, message);
		}
	}

}
