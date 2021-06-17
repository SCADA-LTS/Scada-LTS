package org.scada_lts.service;

import org.scada_lts.dao.SynopticPanelDAO;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.service.model.SynopticPanel;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for Synoptic Panels
 * Handle business logic for this objects.
 *
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0.0
 */
@Service
public class SynopticPanelService {

    private final SynopticPanelDAO synopticPanelDAO = new SynopticPanelDAO();

    public List<ScadaObjectIdentifier> getSimpleSynopticPanelsList() {
        return synopticPanelDAO.getSimpleList();
    }

    public SynopticPanel getSynopticPanel(int id) {
        return synopticPanelDAO.getById(id);
    }

    public SynopticPanel createSynopticPanel(SynopticPanel synopticPanel) {
        return (SynopticPanel) synopticPanelDAO.create(synopticPanel);
    }

    public SynopticPanel updateSynopticPanel(SynopticPanel synopticPanel) {
        return (SynopticPanel) synopticPanelDAO.update(synopticPanel);
    }

    public int deleteSynopticPanel(int id) {
        return (Integer) synopticPanelDAO.delete(id);
    }

}