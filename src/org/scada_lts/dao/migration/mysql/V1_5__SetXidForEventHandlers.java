package org.scada_lts.dao.migration.mysql;

import com.serotonin.mango.vo.event.EventHandlerVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.scada_lts.dao.batch.BatchProcessing;
import org.scada_lts.dao.SerializationData;
import org.scada_lts.dao.batch.Limit;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class V1_5__SetXidForEventHandlers implements SpringJdbcMigration {

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        TempDAO tempDAO = new TempDAO(jdbcTemplate);
        SetXidForEventHandlersProcess process = new SetXidForEventHandlersProcess(tempDAO);
        BatchProcessing.preprocess(process::setXidForEventHandlersAndUpdate,
                tempDAO::getEventHandlersWithPagination, 200, 1000);
        process.clear();
    }

    private class ScriptPart {
        private final long id;
        private final String xid;

        public ScriptPart(long id, String xid) {
            this.id = id;
            this.xid = xid;
        }

        public long getId() {
            return id;
        }

        public String getXid() {
            return xid;
        }
    }

    private class SetXidForEventHandlersProcess {

        private TempDAO tempDAO;
        private Map<Long, String> scripts;

        public SetXidForEventHandlersProcess(TempDAO tempDAO) {
            this.tempDAO = tempDAO;
        }

        void setXidForEventHandlersAndUpdate(List<EventHandlerVO> handlers) {
            if (CollectionUtils.isEmpty(handlers))
                return;
            Map<Long, String> scripts = findXidForScripts();
            setXidForEventHandlersAndUpdate(handlers, scripts);
        }

        private Map<Long, String> findXidForScripts() {
            if(scripts == null) {
                long max = tempDAO.maxIdScript();
                List<ScriptPart> mapps = tempDAO.findXidForScripts(0, new Limit<>((int)max));
                scripts = toMap(mapps);
            }
            return scripts;
        }


        private void setXidForEventHandlersAndUpdate(List<EventHandlerVO> handlers, Map<Long, String> scriptsMap) {
            if (CollectionUtils.isEmpty(handlers)
                    || MapUtils.isEmpty(scriptsMap))
                return;
            handlers.forEach(handler -> {
                setXidForHandlers(scriptsMap, handler::getActiveScriptCommand, handler::getActiveScriptCommandXid,
                        handler::setActiveScriptCommandXid);
                setXidForHandlers(scriptsMap, handler::getInactiveScriptCommand, handler::getInactiveScriptCommandXid,
                        handler::setInactiveScriptCommandXid);
                tempDAO.updateEventHandler(handler);
            });
        }

        private void setXidForHandlers(Map<Long, String> scripts, Supplier<Integer> handlerGetScriptId,
                                       Supplier<String> handlerGetScriptXid, Consumer<String> handlerSetScriptXid) {
            String handlerScriptXid = handlerGetScriptXid.get();
            long handlerScriptId = (long) handlerGetScriptId.get();
            if (StringUtils.isBlank(handlerScriptXid)
                    && scripts.containsKey(handlerScriptId)) {
                String scriptXid = scripts.get(handlerScriptId);
                handlerSetScriptXid.accept(scriptXid);
            }
        }

        private Map<Long, String> toMap(List<ScriptPart> list) {
            return list.stream().collect(Collectors.toMap(ScriptPart::getId, ScriptPart::getXid));
        }

        public void clear() {
            scripts.clear();
            scripts = null;
            tempDAO = null;
        }
    }

    private class TempDAO {

        private final JdbcTemplate jdbcTemplate;

        TempDAO(JdbcTemplate jdbcTemplate) {
            this.jdbcTemplate = jdbcTemplate;
        }

        List<EventHandlerVO> getEventHandlersWithPagination(long offset, Limit<Integer> limit) {
            List<EventHandlerVO> eventHandlers = jdbcTemplate.query("SELECT * FROM eventhandlers LIMIT ? OFFSET ?",
                    new EventHandlerRowMapper(), limit.get(), offset);
            return eventHandlers;
        }

        long maxIdScript() {
            return jdbcTemplate.queryForObject("SELECT MAX(id) FROM scripts", Long.class);
        }

        void updateEventHandler(EventHandlerVO handler) {
            jdbcTemplate.update("UPDATE eventHandlers SET xid=?, alias=?, data=? WHERE id=?", handler.getXid(),
                    handler.getAlias(),
                    new SerializationData().writeObject(handler),
                    handler.getId());
        }

        List<ScriptPart> findXidForScripts(long offset, Limit<Integer> limit) {
            return jdbcTemplate.query("SELECT id, xid FROM scripts LIMIT ? OFFSET ?", new MapIdXidRowMapper(),
                    limit.get(), offset);
        }

        private class EventHandlerRowMapper implements RowMapper<EventHandlerVO> {
            public EventHandlerVO mapRow(ResultSet result, int rowNum) throws SQLException {
                EventHandlerVO handler = (EventHandlerVO)
                        new SerializationData().readObject(result.getBlob("data").getBinaryStream());
                handler.setId(result.getInt("id"));
                handler.setXid(result.getString("xid"));
                handler.setAlias(result.getString("alias"));
                return handler;
            }
        }

        private class MapIdXidRowMapper implements RowMapper<ScriptPart> {
            public ScriptPart mapRow(ResultSet result, int rowNum) throws SQLException {
                return new ScriptPart(result.getLong("id"),
                        result.getString("xid"));
            }
        }
    }
}
