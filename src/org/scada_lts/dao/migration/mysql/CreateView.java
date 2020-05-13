package org.scada_lts.dao.migration.mysql;

/*
 * (c) 2020 hyski.mateusz@gmail.com
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

/**
 * @author  hyski mateusz@gmail.com on 27.04.2020
 */
class CreateView {

    private StringBuilder stringBuilder = new StringBuilder();
    String CreateViewWithSpecification(
            StringBuilder viewName,
            String[] columnNames,
            StringBuilder fromTable
    )
    {
        stringBuilder.append( createView(viewName) );
        stringBuilder.append( AddColumns(columnNames,fromTable) );
        return  stringBuilder.append(";").toString();
    }
    private StringBuilder createView(StringBuilder tableName){
        return new StringBuilder("create view ").append(tableName).append(" as ");
    }
    private StringBuilder AddColumns (String[] columnNames,StringBuilder fromTable){
        StringBuilder sb = new StringBuilder(" select ");
        for(int counter=0;counter<columnNames.length;counter++) {
            sb.append(columnNames[counter]);
        }
        sb.append("from ");
        sb.append(fromTable);
        return sb;
    }
}
