package org.scada_lts;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import utils.NameClass;

import java.util.*;

import static org.mockito.Mockito.mock;
import static utils.ClassFinderUtils.*;

@RunWith(Parameterized.class)
public class InitStaticFieldTest {

    @Parameterized.Parameters(name= "{index}: class name: {0}")
    public static NameClass[] data() {
        String classPackage = "br.org.scadabr";
        String classPackage2 = "cc.radiuino.scadabr";
        String classPackage3 = "com.serotonin";
        String classPackage4 = "org.scada_lts";

        String classPackage5 = "org.scada_lts.mango.service";
        String classPackage6 = "org.scada_lts.permissions.service";
        String classPackage7 = "org.scada_lts.service";
        String classPackage8 = "org.scada_lts.dao";

        Set<NameClass> nameClasses = getDaoServiceClasses(classPackage, classPackage2, classPackage3, classPackage4);
        nameClasses.addAll(getClasses(classPackage5, classPackage6, classPackage7, classPackage8));

        return nameClasses.toArray(new NameClass[]{});
    }

    private final NameClass nameClass;

    public InitStaticFieldTest(NameClass nameClass) {
        this.nameClass = nameClass;
    }

    @Test
    public void when_init_static_field_then_ok(){
        mock(nameClass.getClazz());
    }
}
