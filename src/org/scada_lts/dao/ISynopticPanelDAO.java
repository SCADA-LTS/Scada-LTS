package org.scada_lts.dao;

import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.service.model.SynopticPanel;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

public interface ISynopticPanelDAO extends CrudOperations<SynopticPanel> {
    @Override
    SynopticPanel create(SynopticPanel entity);

    @Override
    List<ScadaObjectIdentifier> getSimpleList();

    @Override
    List<SynopticPanel> getAll();

    @Override
    SynopticPanel getById(int id) throws EmptyResultDataAccessException;

    @Override
    SynopticPanel update(SynopticPanel entity) throws EmptyResultDataAccessException;

    @Override
    int delete(int id);
}
