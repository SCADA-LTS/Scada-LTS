package org.scada_lts.dao;

import org.scada_lts.dao.model.multichangehistory.MultiChangeHistoryValues;
import org.scada_lts.service.model.MultiChangeHistoryDTO;

import java.util.List;

/**
 * Adds histories for the CMP component (which changes multiple point values in order to set eg. valve or pump  to a specific state).
 * And it also allows you to read the latest changes.
 *
 * @author grzegorz.bylica@abilit.eu on 15.10.2019
 */

public interface MultiChangesHistory {

    List<MultiChangeHistoryValues> getHistory(String viewAndCmpId);
    void addHistoryFromCMPComponent(Integer userId, String viewAndCmpId, String interpretedState, Long scadaTime, String values);
}
