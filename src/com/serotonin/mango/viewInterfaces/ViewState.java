package com.serotonin.mango.viewInterfaces;

/**
 * State of view.
 * If we want change condition  to change state of view we can put additional logic into
 * @see com.serotonin.mango.view.ViewLock or
 * @see com.serotonin.mango.view.ViewUnlock
 *
 * @author Mateusz Hyski {@link "mailto:mateusz.hyski@softq.pl;hyski.mateusz@gmail.com","ScadaLTS"}
 */
public interface ViewState {

    /**
     * status about View, true when some element is locked,otherwise false
     *
     * @return boolean
     */
    boolean isBlocked();

}
