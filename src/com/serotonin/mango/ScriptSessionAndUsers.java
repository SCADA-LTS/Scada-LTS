package com.serotonin.mango;

import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.Container;
import org.directwebremoting.ServerContextFactory;
import org.directwebremoting.extend.ScriptSessionManager;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * This class is responsible for collect collection eeg.:
 * <b>key</b>(scriptSessionId)-<b>value</b>(BusinessObject) under <b>key</b>sessionId in context.
 *
 * <s>ScriptSessionAndUsers responsible for managing  User copies specific given browser "tab" (DWR Script Session)
 * User copies are stored in {@value #SCRIPTSESSION_USER} attribute of DWR script session</s>
 *
 * @author Mateusz Hyski mateusz.hyski@softq.pl hyski.mateusz@gmail.com
 *
 * In next commit it will called ScriptSessions not ScriptSessionAndUsers
 */
public class ScriptSessionAndUsers {

    private static final Log LOG = LogFactory.getLog(ScriptSessionAndUsers.class);

    /**
     * !!! in next commit it won't be used
     *
     * {@value #SCRIPTSESSION_USER} is the attribute name under session
     */
    private static final String SCRIPTSESSION_USER = "dwrscriptsessionuser";
    public static User getUserForScriptSessionId(String scriptSessionId, HttpServletRequest request){
        User user = null;
        try {
            Container container = ServerContextFactory.get(request.getServletContext()).getContainer();
            ScriptSessionManager manager = (ScriptSessionManager) container.getBean(ScriptSessionManager.class.getName());

            user = (User) manager.getScriptSession(scriptSessionId).getAttribute(SCRIPTSESSION_USER);
        }catch (Exception e){
            LOG.warn("Could not retrieve user for dwr script session");
        }
        return (user!=null) ? user
                : Common.getUser(request);
    }
    /**
     * !!! in next commit it won't be used
     *
     * @param request HttpServletRequest
     * @return ScriptSessionManager
     */
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
     * @param scriptSessionId String
     * @param request HttpServletRequest
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

    /**
     * put new dependency eeg: <b>key</b>scriptSessionId and <b>value</b>businessObject like Point,View
     * under sessionId in context
     *
     * @param objectEegPointWatchListAndSoOn
     * @param sessionId
     * @param scriptSessionId
     * @return boolean
     */
    public synchronized static boolean addNewEditedObjectForScriptSession(Object objectEegPointWatchListAndSoOn,String sessionId,  String scriptSessionId){
        boolean result = Boolean.FALSE;
        if(sessionId !=null && !sessionId.isEmpty() &&
            objectEegPointWatchListAndSoOn !=null &&
            scriptSessionId !=null && !scriptSessionId.isEmpty()
        ) {
            if (Common.getServletContext().getAttribute(sessionId) != null) {
                ((Map<String, Object>) Common.getServletContext().getAttribute(sessionId)).put(scriptSessionId, objectEegPointWatchListAndSoOn);
            } else {
                Map<String, Object> scriptSessionVSBusinessObject = new HashMap<String, Object>();
                scriptSessionVSBusinessObject.put(scriptSessionId, objectEegPointWatchListAndSoOn);
                Common.getServletContext().setAttribute(sessionId, scriptSessionVSBusinessObject);
            }
            result= Boolean.TRUE;
        }
        else
        {
            LOG.error("Any of parameters (sessionId,objectEegPointWatchListAndSoOn,scriptSessionId) cannot be empty");
        }
        return result;
    }

    /**
     * Returns business object like eventdetector which is collect under concrete scriptsessionid
     * in map. Mentioned map is saved under sessionid in context.
     *
     * @param sessionId String
     * @param scriptSessionId String
     * @return Object eeg. View,EventDetector....
     */
    public synchronized static Object getObjectForScriptSession(String sessionId, String scriptSessionId){

        if(sessionId !=null && !sessionId.isEmpty() &&
            scriptSessionId !=null && !scriptSessionId.isEmpty()
        ) {
            if (Common.getServletContext().getAttribute(sessionId)!=null) {
                if(((Map<String, Object>) Common.getServletContext().getAttribute(sessionId)).get(scriptSessionId)!=null){
                    return ((Map<String, Object>) Common.getServletContext().getAttribute(sessionId)).get(scriptSessionId);
                }
                else
                    return null;

            }
            else
                return null;
        }
        else
            {
            LOG.error("Any of parameters (sessionId,objectEegPointWatchListAndSoOn,scriptSessionId) cannot be empty");
            }
        return null;
    }

    /**
     *remove dependency, it means <b>key</b>sessionId - <b>value</b> ->map(scriptsession - business object) which exists in context
     *
     * @param sessionId String
     * @return boolean
     */
    public synchronized static boolean clearSessionIdFromContext(String sessionId){
        boolean result = Boolean.FALSE;
        if(sessionId!=null && !sessionId.isEmpty()) {
            if (Common.getServletContext().getAttribute(sessionId) != null) {
                Common.getServletContext().removeAttribute(sessionId);
                result = Boolean.TRUE;
            }
        }
        else
        {
            LOG.error("Parameter sessionId cannot be null or empty");
        }
        return  result;
    }

    /**
     * remove ONLY existing key scriptSessionId from map which exist under key sessionId in context
     *
     * @param sessionId String
     * @param scriptSessionId String
     * @return boolean
     */
    public synchronized static boolean removeScriptSessionForObjectBySessionIdAndScriptSessionId(String sessionId, String scriptSessionId){

        boolean result = Boolean.FALSE;
        if(sessionId!=null
            && !sessionId.isEmpty()
            && scriptSessionId!=null
            && !scriptSessionId.isEmpty()) {
            if (Common.getServletContext().getAttribute(sessionId) != null) {
                if (((Map<String, Object>) Common.getServletContext().getAttribute(sessionId)).containsKey(scriptSessionId)) {
                    ((Map<String, Object>) Common.getServletContext().getAttribute(sessionId)).remove(scriptSessionId);
                    result = Boolean.TRUE;
                }
            }
        }
        else
        {
            LOG.error("Parameter sessionId and scriptSessionId cannot be null or empty");
        }

        return result;

    }
}
