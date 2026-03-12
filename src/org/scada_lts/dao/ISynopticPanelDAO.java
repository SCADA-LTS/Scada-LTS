package org.scada_lts.dao;

import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.service.model.SynopticPanel;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

public interface ISynopticPanelDAO extends CrudOperations<SynopticPanel> {
    SynopticPanel create(SynopticPanel entity);

    List<ScadaObjectIdentifier> getSimpleList();

    List<SynopticPanel> getAll();

    SynopticPanel getById(int id) throws EmptyResultDataAccessException;

    SynopticPanel update(SynopticPanel entity);

    int delete(int id);
}
