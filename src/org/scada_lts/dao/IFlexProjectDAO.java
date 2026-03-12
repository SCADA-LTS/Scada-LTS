package org.scada_lts.dao;

import br.org.scadabr.api.vo.FlexProject;

import java.util.List;

public interface IFlexProjectDAO {

    FlexProject getFlexProject(int id);

    int insert(String xid, String name, String description);

    void update(int id, String xid, String name, String description);

    void delete(int id);

    List<FlexProject> getFlexProjects();
}
