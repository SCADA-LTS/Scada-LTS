package br.org.scadabr.db.dao;

import java.util.List;

import org.scada_lts.mango.adapter.MangoScript;
import org.scada_lts.mango.service.ScriptService;

import br.org.scadabr.vo.scripting.ScriptVO;

@Deprecated
public class ScriptDao {

	MangoScript scriptService = new ScriptService();

	public void saveScript(final ScriptVO<?> vo) {
		scriptService.saveScript(vo);
	}

	public void deleteScript(final int scriptId) {
		scriptService.deleteScript(scriptId);
	}

	public ScriptVO<?> getScript(int id) {
		return scriptService.getScript(id);
	}

	public List<ScriptVO<?>> getScripts() {
		return scriptService.getScripts();
	}

	public String generateUniqueXid() {
		return scriptService.generateUniqueXid();
	}

	public boolean isXidUnique(String xid, int excludeId) {
		return scriptService.isXidUnique(xid, excludeId);
	}

	public ScriptVO<?> getScript(String xid) {
		return scriptService.getScript(xid);
	}

}
