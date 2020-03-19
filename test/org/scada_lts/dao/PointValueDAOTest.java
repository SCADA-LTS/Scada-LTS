package org.scada_lts.dao;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.scada_lts.dao.model.point.PointValue;
import org.scada_lts.dao.pointvalues.PointValueDAO;
import org.scada_lts.dao.pointvalues.ResultSetImpl;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class PointValueDAOTest extends TestDAO {

    private Map<String,String> rowExample;
    private ResultSet resultSet;
    private final String USERNAME = "admin";

    @Before
    public void setUp(){
        rowExample = new HashMap<String,String>();
        rowExample.put("dataType","33333");
        rowExample.put("dataPointId","333332");
        rowExample.put("sourceId","11111");
        rowExample.put("sourceType","1");
        rowExample.put("username",USERNAME);

        resultSet = new ResultSetImpl(rowExample);
    }

    @Test
    public void doPointValueContainsGivenUserNameFromResultSetTest(){
        PointValue pointValue =  new PointValueDAO().getPointValueRow(resultSet,1);
        Assert.assertEquals(USERNAME, pointValue.getPointValue().getWhoChangedValue());
    }

    @After
    public void tearDown() {
        // drop database;
        resultSet = null;
        rowExample = null;
        LOG.info("End test ");
    }
}
