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
package org.scada_lts.service.model;

import java.util.Objects;

/**
 * Create by at Grzesiek Bylica
 *
 * @author grzegorz.bylica@gmail.com
 */
public class PointHierarchyConsistency {

    private String xidPoint;
    private String xidFolder;
    private int idFolder;
    private Boolean folderExist;

    public PointHierarchyConsistency() {
        //
    }

    public PointHierarchyConsistency(String xid, String xidFolder, int idFolder, Boolean folderExist) {
        this.xidPoint = xid;
        this.xidFolder = xidFolder;
        this.idFolder = idFolder;
        this.folderExist = folderExist;
    }

    public String getXidPoint() {
        return xidPoint;
    }

    public void setXidPoint(String xidPoint) {
        this.xidPoint = xidPoint;
    }

    public String getXidFolder() {
        return xidFolder;
    }

    public void setXidFolder(String xidFolder) {
        this.xidFolder = xidFolder;
    }

    public int getIdFolder() {
        return idFolder;
    }

    public void setIdFolder(int idFolder) {
        this.idFolder = idFolder;
    }

    public Boolean getFolderExist() {
        return folderExist;
    }

    public void setFolderExist(Boolean folderExist) {
        this.folderExist = folderExist;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointHierarchyConsistency that = (PointHierarchyConsistency) o;
        return idFolder == that.idFolder &&
                Objects.equals(xidPoint, that.xidPoint) &&
                Objects.equals(xidFolder, that.xidFolder) &&
                Objects.equals(folderExist, that.folderExist);
    }

    @Override
    public int hashCode() {

        return Objects.hash(xidPoint, xidFolder, idFolder, folderExist);
    }

    @Override
    public String toString() {
        return "PointHierarchyConsistency{" +
                "xidPoint='" + xidPoint + '\'' +
                ", xidFolder='" + xidFolder + '\'' +
                ", idFolder=" + idFolder +
                ", folderExist=" + folderExist +
                '}';
    }
}
