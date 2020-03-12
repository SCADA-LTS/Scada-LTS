package org.scada_lts.dao;

import org.junit.Test;
import org.scada_lts.dao.pointvalues.PointValueDAO;
import org.scada_lts.dao.pointvalues.ResultSetImpl;

import java.sql.ResultSet;

public class PointValueDAOTest extends TestDAO {

    @Test
    public void test1(){
        ResultSet resultSet = new ResultSetImpl();

        new PointValueDAO().getPointValueRow(resultSet,1);

    }
}
