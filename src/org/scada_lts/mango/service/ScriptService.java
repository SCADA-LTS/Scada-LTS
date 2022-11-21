package org.scada_lts.mango.service;

import br.org.scadabr.vo.scripting.ScriptVO;
import com.serotonin.mango.Common;
import org.scada_lts.dao.ScriptDAO;
import org.scada_lts.mango.adapter.MangoScript;

import java.util.List;

public class ScriptService implements MangoScript {

    private ScriptDAO scriptDAO = new ScriptDAO();

    @Override
    public void saveScript(final ScriptVO<?> vo) {
        if (vo.getId() == Common.NEW_ID)
            vo.setId(scriptDAO.insert(vo));
        else
            scriptDAO.update(vo);
    }

    @Override
    public void deleteScript(final int scriptId) {
        scriptDAO.delete(scriptId);
    }

    @Override
    public ScriptVO<?> getScript(int id) {
        return scriptDAO.getScript(id);
    }

    @Override
    public List<ScriptVO<?>> getScripts() {
        return scriptDAO.getScripts();
    }


    @Override
    public ScriptVO<?> getScript(String xid) {
        return scriptDAO.getScript(xid);
    }

    @Override
    public String generateUniqueXid() {
        return scriptDAO.generateUniqueXid();
    }

    @Override
    public boolean isXidUnique(String xid, int excludeId) {
        return scriptDAO.isXidUnique(xid, excludeId);
    }
}
