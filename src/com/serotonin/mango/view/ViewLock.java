package com.serotonin.mango.view;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ViewLock depends on State interface and is responsible only to set view to
 * available/unavailable state to edit. To change state of view we change nextState method
 */
public class ViewLock implements States {

    private static final Log LOG = LogFactory.getLog(ViewLock.class);

    /**
     * default state of this is unlock state, this state gives blocks view possibility to edit by user
     */
    private final boolean VIEW_IS_LOCKED =true;

    private static ViewLock instance = new ViewLock();

    private ViewLock() { }

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
        return VIEW_IS_LOCKED;
    }


    /**
     * change state of view
     *
     */
    @Override
    public void nextState(View view) {
        view.changeState(ViewUnlock.instance());
        LOG.info(" Change state for "+ViewUnlock.instance().toString()+" to Lock");
    }
    public String toString(){
        return new StringBuilder()
                .append("View is locked").toString();
    }
}
