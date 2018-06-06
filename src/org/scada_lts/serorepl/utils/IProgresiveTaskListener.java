package org.scada_lts.serorepl.utils;

public interface IProgresiveTaskListener {

    void progressUpdate(float value);

    void taskCancelled();

    void taskCompleted();
}
