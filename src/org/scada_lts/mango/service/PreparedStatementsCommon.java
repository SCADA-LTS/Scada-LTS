package org.scada_lts.mango.service;


import br.org.scadabr.vo.scripting.ScriptVO;
import com.serotonin.mango.Common;
import com.serotonin.util.SerializationHelper;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

class PreparedStatementsCommon {

    private String INSERT__SCRIPTS = "insert into scripts (xid, name,  script, userId, data) values (?,?,?,?,?)";

    void insertIntoScripts(final ScriptVO<?> vo){
        try {
            Connection conn = DriverManager.getConnection(Common.getEnvironmentProfile().getString("db.url"),
                    Common.getEnvironmentProfile().getString("db.username"),
                    Common.getEnvironmentProfile().getString("db.password"));
            PreparedStatement preStmt = conn.prepareStatement(INSERT__SCRIPTS);
            preStmt.setString(1, vo.getXid());
            preStmt.setString(2, vo.getName());
            preStmt.setString(3, vo.getScript());
            preStmt.setInt(4, vo.getUserId());
            preStmt.setBytes(5, SerializationHelper.writeObjectToArray(vo));
            preStmt.executeUpdate();

            ResultSet resSEQ = conn.createStatement().executeQuery("SELECT currval('scripts_id_seq')");
            resSEQ.next();
            int id = resSEQ.getInt(1);

            conn.close();

            vo.setId(id);

        } catch (SQLException ex) {
            Logger.getLogger(FlexProjectService.class.getName()).log(Level.SEVERE, null, ex);
            vo.setId(0);
        }
    }
}
