package org.scada_lts.serorepl.utils;

public interface ILifeCycle {

    void init() throws LifeCycleException;

    void annihilate() throws LifeCycleException;

    void joinAnnihilation();
}
