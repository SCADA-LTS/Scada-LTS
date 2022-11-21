package br.org.scadabr.db.dao;

import java.util.List;

import org.scada_lts.mango.adapter.MangoFlexProject;
import org.scada_lts.mango.service.FlexProjectService;

import br.org.scadabr.api.vo.FlexProject;


public class FlexProjectDao {

	MangoFlexProject flexProjectService = new FlexProjectService();

	public int saveFlexProject(int id, String name, String description,
			String xmlConfig) {
		return flexProjectService.saveFlexProject(id, name, description, xmlConfig);
	}


	public void deleteFlexProject(final int flexProjectId) {
		flexProjectService.deleteFlexProject(flexProjectId);
	}

	public FlexProject getFlexProject(int id) {
		return flexProjectService.getFlexProject(id);
	}

	public List<FlexProject> getFlexProjects() {
		return flexProjectService.getFlexProjects();
	}

}
