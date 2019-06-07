package br.org.scadabr.rt.scripting;

import javax.script.ScriptException;

import br.org.scadabr.vo.scripting.ScriptVO;

import com.serotonin.util.ILifecycle;
import com.serotonin.util.LifecycleException;

abstract public class ScriptRT implements ILifecycle {

	abstract public void execute() throws ScriptException;

	protected final ScriptVO<?> vo;

	public ScriptRT(ScriptVO<?> vo) {
		this.vo = vo;
	}

	public int getId() {
		return vo.getId();
	}

	public String getName() {
		return vo.getName();
	}

	public String getScript() {
		return vo.getScript();
	}

	@Override
	public void initialize() throws LifecycleException {
	}

	@Override
	public void joinTermination() {

	}

	@Override
	public void terminate() throws LifecycleException {

	}

}
