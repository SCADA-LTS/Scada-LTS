package org.scada_lts.mango.adapter;

import br.org.scadabr.vo.scripting.ScriptVO;

import java.util.List;

public interface MangoScript {

    void saveScript(final ScriptVO<?> vo);

    void deleteScript(final int scriptId);

    ScriptVO<?> getScript(int id);

    List<ScriptVO<?>> getScripts();

    ScriptVO<?> getScript(String xid);

    String generateUniqueXid();

    boolean isXidUnique(String xid, int excludeId);
}
