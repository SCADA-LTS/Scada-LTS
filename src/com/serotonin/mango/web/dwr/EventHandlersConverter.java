package com.serotonin.mango.web.dwr;

import br.org.scadabr.vo.scripting.ScriptVO;
import com.serotonin.mango.vo.event.EventHandlerVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.scada_lts.dao.ScriptDAO;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Deprecated
class EventHandlersConverter {

    static void setXidForHandlers(List<EventHandlerVO> handlers) {
        if(CollectionUtils.isEmpty(handlers))
            return;
        List<ScriptVO<?>> scripts =  new ScriptDAO().getScripts();

        setXidForHandlers(handlers, scripts);
    }

    private static void setXidForHandlers(List<EventHandlerVO> handlers, List<ScriptVO<?>> scripts) {
        if(CollectionUtils.isEmpty(handlers) || CollectionUtils.isEmpty(scripts))
            return;
        Map<Integer, ScriptVO<?>> scriptsMap = toMap(scripts);
        handlers.stream().peek(handler -> {
            setXidForHandlers(scriptsMap, handler::getActiveScriptCommand, handler::getActiveScriptCommandXid,
                    handler::setActiveScriptCommandXid);
            setXidForHandlers(scriptsMap, handler::getInactiveScriptCommand, handler::getInactiveScriptCommandXid,
                    handler::setInactiveScriptCommandXid);
        }).close();
    }

    private static Map<Integer, ScriptVO<?>> toMap(List<ScriptVO<?>> scripts) {
        return scripts.stream()
                .collect(Collectors.toMap(ScriptVO::getId, script -> script));
    }

    private static void setXidForHandlers(Map<Integer, ScriptVO<?>> scripts, Supplier<Integer> handlerGetScriptId,
                                          Supplier<String> handlerGetScriptXid, Consumer<String> handlerSetScriptXid) {
        String handlerScriptXid = handlerGetScriptXid.get();
        int handlerScriptId = handlerGetScriptId.get();
        if(StringUtils.isBlank(handlerScriptXid) && scripts.containsKey(handlerScriptId)) {
            ScriptVO<?> script = scripts.get(handlerScriptId);
            String scriptXid = script.getXid();
            handlerSetScriptXid.accept(scriptXid);
        }
    }

}
