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
package org.scada_lts.dao;

import br.org.scadabr.vo.scripting.ScriptVO;
import com.serotonin.mango.vo.publish.PublishedPointVO;
import com.serotonin.mango.vo.publish.PublisherVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO for Publisher
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class PublisherDAO {

	private static final Log LOG = LogFactory.getLog(PublisherDAO.class);

	private static final String COLUMN_NAME_ID = "id";
	private static final String COLUMN_NAME_XID = "xid";
	private static final String COLUMN_NAME_DATA = "data";

	private static final String PUBLISHER_SELECT = ""
			+ "select "
				+ COLUMN_NAME_ID + ", "
				+ COLUMN_NAME_XID + ", "
				+ COLUMN_NAME_DATA + " "
			+ "from publishers ";

	private static final String PUBLISHER_INSERT = ""
			+ "insert into publishers ("
				+ COLUMN_NAME_XID + ", "
				+ COLUMN_NAME_DATA + ") "
			+ "values (?,?) ";

	private static final String PUBLISHER_UPDATE = ""
			+ "update publishers set "
				+ COLUMN_NAME_XID + "=?, "
				+ COLUMN_NAME_DATA + "=? "
			+ "where "
				+ COLUMN_NAME_ID + "=? ";

	private static final String PUBLISHER_DELETE = ""
			+ "delete from publishers where "
				+ COLUMN_NAME_ID + "=? ";

	private class PublisherRowMapper implements RowMapper<PublisherVO> {

		@Override
		public PublisherVO<? extends PublishedPointVO> mapRow(ResultSet rs, int rowNum) throws SQLException {
//			PublisherVO<? extends PublishedPointVO> p = (PublisherVO<?>) new SerializationData().writeObject(rs.getBlob(COLUMN_NAME_DATA).getBinaryStream());
//			ScriptVO<?> script = (ScriptVO<?>) new SerializationData().readObject(rs.getBlob(COLUMN_NAME_DATA).getBinaryStream());
			return null;
		}
	}
}
