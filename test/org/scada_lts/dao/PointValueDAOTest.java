package org.scada_lts.dao;

import br.org.scadabr.db.utils.TestUtils;
import com.serotonin.mango.rt.dataImage.AnnotatedPointValueTime;
import com.serotonin.mango.vo.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.scada_lts.dao.model.point.PointValue;
import org.scada_lts.dao.pointvalues.PointValueDAO;
import org.scada_lts.login.ILoggedUsers;
import org.scada_lts.utils.ResultSetMock;
import org.scada_lts.web.beans.ApplicationBeans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ApplicationBeans.class})
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "com.sun.org.apache.xalan.*",
        "javax.activation.*", "javax.management.*"})
public class PointValueDAOTest {

    private Map<String,Object> rowExample;
    private ResultSet resultSet;
    private final String username = "admin";
    private final int sourceId = 111111;

    @Before
    public void setup() {

        IUserDAO userDaoMock = mock(IUserDAO.class);
        when(userDaoMock.getUser(sourceId)).thenReturn(TestUtils.newUser(sourceId, "admin"));

        User user = TestUtils.newUser(sourceId, "admin");
        ILoggedUsers loggedUsers = mock(ILoggedUsers.class);
        when(loggedUsers.getUser(eq(sourceId))).thenReturn(user);

        mockStatic(ApplicationBeans.class);
        when(ApplicationBeans.getLoggedUsersBean()).thenReturn(loggedUsers);

        rowExample = new HashMap<>();
        rowExample.put("dataType",1);
        rowExample.put("dataPointId",123);
        rowExample.put("sourceId",sourceId);
        rowExample.put("sourceType",1);
        rowExample.put("username", username);

        resultSet = new ResultSetMock(rowExample);
    }

    @Test
    public void when_mapRow_to_PointValue_then_sourceId() throws SQLException {

        //given:
        PointValueDAO.PointValueRowMapper mapper = new PointValueDAO.PointValueRowMapper();

        //when:
        PointValue pointValue = mapper.mapRow(resultSet,1);
        AnnotatedPointValueTime annotatedPointValueTime = (AnnotatedPointValueTime) pointValue.getPointValue();

        //then:
        Assert.assertEquals(sourceId, annotatedPointValueTime.getSourceId());
    }

    @Test
    public void when_mapRow_to_PointValue_then_sourceDescriptionArgument_same_username() throws SQLException {

        //given:
        PointValueDAO.PointValueRowMapper mapper = new PointValueDAO.PointValueRowMapper();

        //when:
        PointValue pointValue = mapper.mapRow(resultSet,1);
        AnnotatedPointValueTime annotatedPointValueTime = (AnnotatedPointValueTime) pointValue.getPointValue();

        //then:
        Assert.assertEquals(username, annotatedPointValueTime.getSourceDescriptionArgument());
    }
}
