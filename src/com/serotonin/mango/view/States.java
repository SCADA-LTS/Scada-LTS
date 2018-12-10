package com.serotonin.mango.view;


/**
 * Responsibility
 * - change state
 * - get status about state
 */
public interface States {

    /**
     * status about state, true when some element is locked,otherwise false
     *
     * @return boolean
     */
    boolean isBlocked();

    /**
     * change state of view
     *
     * @param view
     */
    void nextState(View view
    );

}
