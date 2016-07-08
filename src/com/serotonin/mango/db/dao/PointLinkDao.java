/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.db.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.serotonin.db.spring.GenericRowMapper;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.vo.link.PointLinkVO;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 * @author Matthew Lohbihler
 */
public class PointLinkDao extends BaseDao {
    public String generateUniqueXid() {
        return generateUniqueXid(PointLinkVO.XID_PREFIX, "pointLinks");
    }

    public boolean isXidUnique(String xid, int excludeId) {
        return isXidUnique(xid, excludeId, "pointLinks");
    }

    private static final String POINT_LINK_SELECT = "select id, xid, sourcePointId, targetPointId, script, eventType, disabled from pointLinks ";

    public List<PointLinkVO> getPointLinks() {
        return query(POINT_LINK_SELECT, new PointLinkRowMapper());
    }

    public List<PointLinkVO> getPointLinksForPoint(int dataPointId) {
        return query(POINT_LINK_SELECT + "where sourcePointId=? or targetPointId=?", new Object[] { dataPointId,
                dataPointId }, new PointLinkRowMapper());
    }

    public PointLinkVO getPointLink(int id) {
        return queryForObject(POINT_LINK_SELECT + "where id=?", new Object[] { id }, new PointLinkRowMapper(), null);
    }

    public PointLinkVO getPointLink(String xid) {
        return queryForObject(POINT_LINK_SELECT + "where xid=?", new Object[] { xid }, new PointLinkRowMapper(), null);
    }

    class PointLinkRowMapper implements GenericRowMapper<PointLinkVO> {
        public PointLinkVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            PointLinkVO pl = new PointLinkVO();
            int i = 0;
            pl.setId(rs.getInt(++i));
            pl.setXid(rs.getString(++i));
            pl.setSourcePointId(rs.getInt(++i));
            pl.setTargetPointId(rs.getInt(++i));
            pl.setScript(rs.getString(++i));
            pl.setEvent(rs.getInt(++i));
            pl.setDisabled(charToBool(rs.getString(++i)));
            return pl;
        }
    }

    public void savePointLink(final PointLinkVO pl) {
        if (pl.getId() == Common.NEW_ID)
            insertPointLink(pl);
        else
            updatePointLink(pl);
    }

    private static final String POINT_LINK_INSERT = "insert into pointLinks (xid, sourcePointId, targetPointId, script, eventType, disabled) "
            + "values (?,?,?,?,?,?)";

    private void insertPointLink(PointLinkVO pl) {
        int id;
        if (Common.getEnvironmentProfile().getString("db.type").equals("postgres")){                
            try {
                Connection conn = DriverManager.getConnection(Common.getEnvironmentProfile().getString("db.url"),
                                            Common.getEnvironmentProfile().getString("db.username"),
                                            Common.getEnvironmentProfile().getString("db.password"));
                PreparedStatement preStmt = conn.prepareStatement(POINT_LINK_INSERT);
                preStmt.setString(1, pl.getXid());
                preStmt.setInt(2, pl.getSourcePointId());
                preStmt.setInt(3, pl.getTargetPointId());
                preStmt.setString(4, pl.getScript());
                preStmt.setInt(5, pl.getEvent());
                preStmt.setString(6, boolToChar(pl.isDisabled()));
                preStmt.executeUpdate();

                ResultSet resSEQ = conn.createStatement().executeQuery("SELECT currval('pointlinks_id_seq')");
                resSEQ.next();
                id = resSEQ.getInt(1);

                conn.close(); 
            } catch (SQLException ex) {
                ex.printStackTrace();
                id = 0;
            }
        }
        else{
            id = doInsert(POINT_LINK_INSERT, new Object[] { pl.getXid(), pl.getSourcePointId(), pl.getTargetPointId(),
                pl.getScript(), pl.getEvent(), boolToChar(pl.isDisabled()) });
        }
        pl.setId(id);
        AuditEventType.raiseAddedEvent(AuditEventType.TYPE_POINT_LINK, pl);
    }

    private static final String POINT_LINK_UPDATE = "update pointLinks set xid=?, sourcePointId=?, targetPointId=?, script=?, eventType=?, disabled=? "
            + "where id=?";

    private void updatePointLink(PointLinkVO pl) {
        PointLinkVO old = getPointLink(pl.getId());

        ejt.update(POINT_LINK_UPDATE,
                new Object[] { pl.getXid(), pl.getSourcePointId(), pl.getTargetPointId(), pl.getScript(),
                        pl.getEvent(), boolToChar(pl.isDisabled()), pl.getId() });

        AuditEventType.raiseChangedEvent(AuditEventType.TYPE_POINT_LINK, old, pl);
    }

    public void deletePointLink(final int pointLinkId) {
        PointLinkVO pl = getPointLink(pointLinkId);
        if (pl != null) {
            ejt.update("delete from pointLinks where id=?", new Object[] { pointLinkId });
            AuditEventType.raiseDeletedEvent(AuditEventType.TYPE_POINT_LINK, pl);
        }
    }
}
