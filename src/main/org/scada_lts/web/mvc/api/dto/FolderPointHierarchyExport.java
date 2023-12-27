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

import java.util.List;
import java.util.Objects;

/**
 * Create by at Grzesiek Bylica
 *
 * @author grzegorz.bylica@gmail.com
 */
public class FolderPointHierarchyExport {
    private String parentXid;
    private String name;
    private String xidFolder;
    private List<String> pointXids;

    public FolderPointHierarchyExport() {
        //
    }

    public FolderPointHierarchyExport(FolderPointHierarchy fph) {
        this.parentXid = fph.getParentXid();
        this.name = fph.getName();
        this.xidFolder = fph.getXid();
        this.pointXids = fph.getPointXids();
    }

    public String getParentXid() {
        return parentXid;
    }

    public void setParentXid(String parentXid) {
        this.parentXid = parentXid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getXidFolder() {
        return xidFolder;
    }

    public void setXidFolder(String xidFolder) {
        this.xidFolder = xidFolder;
    }

    public List<String> getPointXids() {
        return pointXids;
    }

    public void setPointXids(List<String> pointXids) {
        this.pointXids = pointXids;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FolderPointHierarchyExport that = (FolderPointHierarchyExport) o;
        return Objects.equals(parentXid, that.parentXid) &&
                Objects.equals(name, that.name) &&
                Objects.equals(xidFolder, that.xidFolder) &&
                Objects.equals(pointXids, that.pointXids);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parentXid, name, xidFolder, pointXids);
    }
}
