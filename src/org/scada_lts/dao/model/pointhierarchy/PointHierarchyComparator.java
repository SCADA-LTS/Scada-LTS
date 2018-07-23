/*
 * (c) 2015 Abil'I.T. http://abilit.eu/
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
package org.scada_lts.dao.model.pointhierarchy;

import java.util.Comparator;

import org.apache.commons.lang3.StringUtils;

/**
 * Comparator for sort points hierarchy 
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * person supporting and coreecting translation Jerzy Piejko
 */
public class PointHierarchyComparator implements Comparator<PointHierarchyNode> {
	
	private static final PointHierarchyComparator instance = new PointHierarchyComparator();

    public int compare(PointHierarchyNode ph1, PointHierarchyNode ph2) {
        if (StringUtils.isEmpty(ph1.getTitle()))
            return -1;
        return ph1.getTitle().compareToIgnoreCase(ph2.getTitle());
    }
    
    public static PointHierarchyComparator getInst() {
    	return instance;
    }

}
