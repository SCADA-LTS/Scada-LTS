package br.org.scadabr.web.dwr;

import java.util.ArrayList;
import java.util.List;

import br.org.scadabr.rt.scripting.ScriptRT;
import br.org.scadabr.vo.scripting.ContextualizedScriptVO;
import br.org.scadabr.vo.scripting.ScriptVO;

import com.serotonin.db.IntValuePair;
import com.serotonin.mango.Common;
import com.serotonin.mango.vo.DataPointExtendedNameComparator;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.mango.web.dwr.BaseDwr;
import com.serotonin.mango.web.dwr.beans.DataPointBean;
import com.serotonin.web.dwr.DwrResponseI18n;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.ScriptService;
import org.scada_lts.utils.GetDataPointsUtils;

import static com.serotonin.mango.util.LoggingScriptUtils.infoErrorExecutionScript;

public class ScriptsDwr extends BaseDwr {

	private static final Log LOG = LogFactory.getLog(ScriptsDwr.class);


	@Deprecated(since = "2.8.0")
	public List<DataPointVO> getPoints1() {
		List<DataPointVO> allPoints = new DataPointService().getDataPoints(
				DataPointExtendedNameComparator.instance, false);
		return allPoints;
	}

	public List<ScriptVO<?>> getScripts() {
		List<ScriptVO<?>> scripts = new ScriptService().getScripts();
		return scripts;
	}

	public DwrResponseI18n getScript(int id) {
		DwrResponseI18n response = new DwrResponseI18n();

		User user = Common.getUser();
		ScriptService scriptService = new ScriptService();

		ScriptVO<?> scriptVO;
		if (id == Common.NEW_ID) {
			scriptVO = new ContextualizedScriptVO();
			scriptVO.setXid(scriptService.generateUniqueXid());
		} else {
			scriptVO = scriptService.getScript(id);
		}

		List<DataPointBean> dataPoints = new ArrayList<>();
		if(scriptVO instanceof ContextualizedScriptVO) {
			dataPoints.addAll(GetDataPointsUtils.getDataPointsByScript(user, (ContextualizedScriptVO) scriptVO, new DataPointService()));
		}
		response.addData("script",scriptVO);
		response.addData("dataPoints", dataPoints);
		return response;
	}

	public DwrResponseI18n saveScript(int id, String xid, String name,
			String script, List<IntValuePair> pointsOnContext,
			List<IntValuePair> objectsOnContext) {
		Permissions.ensureAdmin();
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
			new ScriptService().saveScript(vo);

		response.addData("seId", vo.getId());
		return response;
	}

	public void deleteScript(int scriptId) {
		Permissions.ensureAdmin();
		new ScriptService().deleteScript(scriptId);
	}

	public boolean executeScript(int scriptId) {
		ScriptVO<?> script = new ScriptService().getScript(scriptId);

		try {
			if (script != null) {
				ScriptRT rt = script.createScriptRT();
				rt.execute();
				return true;
			}
		} catch (Exception e) {
			LOG.warn(infoErrorExecutionScript(e,script), e);
		}

		return false;
	}
}
