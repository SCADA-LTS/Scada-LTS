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

import org.scada_lts.service.model.PointHierarchyConsistency;

import java.util.List;
import java.util.Objects;

/**
 * Create by at Grzesiek Bylica
 *
 * @author grzegorz.bylica@gmail.com
 */
public class PointHierarchyConsistencyCheck {
    private List<PointHierarchyConsistency> points;

    public List<PointHierarchyConsistency> getPoints() {
        return points;
    }

    public void setPoints(List<PointHierarchyConsistency> points) {
        this.points = points;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointHierarchyConsistencyCheck that = (PointHierarchyConsistencyCheck) o;
        return Objects.equals(points, that.points);
    }

    @Override
    public int hashCode() {

        return Objects.hash(points);
    }

    @Override
    public String toString() {
        return "PointHierarchyConsistencyCheck{" +
                "points=" + points +
                '}';
    }
}
