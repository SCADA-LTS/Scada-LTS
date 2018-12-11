package com.serotonin.mango.view;


import com.serotonin.mango.Common;

import java.util.HashSet;
import java.util.Set;


/**
 * Puts and remove in application scope data like viewXid and instance of  ViewLockedBy class
 *
 * @author Mateusz Hyski mateusz.hyski@softq.pl
 */
public class AvailableUnavailableViews {

    private  static Set<LockedViews> lockedViewsRegistry = new HashSet<LockedViews>();

    /**
     * check that view is available or not to edit state
     *
     * @param viewXid
     * @return boolean
     */
    public static boolean isThisViewIsAvailableToEdit(String viewXid) {
        LockedViews viewLockedBy = null;
        try {
            viewLockedBy = (LockedViews) Common.ctx.getCtx().getAttribute(viewXid);
        }
        catch(Exception exception){
            return false;
        }
        return viewLockedBy==null;
    }

    /**
     * add to applicationscope data about view which is locked and who is locking view and since when
     *
     * @param viewXid
     * @param userName
     */
    public static void addViewToBlockList(String viewXid, String userName,String sessionId){

        LockedViews viewLockedBy = new LockedViews(userName,viewXid,sessionId);

        lockedViewsRegistry.add(viewLockedBy);
        Common.ctx.getCtx().setAttribute(viewXid,viewLockedBy);
    }

    /**
     * remove from applicationscope view - in other words unlock view
     *
     * @param viewXid
     */
    public static void removeViewFromBlockList(String viewXid){
        Common.ctx.getCtx().removeAttribute(viewXid);
    }

    /**
     * when user select logout action, remove all views with locked status with present sessionId,
     * in other words when user logout from one web browser
     *
     * @param sessionId
     */
    public static void removeAllLockedViewsWhenUserLoggedOut(String sessionId){
        for (LockedViews view: lockedViewsRegistry) {
            if(view.getSessionId().equals(sessionId))
                removeViewFromBlockList(view.getViewXid());
        }
        lockedViewsRegistry.removeIf(item -> item.getSessionId().equals(sessionId));
    }
    /**
     *
     * get delails about user who edit the view
     *
     * @param xid
     * @return LockedViews
     */
    public static LockedViews getOwnerOfEditView(String xid){
        for (LockedViews view: lockedViewsRegistry) {
            if(view.getViewXid().equals(xid)) {
                return view;
            }
        }
        return null;
    }
    /**
     * check that view with given xid exists in Set.
     *
     * @param xid
     * @return boolean
     */
    public static boolean checkAvailabibityView(String xid){

        return isViewIsInLockedRegistry(xid);
    }

    /**
     * remove view with given xid from applicationscope
     * @param xid
     */
    public static void breakEditActionForUser(String xid){
        if(isViewIsInLockedRegistry(xid)) {
            removeViewFromBlockList(xid);
        }
    }

    /**
     * check view by xidName in 'locked resistry'
     *
     * @param xidName
     * @return boolean
     */
    private static boolean isViewIsInLockedRegistry(String xidName){
        for (LockedViews view: lockedViewsRegistry) {
            if(view.getViewXid().equals(xidName)) {
                return true;
            }
        }
        return false;
    }


}
