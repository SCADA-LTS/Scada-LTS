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
public class FolderPointHierarchy {

    private String name;
    private int id;
    private String xid;
    private String parentXid;
    private int parentId;
    private List<String> pointXids;

    public FolderPointHierarchy() {
        //
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
    }

    public String getParentXid() {
        return parentXid;
    }

    public void setParentXid(String parentXid) {
        this.parentXid = parentXid;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
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
        FolderPointHierarchy that = (FolderPointHierarchy) o;
        return id == that.id &&
                parentId == that.parentId &&
                Objects.equals(name, that.name) &&
                Objects.equals(xid, that.xid) &&
                Objects.equals(parentXid, that.parentXid) &&
                Objects.equals(pointXids, that.pointXids);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id, xid, parentXid, parentId, pointXids);
    }
}
