package org.scada_lts.service;

import com.serotonin.mango.Common;
import org.scada_lts.dao.SynopticPanelDAO;
import org.scada_lts.mango.service.ViewService;
import org.scada_lts.service.model.SynopticPanel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SynopticPanelService {

    private SynopticPanelDAO spDAO = new SynopticPanelDAO();

    public SynopticPanelService() {
        spDAO = new SynopticPanelDAO();
    }

    public void savePanel(final SynopticPanel synopticPanel) {
        if (synopticPanel.getId() == Common.NEW_ID) {
            spDAO.create(synopticPanel);
        } else {
            spDAO.update(synopticPanel);
        }
    }

    public void deletePanel(final SynopticPanel synopticPanel) {
        spDAO.delete(synopticPanel);
    }

    public List<SynopticPanel> getPanelSelectList() {
        List<SynopticPanel> synopticPanels = spDAO.getPanelSelectList();
        return synopticPanels;
    }

    public SynopticPanel getSynopticPanel(int id) {
        return spDAO.findById(new Object[] {id});
    }
}