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
package org.scada_lts.mango.adapter;

import br.org.scadabr.api.vo.FlexProject;

import java.util.List;

/**
 * FlexProject adapter
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public interface MangoFlexProject {

	int saveFlexProject(int id, String name, String description, String xmlConfig);

	void deleteFlexProject(final int flexProjectId);

	FlexProject getFlexProject(int id);

	List<FlexProject> getFlexProjects();
}
