package com.serotonin.mango.view;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ViewUnlock depends on State interface and is responsible only to set view to
 * available/unavailable state to edit. To change state of view we change nextState method
 */
public class ViewUnlock implements States {

    private static final Log LOG = LogFactory.getLog(ViewUnlock.class);

    /**
     * default state of this is unlock state, this state gives view possibility to edit by user
     */
    private final boolean VIEW_IS_UNLOCKED =false;

    private static ViewUnlock instance = new ViewUnlock();

    private ViewUnlock() {}

    public static States instance() {
        return instance;
    }

    /**
     * status about state, true when some element is locked,otherwise false
     *
     * @return boolean
     */
    @Override
    public boolean isBlocked() {
        return VIEW_IS_UNLOCKED;
    }

    /**
     * change state of view
     *
     */
    @Override
    public void nextState(View view) {
        view.changeState(ViewLock.instance());
        LOG.info(" Change state for "+ViewLock.instance().toString()+" to Unlock");
    }
    public String toString(){
        return new StringBuilder()
                .append("View is unlocked").toString();
    }
}
