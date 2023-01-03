/*
 * (c) 2016 Abil'I.T. http://abilit.eu/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.scada_lts.mango.service;

import br.org.scadabr.api.vo.FlexProject;
import com.serotonin.mango.Common;
import org.scada_lts.dao.impl.FlexProjectDAO;
import org.scada_lts.mango.adapter.MangoFlexProject;

import java.util.List;

/**
 * FlexProjectDAO service
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class FlexProjectService implements MangoFlexProject {

	private FlexProjectDAO flexProjectDAO = new FlexProjectDAO();

	@Override
	public int saveFlexProject(int id, String name, String description, String xmlConfig) {
		if (id == Common.NEW_ID) {
			return flexProjectDAO.insert(name, description, xmlConfig);
		} else {
			flexProjectDAO.update(id, name, description, xmlConfig);
			return id;
		}
	}

	@Override
	public void deleteFlexProject(int flexProjectId) {
		flexProjectDAO.delete(flexProjectId);
	}

	@Override
	public FlexProject getFlexProject(int id) {
		return flexProjectDAO.getFlexProject(id);
	}

	@Override
	public List<FlexProject> getFlexProjects() {
		return flexProjectDAO.getFlexProjects();
	}
}
