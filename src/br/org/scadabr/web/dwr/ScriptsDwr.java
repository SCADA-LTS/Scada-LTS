package br.org.scadabr.web.dwr;

import java.util.List;

import br.org.scadabr.rt.scripting.ScriptRT;
import br.org.scadabr.vo.scripting.ContextualizedScriptVO;
import br.org.scadabr.vo.scripting.ScriptVO;

import com.serotonin.db.IntValuePair;
import com.serotonin.mango.Common;
import org.scada_lts.mango.service.ServiceInstances;
import com.serotonin.mango.vo.DataPointExtendedNameComparator;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.web.dwr.BaseDwr;
import com.serotonin.web.dwr.DwrResponseI18n;

public class ScriptsDwr extends BaseDwr {

	public List<DataPointVO> getPoints() {
		return ServiceInstances.DataPointService.getDataPoints(
				DataPointExtendedNameComparator.instance, false);
	}

	public List<ScriptVO<?>> getScripts() {
		return ServiceInstances.ScriptService.getScripts();
	}

	public ScriptVO<?> getScript(int id) {
		if (id == Common.NEW_ID) {
			ContextualizedScriptVO vo = new ContextualizedScriptVO();
			vo.setXid(ServiceInstances.ScriptService.generateUniqueXid());
			return vo;
		}

		return ServiceInstances.ScriptService.getScript(id);
	}

	public DwrResponseI18n saveScript(int id, String xid, String name,
			String script, List<IntValuePair> pointsOnContext,
			List<IntValuePair> objectsOnContext) {

		ContextualizedScriptVO vo = new ContextualizedScriptVO();
		vo.setId(id);
		vo.setXid(xid);
		vo.setName(name);
		vo.setScript(script);
		vo.setPointsOnContext(pointsOnContext);
		vo.setObjectsOnContext(objectsOnContext);
		vo.setUserId(Common.getUser().getId());

		DwrResponseI18n response = new DwrResponseI18n();

		vo.validate(response);

		if (!response.getHasMessages())
			ServiceInstances.ScriptService.saveScript(vo);

		response.addData("seId", vo.getId());
		return response;
	}

	public void deleteScript(int scriptId) {
		ServiceInstances.ScriptService.deleteScript(scriptId);
	}

	public boolean executeScript(int scriptId) {
		ScriptVO<?> script = ServiceInstances.ScriptService.getScript(scriptId);

		try {
			if (script != null) {
				ScriptRT rt = script.createScriptRT();
				rt.execute();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
}
