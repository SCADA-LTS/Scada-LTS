package com.serotonin.mango.rt.event.handlers;

import javax.script.ScriptException;

import br.org.scadabr.db.dao.ScriptDao;
import br.org.scadabr.vo.scripting.ScriptVO;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.event.EventHandlerVO;

public class ScriptHandlerRT extends EventHandlerRT {

	public ScriptHandlerRT(EventHandlerVO vo) {
		this.vo = vo;
	}

	@Override
	public void eventInactive(EventInstance evt) {
		ScriptVO<?> script = new ScriptDao().getScript(vo
				.getInactiveScriptCommand());
		if (script != null) {
			try {
				script.createScriptRT().execute();
			} catch (ScriptException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void eventRaised(EventInstance evt) {
		ScriptVO<?> script = new ScriptDao().getScript(vo
				.getActiveScriptCommand());
		if (script != null) {
			try {
				script.createScriptRT().execute();
			} catch (ScriptException e) {
				e.printStackTrace();
			}
		}
	}

}
