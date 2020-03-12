package org.scada_lts.dao;

import org.junit.Test;
import org.scada_lts.dao.pointvalues.PointValueDAO;
import org.scada_lts.dao.pointvalues.ResultSetImpl;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class PointValueDAOTest extends TestDAO {

    @Test
    public void test1(){
        Map<String,String> rowExample = new HashMap<String,String>();
        ResultSet resultSet = new ResultSetImpl(rowExample);

        new PointValueDAO().getPointValueRow(resultSet,1);

    }
}
