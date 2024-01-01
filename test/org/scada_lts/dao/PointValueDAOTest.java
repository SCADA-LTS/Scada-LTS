package org.scada_lts.dao;

import br.org.scadabr.db.utils.TestUtils;
import com.serotonin.mango.rt.dataImage.AnnotatedPointValueTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.scada_lts.dao.model.point.PointValue;
import org.scada_lts.dao.pointvalues.PointValueDAO;
import org.scada_lts.utils.ResultSetMock;
import org.scada_lts.web.beans.GetApplicationBeans;
import org.springframework.context.ApplicationContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PointValueDAOTest extends TestDAO {

    private Map<String,Object> rowExample;
    private ResultSet resultSet;
    private final String username = "admin";
    private final int sourceId = 111111;

    @Before
    public void setUp(){
        ApplicationContext applicationContextMock = mock(ApplicationContext.class);
        IUserDAO userDaoMock = mock(IUserDAO.class);
        when(userDaoMock.getUser(11111)).thenReturn(TestUtils.newUser(11111, "admin"));
        when(applicationContextMock.getBean(eq("userDaoWithCache"))).thenReturn(userDaoMock);
        new GetApplicationBeans().setApplicationContext(applicationContextMock);
        rowExample = new HashMap<>();
        rowExample.put("dataType",33333);
        rowExample.put("dataPointId",333332);
        rowExample.put("sourceId",sourceId);
        rowExample.put("sourceType",1);
        rowExample.put("username", username);

        resultSet = new ResultSetMock(rowExample);
    }

    @Test
    public void doPointValueContainsGivenUserNameFromResultSetTest() throws SQLException {
        PointValue pointValue = new PointValueDAO.PointValueRowMapper().mapRow(resultSet,1);
        AnnotatedPointValueTime annotatedPointValueTime = (AnnotatedPointValueTime) pointValue.getPointValue();
        Assert.assertEquals(sourceId, annotatedPointValueTime.getSourceId());
    }

    @After
    public void tearDown() {
        // drop database;
        resultSet = null;
        rowExample = null;
        LOG.info("End test ");
    }
}
