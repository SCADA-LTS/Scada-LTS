package org.scada_lts.ds.state.change;

import org.scada_lts.ds.state.IStateDs;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @autor grzegorz.bylica@gmail.com on 02.11.18
 */
public class ChangeStatus {

    private static final String STATE_PROPERTY = "state_property";

    List<PropertyChangeListener> listener;

    protected ChangeStatus() {
        listener = new ArrayList<>();
    }

    protected void notifyListeners(Object object, IStateDs oldValue, IStateDs newValue) {
        for (PropertyChangeListener state : listener) {
            state.propertyChange(new PropertyChangeEvent(object, STATE_PROPERTY, oldValue, newValue));
        }
    }

    public void addChangeListener(PropertyChangeListener newListener) {
        listener.add(newListener);
    }

    protected void resetListeners() {
        this.listener = new ArrayList<>();
    }
}
