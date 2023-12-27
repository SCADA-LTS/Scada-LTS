/*
 * (c) 2018 grzegorz.bylica@gmail.com
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
package org.scada_lts.web.mvc.api.dto;

import com.serotonin.mango.vo.hierarchy.PointHierarchy;

import java.util.List;
import java.util.Objects;

/**
 * Create by at Grzesiek Bylica
 *
 * @author grzegorz.bylica@gmail.com
 */
public class PointHierarchyExp {

    private List<FolderPointHierarchyExport> folders;

    public PointHierarchyExp() {
        //
    }

    public PointHierarchyExp(List<FolderPointHierarchyExport> folders) {
        this.folders = folders;
    }

    public List<FolderPointHierarchyExport> getFolders() {
        return folders;
    }

    public void setFolders(List<FolderPointHierarchyExport> folders) {
        this.folders = folders;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointHierarchyExp that = (PointHierarchyExp) o;
        return Objects.equals(folders, that.folders);
    }

    @Override
    public int hashCode() {

        return Objects.hash(folders);
    }
}
