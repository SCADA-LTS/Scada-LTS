package org.scada_lts;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.DatabaseAccess;
import com.serotonin.mango.rt.EventManager;
import com.serotonin.mango.web.ContextWrapper;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.scada_lts.dao.IUserDAO;
import org.scada_lts.permissions.service.GetShareUsers;
import org.scada_lts.utils.ApplicationBeans;
import org.springframework.context.ApplicationContext;
import utils.NameClass;

import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static utils.ClassFinderUtils.*;

@RunWith(Parameterized.class)
public class CreateObjectTest {

    @Parameterized.Parameters(name= "{index}: test constructor: {0}")
    public static NameClass[] data() {
        String classPackage = "br.org.scadabr";
        String classPackage2 = "cc.radiuino.scadabr";
        String classPackage3 = "com.serotonin";
        String classPackage4 = "org.scada_lts";

        String classPackage5 = "org.scada_lts.mango.service";
        String classPackage6 = "org.scada_lts.permissions.service";
        String classPackage7 = "org.scada_lts.service";
        String classPackage8 = "org.scada_lts.dao";

        Set<NameClass> nameClasses = getDaoServiceNoInterfacePublicClasses(classPackage, classPackage2, classPackage3, classPackage4);
        nameClasses.addAll(getNoInterfacePublicClasses(classPackage5, classPackage6, classPackage7, classPackage8));

        return nameClasses.toArray(new NameClass[]{});
    }

    private final NameClass nameClass;

    public CreateObjectTest(NameClass nameClass) {
        this.nameClass = nameClass;
    }

    @BeforeClass
    public static void config() {
        ContextWrapper contextWrapper = mock(ContextWrapper.class);
        DatabaseAccess databaseAccess = mock(DatabaseAccess.class);
        EventManager eventManager = mock(EventManager.class);
        when(contextWrapper.getDatabaseAccess()).thenReturn(databaseAccess);
        when(contextWrapper.getDatabaseQueryAccess()).thenReturn(databaseAccess);
        when(contextWrapper.getEventManager()).thenReturn(eventManager);
        DataSource dataSource = mock(DataSource.class);
        when(databaseAccess.getDataSource()).thenReturn(dataSource);
        Common.ctx = contextWrapper;

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(applicationContext.getBean("getShareUsers")).thenReturn(mock(GetShareUsers.class));
        when(applicationContext.getBean("userDAO")).thenReturn(mock(IUserDAO.class));
        when(applicationContext.getBean("userDaoWithCache")).thenReturn(mock(IUserDAO.class));

        ApplicationBeans applicationBeans = new ApplicationBeans();
        applicationBeans.setApplicationContext(applicationContext);
    }

    @Test
    public void when_create_object_then_no_error() throws InstantiationException,
            IllegalAccessException, InvocationTargetException {
        Class<?> clazz = nameClass.getClazz();

        Optional<Constructor<?>> constructorOpt = getConstructor(clazz);

        if(constructorOpt.isPresent() && constructorOpt.get().getParameters().length > 0) {
            Constructor<?> constructor = constructorOpt.get();
            Object[] args = nulls(constructor.getParameters());
            constructor.newInstance(args);
        } else {
            clazz.newInstance();
        }
    }
}
