package com.serotonin.mango;

import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.Container;
import org.directwebremoting.ServerContextFactory;
import org.directwebremoting.extend.ScriptSessionManager;
import org.scada_lts.web.mvc.controller.FinalVariablesForControllers;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * ScriptSessionAndUsers responsible for managing  User copies specific given browser "tab" (DWR Script Session)
 * User copies are stored in {@value #SCRIPTSESSION_USER} attribute of DWR script session
 *
 * @author Mateusz Hyski mateusz.hyski@softq.pl
 */
public class ScriptSessionAndUsers {

    private static final Log LOG = LogFactory.getLog(ScriptSessionAndUsers.class);

    /**
     * {@value #SCRIPTSESSION_USER} is the attribute name under session
     */
    public static final String SCRIPTSESSION_USER = FinalVariablesForControllers.DWRSCRIPTSESSIONUSER;


    public synchronized static boolean removeScriptSessionVsObjectBySessionIdAndScriptSessionId(String sessionId, String scriptSessionId){

        boolean result = Boolean.FALSE;

            if (Common.getCtx().getAttribute(sessionId) != null) {
                if (((Map<String, Object>) Common.getCtx().getAttribute(sessionId)).containsKey(scriptSessionId)) {
                    ((Map<String, Object>) Common.getCtx().getAttribute(sessionId)).remove(scriptSessionId);
                    result = Boolean.TRUE;
                }
            }

        return result;

    }
    private static ScriptSessionManager getScriptSessionManager(HttpServletRequest request){
        ScriptSessionManager manager = null;
        try {
            Container container = ServerContextFactory.get(request.getServletContext()).getContainer();
            manager = (ScriptSessionManager) container.getBean(ScriptSessionManager.class.getName());
        }catch (Exception e){
            LOG.warn("Could not retrieve user for dwr script session");
        }
        return manager;
    }
    /**
     * Returns correct User from Script Session
     *
     * @param scriptSessionId
     * @param request
     * @return User
     */
    public static User getUserFromScriptSessionManagerSavedUnderDWRSCRIPTSESSIONUSER(String scriptSessionId, HttpServletRequest request){
        User user = null;
        try {
            user = (User) getScriptSessionManager(request).getScriptSession(scriptSessionId).getAttribute(SCRIPTSESSION_USER);
        }catch (Exception e){
            LOG.warn("Could not retrieve user for dwr script session");
        }
        return (user!=null) ? user
                            : Common.getUser(request);
    }
    public static void addNewScriptSessionVsObjectUnderGivenSessionId(String sessionId, Object objectEegPointWatchListAndSoOn, String scriptSessionId){
        if(sessionId !=null && !sessionId.isEmpty() &&
            objectEegPointWatchListAndSoOn !=null &&
            scriptSessionId !=null && !scriptSessionId.isEmpty()
        ) {
            if (Common.getCtx().getAttribute(sessionId) != null) {
                ((Map<String, Object>) Common.getCtx().getAttribute(sessionId)).put(scriptSessionId, objectEegPointWatchListAndSoOn);
            } else {
                Map<String, Object> scriptSessionVSBusinessObject = new HashMap<String, Object>();
                scriptSessionVSBusinessObject.put(scriptSessionId, objectEegPointWatchListAndSoOn);
                Common.getCtx().setAttribute(sessionId, scriptSessionVSBusinessObject);
            }
        }
        else
        {
            try {
                throw new Exception("Any of parameters (sessionId,objectEegPointWatchListAndSoOn,scriptSessionId) cannot be empty");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    public static Object getObjectVsScriptSession(String sessionId, String scriptSessionId){

        if(sessionId !=null && !sessionId.isEmpty() &&
            scriptSessionId !=null && !scriptSessionId.isEmpty()
        ) {
            if (Common.getCtx().getAttribute(sessionId)!=null) {
                if(((Map<String, Object>) Common.getCtx().getAttribute(sessionId)).get(scriptSessionId)!=null){
                    return ((Map<String, Object>) Common.getCtx().getAttribute(sessionId)).get(scriptSessionId);
                }
                else
                    return null;
            }
            else
                return null;
        }
        else
            try {
                throw new Exception("Any of parameters (sessionId,objectEegPointWatchListAndSoOn,scriptSessionId) cannot be empty");
            } catch (Exception e) {
                LOG.error(e);
            }
            return null;

    }
    public static void removeAllUnderSessionIdAttributeFromCommonContext(String sessionId){
        if(sessionId!=null && !sessionId.isEmpty()) {
            if (Common.getCtx().getAttribute(sessionId) != null) {
                Common.getCtx().removeAttribute(sessionId);
            }
        }
        else
            try {
                throw new Exception("Parameter sessionId cannot be null or empty");
            } catch (Exception e) {
                LOG.warn(e.getMessage());
            }

    }
    public static void aa(HttpServletRequest request,String scriptSessionId){
        Set<String> scriptSessionsForWebSession;

        if( Common.getCtx().getAttribute(SCRIPTSESSION_USER)==null) {
            (scriptSessionsForWebSession = new HashSet<String>()).add(scriptSessionId);
            Common.getCtx().setAttribute(SCRIPTSESSION_USER, scriptSessionsForWebSession);
        }
        else {
            scriptSessionsForWebSession = (Set<String>) Common.getCtx().getAttribute(SCRIPTSESSION_USER);
        }

        scriptSessionsForWebSession.add(scriptSessionId);

        addAttributeAndValueIntoCommonContextWithSyncProtection(SCRIPTSESSION_USER,scriptSessionsForWebSession);

    }
    private synchronized static void addAttributeAndValueIntoCommonContextWithSyncProtection(String attribute,Object value){

            Common.getCtx().removeAttribute(attribute);
            Common.getCtx().setAttribute(attribute, value);

    }


}
