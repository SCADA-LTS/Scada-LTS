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
class AlterTable {

    private StringBuilder stringBuilder = new StringBuilder();
    enum
        Fields{
        TINYINT
    }
    String AlterTableWithSpecification(
            StringBuilder tableName,
            StringBuilder columtnName,
            Enum Fields,int size, boolean isNull
    )
    {
        stringBuilder.append( AlterTable(tableName) );
        stringBuilder.append( AddColumn(columtnName) );
        stringBuilder.append( SpecificationColumn(Fields,size,isNull));
        return  stringBuilder.append(";").toString();
    }
    private StringBuilder AlterTable(StringBuilder tableName){
        return new StringBuilder("ALTER TABLE ").append(tableName).append(" ");
    }
    private StringBuilder AddColumn (StringBuilder columnName){
        StringBuilder sb = new StringBuilder(" ADD COLUMN ");
        sb.append("`");
        sb.append(columnName);
        sb.append("`");
        sb.append(" ");
        return sb;
    }
    private StringBuilder SpecificationColumn(Enum Fields,int size, boolean isNull){
        StringBuilder sb = new StringBuilder(" ");
        sb.append(Fields.toString());
        sb.append("(");
        sb.append(size);
        sb.append(")");
        sb.append(" ");
        sb.append(isNull?"NULL":"NOT NULL");
        sb.append(" ");
        return sb;
    }

}
