package org.scada_lts.dao;

import br.org.scadabr.vo.scripting.ScriptVO;
import org.scada_lts.web.mvc.api.json.JsonScript;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

public interface IScriptDAO {
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    int insert(ScriptVO<?> vo);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    void update(ScriptVO<?> vo);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    void delete(int id);

    ScriptVO<?> getScript(int id);

    List<ScriptVO<?>> getScripts();

    ScriptVO<?> getScript(String xid);

    String generateUniqueXid();

    boolean isXidUnique(String xid, int excludeId);

    JsonScript findScriptsByPage(String query);
}
