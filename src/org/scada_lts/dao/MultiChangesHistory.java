package org.scada_lts.dao;

import org.scada_lts.dao.model.multichangehistory.MultiChangeHistoryValues;
import org.scada_lts.service.model.MultiChangeHistoryDTO;

import java.util.List;

/**
 * @author grzegorz.bylica@abilit.eu on 15.10.2019
 */
public interface MultiChangesHistory {

    List<MultiChangeHistoryValues> getHistory(String viewAndCmpId);
}
