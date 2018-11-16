package com.serotonin.mango;

import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.*;
import org.directwebremoting.extend.ScriptSessionManager;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * ScriptSessionAndUsers responsible for
 * - save User under correct key (scriptSessionId) into map as additional attibute in session which is called userDwrScriptSessions
 * - operate on map.
 *
 * @author Mateusz Hyski mateusz.hyski@softq.pl
 */
public class ScriptSessionAndUsers {

    /**
     * ...for information in catch part...
     */
    private static final Log LOG = LogFactory.getLog(ScriptSessionAndUsers.class);

    /**
     * {@value #USER_SCRIPT_SESSIONS} is the attribute name under session
     */
    public static final String USER_SCRIPT_SESSIONS = "userDwrScriptSessions";
    public static final String SCRIPTSESSION_USER = "dwrscriptsessionuser";

    /**
     * Returns correct User from Session
     * ( under 'userDwrScriptSessions' attribute is map keys of scriptSessionIds and Users as a value)
     *
     * @param scriptSessionId
     * @return User
     */
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
     * returns WebContext ONLY
     *
     * @return WebContext
     */
    private static WebContext getWebContext(){
        return WebContextFactory.get();
    }
    /**
     * Grab map (like a return value)  from Session under userDwrScriptSessions attribute
     *
     * @return Map<String, User>
     */
    private static Map<String, User> getMapScriptSessionOfUsers(){
        Map<String, User> scriptSessionAndUser = (Map) getWebContext().getHttpServletRequest().getSession().getAttribute(USER_SCRIPT_SESSIONS);
        return scriptSessionAndUser;
    }
    /**
     * Gets user from map what is attribute of Session
     *
     * @param user
     * @return User
     */
    static User findScriptSessionUser(User user, WebContext webContext){

        ScriptSession scriptSession=null;

        try {
            scriptSession = webContext == null ? null : webContext.getScriptSession();
        }catch (Exception e){
            LOG.warn("We have exception when trying to get scriptSession.");
        }

        if  (scriptSession == null) {
            return  null;
        }else{
            if (scriptSession.getAttribute(SCRIPTSESSION_USER) == null){
                User scriptSessionUser = new User(user);
                scriptSession.setAttribute(SCRIPTSESSION_USER,scriptSessionUser);
                return scriptSessionUser;
            }else{
                return (User) scriptSession.getAttribute(SCRIPTSESSION_USER);
            }
        }

        //scriptSession is null or exception, so try get User from map
        //return (scriptSession == null)  ? null
//                                      : dependsOnAttribute(scriptSession,webContext,user);
//                                        : (User) scriptSession.getAttribute("dwrscriptsessionuser");
    }

    /**
     * Fill map by puts User and scriptSessionId to them and return null
     * or return user from map.
     * Map is taked from Session as attribute {@value #USER_SCRIPT_SESSIONS}.
     *
     * @param scriptSession
     * @param webContext
     * @param user
     * @return User or null
     */
    private static User dependsOnAttribute(ScriptSession scriptSession, WebContext webContext, User user){
        Map<String, User> userScriptSessions = null;
        try {
            userScriptSessions = (Map) webContext.getHttpServletRequest().getSession().getAttribute(USER_SCRIPT_SESSIONS);
        }
        catch (Exception exception){
            LOG.warn("Under Session where is no attribute called "+USER_SCRIPT_SESSIONS);
        }

        //create map and fill by new value
        if (userScriptSessions == null) {
            scriptSessionAndUser(new HashMap<String, User>(),scriptSession,user,webContext);
        }
        else {
            //if map doesn't contain key 'scriptSessionId', put new
            if (userScriptSessions.get(scriptSession.getId()) == null) {
                scriptSessionAndUser(userScriptSessions,scriptSession,user,webContext);
            }
            else {
                //otherwide return user
                return userScriptSessions.get(scriptSession.getId());
            }
        }
        return null;
    }

    /**
     * Fill userScriptSessions by puts scriptSession and user
     * additional attribute userScriptSessions into webContext as {@value #USER_SCRIPT_SESSIONS}
     *
     * @param userScriptSessions
     * @param scriptSession
     * @param user
     * @param webContext
     */
    private static void scriptSessionAndUser(Map<String, User> userScriptSessions, ScriptSession scriptSession, User user, WebContext webContext){
        try {
            userScriptSessions.put(scriptSession.getId(), new User(user));
        }
        catch (Exception ex){
            LOG.error("Something wrong during add scriptSession and user to the map. ");
        }
        finally {
            webContext.getHttpServletRequest().getSession().setAttribute(USER_SCRIPT_SESSIONS, userScriptSessions);
        }


    }
}
