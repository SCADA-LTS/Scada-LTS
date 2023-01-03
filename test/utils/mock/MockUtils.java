package utils.mock;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.PointValueDao;
import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.web.ContextWrapper;
import com.serotonin.util.PropertiesUtils;
import org.scada_lts.dao.impl.DAO;
import org.springframework.jdbc.core.JdbcTemplate;
import utils.ServletContextMock;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

public class MockUtils {

    public static void configMock(RuntimeManager runtimeManager, User user) throws Exception {
        ContextWrapper contextWrapper = mock(ContextWrapper.class);
        ServletContext servletContext = new ServletContextMock(a ->
                a.contains("scriptFunctions") ? "test/scriptFunctions.js" : "".equals(a) ? "test/" : "");

        Common.ctx = contextWrapper;
        when(contextWrapper.getRuntimeManager()).thenReturn(runtimeManager);
        when(contextWrapper.getServletContext()).thenReturn(servletContext);

        PointValueDao pointValueDao = mock(PointValueDao.class);
        whenNew(PointValueDao.class)
                .withNoArguments()
                .thenReturn(pointValueDao);

        DAO dao = mock(DAO.class);
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        when(dao.getJdbcTemp()).thenReturn(jdbcTemplate);
        whenNew(DAO.class)
                .withNoArguments()
                .thenReturn(dao);

        UserDao userDao = mock(UserDao.class);
        whenNew(UserDao.class)
                .withNoArguments()
                .thenReturn(userDao);

        when(userDao.getUser(any())).thenReturn(user);
        when(userDao.getUser(anyInt())).thenReturn(user);

        mockStatic(Common.class);
        when(Common.getUser()).thenReturn(user);
        when(Common.getUser(any(HttpServletRequest.class))).thenReturn(user);

        PropertiesUtils propertiesUtils = new PropertiesUtils("WEB-INF/classes/env");
        when(Common.getEnvironmentProfile()).thenReturn(propertiesUtils);
    }
}
