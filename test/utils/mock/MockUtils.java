package utils.mock;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.PointValueDao;
import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.rt.maint.BackgroundProcessing;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.web.ContextWrapper;
import com.serotonin.util.PropertiesUtils;
import org.scada_lts.dao.DAO;
import org.scada_lts.web.beans.ApplicationBeans;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import utils.ServletContextMock;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

public class MockUtils {

    public static void configMock(RuntimeManager runtimeManager, User user) throws Exception {
        configMockContextWrapper(runtimeManager);

        PointValueDao pointValueDao = mock(PointValueDao.class);
        whenNew(PointValueDao.class)
                .withNoArguments()
                .thenReturn(pointValueDao);

        DAO dao = mock(DAO.class);
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        when(dao.getJdbcTemp()).thenReturn(jdbcTemplate);
        whenNew(DAO.class)
                .withAnyArguments()
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

        PropertiesUtils propertiesUtils = new PropertiesUtils("env");
        when(Common.getEnvironmentProfile()).thenReturn(propertiesUtils);
    }

    public static void configMockContextWrapper(RuntimeManager runtimeManager) {
        ContextWrapper contextWrapper = mock(ContextWrapper.class);
        ServletContext servletContext = new ServletContextMock(a ->
                a.contains("scriptFunctions") ? "test/scriptFunctions.js" : "".equals(a) ? "test/" : "");
        BackgroundProcessing backgroundProcessing = mock(BackgroundProcessing.class);

        when(contextWrapper.getRuntimeManager()).thenReturn(runtimeManager);
        when(contextWrapper.getServletContext()).thenReturn(servletContext);
        when(contextWrapper.getBackgroundProcessing()).thenReturn(backgroundProcessing);
        Common.ctx = contextWrapper;
    }
  
    public static void configDaoMock() throws Exception {
        mockStatic(ApplicationBeans.class);

        DataSource dataSource = mock(DataSource.class);
        when(ApplicationBeans.getBean(eq("databaseSource"), eq(DataSource.class))).thenReturn(dataSource);

        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = mock(NamedParameterJdbcTemplate.class);

        whenNew(JdbcTemplate.class)
                .withParameterTypes(DataSource.class)
                .withArguments(eq(dataSource))
                .thenReturn(jdbcTemplate);

        whenNew(NamedParameterJdbcTemplate.class)
                .withParameterTypes(DataSource.class)
                .withArguments(eq(dataSource))
                .thenReturn(namedParameterJdbcTemplate);
    }
}
