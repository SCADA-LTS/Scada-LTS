package com.serotonin.mango;

import com.rits.cloning.Cloner;
import com.serotonin.mango.vo.User;
import com.sun.jna.platform.win32.OaIdl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.*;
import org.directwebremoting.extend.RealScriptSession;
import org.directwebremoting.extend.ScriptSessionManager;
import org.directwebremoting.impl.DefaultScriptSession;
import org.scada_lts.web.mvc.controller.FinalVariablesForControllers;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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


    public static boolean removeScriptSessionVsObjectBySessionIdAndScriptSessionId(String sessionId, String scriptSessionId){

        boolean result = Boolean.FALSE;

        Object object = new Object();

        synchronized (object) {
            if (Common.getCtx().getAttribute(sessionId) != null) {
                if (((Map<String, Object>) Common.getCtx().getAttribute(sessionId)).containsKey(scriptSessionId)) {
                    ((Map<String, Object>) Common.getCtx().getAttribute(sessionId)).remove(scriptSessionId);
                    result=Boolean.TRUE;
                }
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
    public static User getUserFromScriptSessionManagerByScriptSessionId(HttpServletRequest request, String scriptsessionid) {
        User user = null;
        try {
            RealScriptSession rss = getScriptSessionManager(request).getScriptSession(scriptsessionid);
            user = (User) rss.getAttribute(scriptsessionid);
        } catch (Exception e) {
            LOG.warn("Could not retrieve user for dwr script session");
        }
        return user;
    }
    /**
     * Gets user from map what is attribute of Script Session
     *
     * @param user
     * @param webContext
     * @return User
     */
    public static User findOrAddScriptSessionUserIntoScriptSessionUnderDwrScriptSessionUser(User user, WebContext webContext){

        ScriptSession scriptSession=null;

        try {
            scriptSession = webContext == null ? null : webContext.getScriptSession();
        }catch (Exception e){
            LOG.warn("We have exception when trying to get scriptSession.");
        }

        if  (scriptSession == null) {
            return  null;
        }else{
            if( Common.getCtx().getAttribute(SCRIPTSESSION_USER)!=null) {
                return getUserFromCommonUnderGivensessionId();
            }
             else if (scriptSession.getAttribute(SCRIPTSESSION_USER) == null){

                User scriptSessionUser = new Cloner().deepClone(user);
                scriptSession.setAttribute(SCRIPTSESSION_USER, scriptSessionUser);
                scriptSession.setAttribute(scriptSession.getId(), scriptSessionUser);
                scriptSession.setAttribute(webContext.getSession().getId(), scriptSession.getId());

                ScriptSessionAndUsers.addScriptSessionIdToSetUnderWebSession(webContext,scriptSession);
                return scriptSessionUser;
            }else{
                return (User) scriptSession.getAttribute(SCRIPTSESSION_USER);
            }

        }
    }
    public static void addNewScriptSessionVsObject(String sessionId,Object objectEegPointWatchListAndSoOn,String scriptSessionId){
        if(sessionId !=null && sessionId != "" &&
            objectEegPointWatchListAndSoOn !=null &&
            scriptSessionId !=null && scriptSessionId != ""
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

        if(sessionId !=null && sessionId != "" &&
            scriptSessionId !=null && scriptSessionId != ""
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
    public static void removeScriptSessionVsObject(String sessionId){
        if(sessionId!=null && !sessionId.isEmpty()) {
            if (Common.getCtx().getAttribute(sessionId) != null) {
                Common.getCtx().removeAttribute(sessionId);
            }
        }
        else
            try {
                throw new Exception("Parameter sessionId cannot be null or empty");
            } catch (Exception e) {
                LOG.error(e);
            }

    }
    public static void removeScriptSessionVsObject(String sessionId, String scriptSessionId){

        ((Map<String,Object>)Common.getCtx().getAttribute(sessionId)).remove(scriptSessionId);

    }
    private static User getUserFromCommonUnderGivensessionId(){
        Set<String> scriptSessionsForWebSession = new HashSet<String>();
        User user = null;
        if(Common.getCtx().getAttribute(FinalVariablesForControllers.DWRSCRIPTSESSIONUSER)!=null) {
            try {
                scriptSessionsForWebSession = (Set<String>) Common.getCtx().getAttribute((String) Common.getCtx().getAttribute(FinalVariablesForControllers.DWRSCRIPTSESSIONUSER));
                for (String id : scriptSessionsForWebSession) {
                    String h = id;

                }
            } catch (Exception e) {
                int a = 0;
            }
        }
        return user;
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

        Object object = new Object();

        synchronized(object) {
            Common.getCtx().removeAttribute(SCRIPTSESSION_USER);
            Common.getCtx().setAttribute(SCRIPTSESSION_USER, scriptSessionsForWebSession);
        }

    }
    public static void addScriptSessionIdToSetUnderWebSession( WebContext webContext,ScriptSession scriptSession){
        if( Common.getCtx().getAttribute(SCRIPTSESSION_USER)==null) {
            Common.getCtx().setAttribute(SCRIPTSESSION_USER, webContext.getSession().getId());
            Common.getCtx().setAttribute(webContext.getSession().getId(), new HashSet<String>());
        }

        else {
            Set<String> scriptSessionsForWebSession = new HashSet<String>();
            try {
                scriptSessionsForWebSession = (Set<String>) Common.getCtx().getAttribute((String)Common.getCtx().getAttribute(SCRIPTSESSION_USER));
            }
            catch (Exception e){
                int a=0;
            }
            scriptSessionsForWebSession.add(scriptSession.getId());
            Common.getCtx().removeAttribute(webContext.getSession().getId());
            Common.getCtx().setAttribute(webContext.getSession().getId(), scriptSessionsForWebSession);
        }
    }
    public static User findScriptSessionUserInScriptSessionManagerCollection(HttpServletRequest request){
        User userDwr = null;
        Collection<DefaultScriptSession> col  = (Collection<DefaultScriptSession>) getScriptSessionManager(request).getScriptSessionsByPage("/ScadaBR/data_point_edit.shtm");
        Iterator dss = col.iterator();
        while(dss.hasNext()){
            DefaultScriptSession dss2 = (DefaultScriptSession) dss.next();
            userDwr=(User) dss2.getAttribute((String)request.getSession().getId());

            if(userDwr!=null)
                return userDwr;
        }
        return userDwr;
    }


}
