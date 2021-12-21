package org.scada_lts.service;

import org.scada_lts.dao.SynopticPanelDAO;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.service.model.SynopticPanel;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Optional<SynopticPanel> getSynopticPanel(int id) {
        return Optional.ofNullable(synopticPanelDAO.getById(id));
    }

    public SynopticPanel createSynopticPanel(SynopticPanel synopticPanel) {
        return (SynopticPanel) synopticPanelDAO.create(synopticPanel);
    }

    public SynopticPanel updateSynopticPanel(SynopticPanel synopticPanel) throws EmptyResultDataAccessException {
        return (synopticPanelDAO.update(synopticPanel));
    }

    public int deleteSynopticPanel(int id) {
        return (Integer) synopticPanelDAO.delete(id);
    }

}