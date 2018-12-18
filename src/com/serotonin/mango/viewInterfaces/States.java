package com.serotonin.mango.viewInterfaces;


/**
 * Responsibility
 * - change state
 * - get status about state
 *
 * @author Mateusz Hyski {@link "mailto:mateusz.hyski@softq.pl;hyski.mateusz@gmail.com","ScadaLTS"}
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
     * @param viewState
     * @param userName
     * @param sessionId
     */
    void changeState(ViewState viewState, String userName, String sessionId);

}
