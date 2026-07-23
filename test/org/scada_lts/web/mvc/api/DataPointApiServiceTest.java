package org.scada_lts.web.mvc.api;

import com.google.common.collect.Lists;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.internal.util.collections.Sets;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.web.mvc.api.datasources.SearchDataPointJson;
import utils.TestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class DataPointApiServiceTest {

    private static final DataPointVO dataPoint1Type1Settable = TestUtils.newPointSettable(1, 1, 1);
    private static final DataPointVO dataPoint2Type1Settable = TestUtils.newPointSettable(2, 1, 1);
    private static final DataPointVO dataPoint3Type1Settable = TestUtils.newPointSettable(3, 1, 1);
    private static final DataPointVO dataPoint4Type1Settable = TestUtils.newPointSettable(4, 1, 1);
    private static final DataPointVO dataPoint5Type1Settable = TestUtils.newPointSettable(5, 1, 1);
    private static final DataPointVO dataPoint6Type2Settable = TestUtils.newPointSettable(6, 1, 2);
    private static final DataPointVO dataPoint7Type2Settable = TestUtils.newPointSettable(7, 1, 2);
    private static final DataPointVO dataPoint8Type2Settable = TestUtils.newPointSettable(8, 1, 2);
    private static final DataPointVO dataPoint9Type2Settable = TestUtils.newPointSettable(9, 1, 2);
    private static final DataPointVO dataPoint10Type2Settable = TestUtils.newPointSettable(10, 1, 2);

    private static final DataPointVO dataPoint11Type1NonSettable = TestUtils.newPointNonSettable(11, 1, 1);
    private static final DataPointVO dataPoint12Type1NonSettable = TestUtils.newPointNonSettable(12, 1, 1);
    private static final DataPointVO dataPoint13Type1NonSettable = TestUtils.newPointNonSettable(13, 1, 1);
    private static final DataPointVO dataPoint14Type1NonSettable = TestUtils.newPointNonSettable(14, 1, 1);
    private static final DataPointVO dataPoint15Type1NonSettable = TestUtils.newPointNonSettable(15, 1, 1);
    private static final DataPointVO dataPoint16Type2NonSettable = TestUtils.newPointNonSettable(16, 1, 2);
    private static final DataPointVO dataPoint17Type2NonSettable = TestUtils.newPointNonSettable(17, 1, 2);
    private static final DataPointVO dataPoint18Type2NonSettable = TestUtils.newPointNonSettable(18, 1, 2);
    private static final DataPointVO dataPoint19Type2NonSettable = TestUtils.newPointNonSettable(19, 1, 2);
    private static final DataPointVO dataPoint20Type2NonSettable = TestUtils.newPointNonSettable(20, 1, 2);


    @Parameterized.Parameters(name= "{index}: {0} >>>>>>>>>> user: {1} >>>>>>>>>> sizeExpected: {2} >>>>>>>>>> dataPoints: {3}")
    public static Object[][] data() {
        User admin = TestUtils.newUser(1);
        admin.setAdmin(true);
        User nonAdmin = TestUtils.newUser(2);
        nonAdmin.setAdmin(false);
        nonAdmin.setDataPointProfilePermissions(Lists.newArrayList(
                new DataPointAccess(1, 2),
                new DataPointAccess(2, 2),
                new DataPointAccess(3, 2),
                new DataPointAccess(4, 2),
                new DataPointAccess(5, 2),
                new DataPointAccess(6, 2),
                new DataPointAccess(7, 2),
                new DataPointAccess(8, 1),
                new DataPointAccess(9, 2),
                new DataPointAccess(10, 2),
                new DataPointAccess(11, 2),
                new DataPointAccess(12, 2),
                new DataPointAccess(13, 2),
                new DataPointAccess(14, 2),
                new DataPointAccess(15, 2),
                new DataPointAccess(16, 2),
                new DataPointAccess(17, 2),
                new DataPointAccess(18, 2),
                new DataPointAccess(19, 2),
                new DataPointAccess(20, 2)
                ));

        return new Object[][] {

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(5).page(1).build(), admin, 5, List.of(dataPoint1Type1Settable, dataPoint10Type2Settable, dataPoint11Type1NonSettable, dataPoint12Type1NonSettable, dataPoint13Type1NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(5).page(2).build(), admin, 5, List.of(dataPoint14Type1NonSettable, dataPoint15Type1NonSettable, dataPoint16Type2NonSettable, dataPoint17Type2NonSettable, dataPoint18Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(5).page(3).build(), admin, 5, List.of(dataPoint19Type2NonSettable, dataPoint2Type1Settable, dataPoint20Type2NonSettable, dataPoint3Type1Settable, dataPoint4Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(5).page(4).build(), admin, 5, List.of(dataPoint5Type1Settable, dataPoint6Type2Settable, dataPoint7Type2Settable, dataPoint8Type2Settable, dataPoint9Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(5).page(5).build(), admin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(1).build(), admin, 3, List.of(dataPoint1Type1Settable, dataPoint10Type2Settable, dataPoint11Type1NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(2).build(), admin, 3, List.of(dataPoint12Type1NonSettable, dataPoint13Type1NonSettable, dataPoint14Type1NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(3).build(), admin, 3, List.of(dataPoint15Type1NonSettable, dataPoint16Type2NonSettable, dataPoint17Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(4).build(), admin, 3, List.of(dataPoint18Type2NonSettable, dataPoint19Type2NonSettable, dataPoint2Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(5).build(), admin, 3, List.of(dataPoint20Type2NonSettable, dataPoint3Type1Settable, dataPoint4Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(6).build(), admin, 3, List.of(dataPoint5Type1Settable, dataPoint6Type2Settable, dataPoint7Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(7).build(), admin, 2, List.of(dataPoint8Type2Settable, dataPoint9Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(8).build(), admin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(5).page(1).build(), nonAdmin, 5, List.of(dataPoint1Type1Settable, dataPoint10Type2Settable, dataPoint11Type1NonSettable, dataPoint12Type1NonSettable, dataPoint13Type1NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(5).page(2).build(), nonAdmin, 5, List.of(dataPoint14Type1NonSettable, dataPoint15Type1NonSettable, dataPoint16Type2NonSettable, dataPoint17Type2NonSettable, dataPoint18Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(5).page(3).build(), nonAdmin, 5, List.of(dataPoint19Type2NonSettable, dataPoint2Type1Settable, dataPoint20Type2NonSettable, dataPoint3Type1Settable, dataPoint4Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(5).page(4).build(), nonAdmin, 5, List.of(dataPoint5Type1Settable, dataPoint6Type2Settable, dataPoint7Type2Settable, dataPoint8Type2Settable, dataPoint9Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(5).page(5).build(), nonAdmin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(1).build(), nonAdmin, 3, List.of(dataPoint1Type1Settable, dataPoint10Type2Settable, dataPoint11Type1NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(2).build(), nonAdmin, 3, List.of(dataPoint12Type1NonSettable, dataPoint13Type1NonSettable, dataPoint14Type1NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(3).build(), nonAdmin, 3, List.of(dataPoint15Type1NonSettable, dataPoint16Type2NonSettable, dataPoint17Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(4).build(), nonAdmin, 3, List.of(dataPoint18Type2NonSettable, dataPoint19Type2NonSettable, dataPoint2Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(5).build(), nonAdmin, 3, List.of(dataPoint20Type2NonSettable, dataPoint3Type1Settable, dataPoint4Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(6).build(), nonAdmin, 3, List.of(dataPoint5Type1Settable, dataPoint6Type2Settable, dataPoint7Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(7).build(), nonAdmin, 2, List.of(dataPoint8Type2Settable, dataPoint9Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(8).build(), nonAdmin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1)).limit(5).page(1).build(), admin, 5, List.of(dataPoint1Type1Settable, dataPoint11Type1NonSettable, dataPoint12Type1NonSettable, dataPoint13Type1NonSettable, dataPoint14Type1NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1)).limit(5).page(2).build(), admin, 5, List.of(dataPoint15Type1NonSettable, dataPoint2Type1Settable, dataPoint3Type1Settable, dataPoint4Type1Settable, dataPoint5Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1)).limit(5).page(3).build(), admin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1)).limit(3).page(1).build(), admin, 3, List.of(dataPoint1Type1Settable, dataPoint11Type1NonSettable, dataPoint12Type1NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1)).limit(3).page(2).build(), admin, 3, List.of(dataPoint13Type1NonSettable, dataPoint14Type1NonSettable, dataPoint15Type1NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1)).limit(3).page(3).build(), admin, 3, List.of(dataPoint2Type1Settable, dataPoint3Type1Settable, dataPoint4Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1)).limit(3).page(4).build(), admin, 1, List.of(dataPoint5Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1)).limit(3).page(5).build(), admin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1)).limit(5).page(1).build(), nonAdmin, 5, List.of(dataPoint1Type1Settable, dataPoint11Type1NonSettable, dataPoint12Type1NonSettable, dataPoint13Type1NonSettable, dataPoint14Type1NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1)).limit(5).page(2).build(), nonAdmin, 5, List.of(dataPoint15Type1NonSettable, dataPoint2Type1Settable, dataPoint3Type1Settable, dataPoint4Type1Settable, dataPoint5Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1)).limit(5).page(3).build(), nonAdmin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1)).limit(3).page(1).build(), nonAdmin, 3, List.of(dataPoint1Type1Settable, dataPoint11Type1NonSettable, dataPoint12Type1NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1)).limit(3).page(2).build(), nonAdmin, 3, List.of(dataPoint13Type1NonSettable, dataPoint14Type1NonSettable, dataPoint15Type1NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1)).limit(3).page(3).build(), nonAdmin, 3, List.of(dataPoint2Type1Settable, dataPoint3Type1Settable, dataPoint4Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1)).limit(3).page(4).build(), nonAdmin, 1, List.of(dataPoint5Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1)).limit(3).page(5).build(), nonAdmin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(2)).limit(5).page(1).build(), admin, 5, List.of(dataPoint10Type2Settable, dataPoint16Type2NonSettable, dataPoint17Type2NonSettable, dataPoint18Type2NonSettable, dataPoint19Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(2)).limit(5).page(2).build(), admin, 5, List.of(dataPoint20Type2NonSettable, dataPoint6Type2Settable, dataPoint7Type2Settable, dataPoint8Type2Settable, dataPoint9Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(2)).limit(5).page(3).build(), admin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(2)).limit(3).page(1).build(), admin, 3, List.of(dataPoint10Type2Settable, dataPoint16Type2NonSettable, dataPoint17Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(2)).limit(3).page(2).build(), admin, 3, List.of(dataPoint18Type2NonSettable, dataPoint19Type2NonSettable, dataPoint20Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(2)).limit(3).page(3).build(), admin, 3, List.of(dataPoint6Type2Settable, dataPoint7Type2Settable, dataPoint8Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(2)).limit(3).page(4).build(), admin, 1, List.of(dataPoint9Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(2)).limit(3).page(5).build(), admin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(2)).limit(5).page(1).build(), nonAdmin, 5, List.of(dataPoint10Type2Settable, dataPoint16Type2NonSettable, dataPoint17Type2NonSettable, dataPoint18Type2NonSettable, dataPoint19Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(2)).limit(5).page(2).build(), nonAdmin, 5, List.of(dataPoint20Type2NonSettable, dataPoint6Type2Settable, dataPoint7Type2Settable, dataPoint8Type2Settable, dataPoint9Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(2)).limit(5).page(3).build(), nonAdmin, 0, List.of()},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(2)).limit(3).page(1).build(), nonAdmin, 3, List.of(dataPoint10Type2Settable, dataPoint16Type2NonSettable, dataPoint17Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(2)).limit(3).page(2).build(), nonAdmin, 3, List.of(dataPoint18Type2NonSettable, dataPoint19Type2NonSettable, dataPoint20Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(2)).limit(3).page(3).build(), nonAdmin, 3, List.of(dataPoint6Type2Settable, dataPoint7Type2Settable, dataPoint8Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(2)).limit(3).page(4).build(), nonAdmin, 1, List.of(dataPoint9Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(2)).limit(3).page(5).build(), nonAdmin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(5).page(1).build(), admin, 5, List.of(dataPoint1Type1Settable, dataPoint10Type2Settable, dataPoint11Type1NonSettable, dataPoint12Type1NonSettable, dataPoint13Type1NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(5).page(2).build(), admin, 5, List.of(dataPoint14Type1NonSettable, dataPoint15Type1NonSettable, dataPoint16Type2NonSettable, dataPoint17Type2NonSettable, dataPoint18Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(5).page(3).build(), admin, 5, List.of(dataPoint19Type2NonSettable, dataPoint2Type1Settable, dataPoint20Type2NonSettable, dataPoint3Type1Settable, dataPoint4Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(5).page(4).build(), admin, 5, List.of(dataPoint5Type1Settable, dataPoint6Type2Settable, dataPoint7Type2Settable, dataPoint8Type2Settable, dataPoint9Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(5).page(5).build(), admin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(1).build(), admin, 3, List.of(dataPoint1Type1Settable, dataPoint10Type2Settable, dataPoint11Type1NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(2).build(), admin, 3, List.of(dataPoint12Type1NonSettable, dataPoint13Type1NonSettable, dataPoint14Type1NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(3).build(), admin, 3, List.of(dataPoint15Type1NonSettable, dataPoint16Type2NonSettable, dataPoint17Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(4).build(), admin, 3, List.of(dataPoint18Type2NonSettable, dataPoint19Type2NonSettable, dataPoint2Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(5).build(), admin, 3, List.of(dataPoint20Type2NonSettable, dataPoint3Type1Settable, dataPoint4Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(6).build(), admin, 3, List.of(dataPoint5Type1Settable, dataPoint6Type2Settable, dataPoint7Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(7).build(), admin, 2, List.of(dataPoint8Type2Settable, dataPoint9Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(8).build(), admin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(5).page(1).build(), nonAdmin, 5, List.of(dataPoint1Type1Settable, dataPoint10Type2Settable, dataPoint11Type1NonSettable, dataPoint12Type1NonSettable, dataPoint13Type1NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(5).page(2).build(), nonAdmin, 5, List.of(dataPoint14Type1NonSettable, dataPoint15Type1NonSettable, dataPoint16Type2NonSettable, dataPoint17Type2NonSettable, dataPoint18Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(5).page(3).build(), nonAdmin, 5, List.of(dataPoint19Type2NonSettable, dataPoint2Type1Settable, dataPoint20Type2NonSettable, dataPoint3Type1Settable, dataPoint4Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(5).page(4).build(), nonAdmin, 5, List.of(dataPoint5Type1Settable, dataPoint6Type2Settable, dataPoint7Type2Settable, dataPoint8Type2Settable, dataPoint9Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(5).page(5).build(), nonAdmin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(1).build(), nonAdmin, 3, List.of(dataPoint1Type1Settable, dataPoint10Type2Settable, dataPoint11Type1NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(2).build(), nonAdmin, 3, List.of(dataPoint12Type1NonSettable, dataPoint13Type1NonSettable, dataPoint14Type1NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(3).build(), nonAdmin, 3, List.of(dataPoint15Type1NonSettable, dataPoint16Type2NonSettable, dataPoint17Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(4).build(), nonAdmin, 3, List.of(dataPoint18Type2NonSettable, dataPoint19Type2NonSettable, dataPoint2Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(5).build(), nonAdmin, 3, List.of(dataPoint20Type2NonSettable, dataPoint3Type1Settable, dataPoint4Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(6).build(), nonAdmin, 3, List.of(dataPoint5Type1Settable, dataPoint6Type2Settable, dataPoint7Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(7).build(), nonAdmin, 2, List.of(dataPoint8Type2Settable, dataPoint9Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(8).build(), nonAdmin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(5).page(1).settable(true).build(), admin, 5, List.of(dataPoint1Type1Settable, dataPoint10Type2Settable, dataPoint2Type1Settable, dataPoint3Type1Settable, dataPoint4Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(5).page(2).settable(true).build(), admin, 5, List.of(dataPoint5Type1Settable, dataPoint6Type2Settable, dataPoint7Type2Settable, dataPoint8Type2Settable, dataPoint9Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(5).page(3).settable(true).build(), admin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(1).settable(true).build(), admin, 3, List.of(dataPoint1Type1Settable, dataPoint10Type2Settable, dataPoint2Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(2).settable(true).build(), admin, 3, List.of(dataPoint3Type1Settable, dataPoint4Type1Settable, dataPoint5Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(3).settable(true).build(), admin, 3, List.of(dataPoint6Type2Settable, dataPoint7Type2Settable, dataPoint8Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(4).settable(true).build(), admin, 1, List.of(dataPoint9Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(5).settable(true).build(), admin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(5).page(1).settable(true).build(), nonAdmin, 5, List.of(dataPoint1Type1Settable, dataPoint10Type2Settable, dataPoint2Type1Settable, dataPoint3Type1Settable, dataPoint4Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(5).page(2).settable(true).build(), nonAdmin, 4, List.of(dataPoint5Type1Settable, dataPoint6Type2Settable, dataPoint7Type2Settable, dataPoint9Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(5).page(3).settable(true).build(), nonAdmin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(1).settable(true).build(), nonAdmin, 3, List.of(dataPoint1Type1Settable, dataPoint10Type2Settable, dataPoint2Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(2).settable(true).build(), nonAdmin, 3, List.of(dataPoint3Type1Settable, dataPoint4Type1Settable, dataPoint5Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(3).settable(true).build(), nonAdmin, 3, List.of(dataPoint6Type2Settable, dataPoint7Type2Settable, dataPoint9Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(4).settable(true).build(), nonAdmin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(5).page(1).settable(true).build(), admin, 5, List.of(dataPoint1Type1Settable, dataPoint10Type2Settable, dataPoint2Type1Settable, dataPoint3Type1Settable, dataPoint4Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(5).page(2).settable(true).build(), admin, 5, List.of(dataPoint5Type1Settable, dataPoint6Type2Settable, dataPoint7Type2Settable, dataPoint8Type2Settable, dataPoint9Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(5).page(3).settable(true).build(), admin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(1).settable(true).build(), admin, 3, List.of(dataPoint1Type1Settable, dataPoint10Type2Settable, dataPoint2Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(2).settable(true).build(), admin, 3, List.of(dataPoint3Type1Settable, dataPoint4Type1Settable, dataPoint5Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(3).settable(true).build(), admin, 3, List.of(dataPoint6Type2Settable, dataPoint7Type2Settable, dataPoint8Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(4).settable(true).build(), admin, 1, List.of(dataPoint9Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(5).settable(true).build(), admin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(5).page(1).settable(true).build(), nonAdmin, 5, List.of(dataPoint1Type1Settable, dataPoint10Type2Settable, dataPoint2Type1Settable, dataPoint3Type1Settable, dataPoint4Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(5).page(2).settable(true).build(), nonAdmin, 4, List.of(dataPoint5Type1Settable, dataPoint6Type2Settable, dataPoint7Type2Settable, dataPoint9Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(5).page(3).settable(true).build(), nonAdmin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(1).settable(true).build(), nonAdmin, 3, List.of(dataPoint1Type1Settable, dataPoint10Type2Settable, dataPoint2Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(2).settable(true).build(), nonAdmin, 3, List.of(dataPoint3Type1Settable, dataPoint4Type1Settable, dataPoint5Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(3).settable(true).build(), nonAdmin, 3, List.of(dataPoint6Type2Settable, dataPoint7Type2Settable, dataPoint9Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(4).settable(true).build(), nonAdmin, 0, List.of()},


            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(5).page(1).settable(false).build(), admin, 5, List.of(dataPoint11Type1NonSettable, dataPoint12Type1NonSettable, dataPoint13Type1NonSettable, dataPoint14Type1NonSettable, dataPoint15Type1NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(5).page(2).settable(false).build(), admin, 5, List.of(dataPoint16Type2NonSettable, dataPoint17Type2NonSettable, dataPoint18Type2NonSettable, dataPoint19Type2NonSettable, dataPoint20Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(5).page(3).settable(false).build(), admin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(1).settable(false).build(), admin, 3, List.of(dataPoint11Type1NonSettable, dataPoint12Type1NonSettable, dataPoint13Type1NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(2).settable(false).build(), admin, 3, List.of(dataPoint14Type1NonSettable, dataPoint15Type1NonSettable, dataPoint16Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(3).settable(false).build(), admin, 3, List.of(dataPoint17Type2NonSettable, dataPoint18Type2NonSettable, dataPoint19Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(4).settable(false).build(), admin, 1, List.of(dataPoint20Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(5).settable(false).build(), admin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(5).page(1).settable(false).build(), nonAdmin, 5, List.of(dataPoint11Type1NonSettable, dataPoint12Type1NonSettable, dataPoint13Type1NonSettable, dataPoint14Type1NonSettable, dataPoint15Type1NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(5).page(2).settable(false).build(), nonAdmin, 5, List.of(dataPoint16Type2NonSettable, dataPoint17Type2NonSettable, dataPoint18Type2NonSettable, dataPoint19Type2NonSettable, dataPoint20Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(5).page(3).settable(false).build(), nonAdmin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(1).settable(false).build(), nonAdmin, 3, List.of(dataPoint11Type1NonSettable, dataPoint12Type1NonSettable, dataPoint13Type1NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(2).settable(false).build(), nonAdmin, 3, List.of(dataPoint14Type1NonSettable, dataPoint15Type1NonSettable, dataPoint16Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(3).settable(false).build(), nonAdmin, 3, List.of(dataPoint17Type2NonSettable, dataPoint18Type2NonSettable, dataPoint19Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(4).settable(false).build(), nonAdmin, 1, List.of(dataPoint20Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(5).settable(false).build(), nonAdmin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(5).page(1).settable(false).build(), admin, 5, List.of(dataPoint11Type1NonSettable, dataPoint12Type1NonSettable, dataPoint13Type1NonSettable, dataPoint14Type1NonSettable, dataPoint15Type1NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(5).page(2).settable(false).build(), admin, 5, List.of(dataPoint16Type2NonSettable, dataPoint17Type2NonSettable, dataPoint18Type2NonSettable, dataPoint19Type2NonSettable, dataPoint20Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(5).page(3).settable(false).build(), admin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(1).settable(false).build(), admin, 3, List.of(dataPoint11Type1NonSettable, dataPoint12Type1NonSettable, dataPoint13Type1NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(2).settable(false).build(), admin, 3, List.of(dataPoint14Type1NonSettable, dataPoint15Type1NonSettable, dataPoint16Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(3).settable(false).build(), admin, 3, List.of(dataPoint17Type2NonSettable, dataPoint18Type2NonSettable, dataPoint19Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(4).settable(false).build(), admin, 1, List.of(dataPoint20Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(5).settable(false).build(), admin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(5).page(1).settable(false).build(), nonAdmin, 5, List.of(dataPoint11Type1NonSettable, dataPoint12Type1NonSettable, dataPoint13Type1NonSettable, dataPoint14Type1NonSettable, dataPoint15Type1NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(5).page(2).settable(false).build(), nonAdmin, 5, List.of(dataPoint16Type2NonSettable, dataPoint17Type2NonSettable, dataPoint18Type2NonSettable, dataPoint19Type2NonSettable, dataPoint20Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(5).page(3).settable(false).build(), nonAdmin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(1).settable(false).build(), nonAdmin, 3, List.of(dataPoint11Type1NonSettable, dataPoint12Type1NonSettable, dataPoint13Type1NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(2).settable(false).build(), nonAdmin, 3, List.of(dataPoint14Type1NonSettable, dataPoint15Type1NonSettable, dataPoint16Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(3).settable(false).build(), nonAdmin, 3, List.of(dataPoint17Type2NonSettable, dataPoint18Type2NonSettable, dataPoint19Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(4).settable(false).build(), nonAdmin, 1, List.of(dataPoint20Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(5).settable(false).build(), nonAdmin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(5).page(1).settable(true).excludeIds(Set.of(18, 20, 6, 9)).build(), admin, 5, List.of(dataPoint1Type1Settable, dataPoint10Type2Settable, dataPoint2Type1Settable, dataPoint3Type1Settable, dataPoint4Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(5).page(2).settable(true).excludeIds(Set.of(18, 20, 6, 9)).build(), admin, 3, List.of(dataPoint5Type1Settable, dataPoint7Type2Settable, dataPoint8Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(5).page(3).settable(true).excludeIds(Set.of(18, 20, 6, 9)).build(), admin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(1).settable(true).excludeIds(Set.of(18, 20, 6, 9)).build(), admin, 3, List.of(dataPoint1Type1Settable, dataPoint10Type2Settable, dataPoint2Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(2).settable(true).excludeIds(Set.of(18, 20, 6, 9)).build(), admin, 3, List.of(dataPoint3Type1Settable, dataPoint4Type1Settable, dataPoint5Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(3).settable(true).excludeIds(Set.of(18, 20, 6, 9)).build(), admin, 2, List.of(dataPoint7Type2Settable, dataPoint8Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(4).settable(true).excludeIds(Set.of(18, 20, 6, 9)).build(), admin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(5).page(1).settable(true).excludeIds(Set.of(18, 20, 6, 9)).build(), nonAdmin, 5, List.of(dataPoint1Type1Settable, dataPoint10Type2Settable, dataPoint2Type1Settable, dataPoint3Type1Settable, dataPoint4Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(5).page(2).settable(true).excludeIds(Set.of(18, 20, 6, 9)).build(), nonAdmin, 2, List.of(dataPoint5Type1Settable, dataPoint7Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(5).page(3).settable(true).excludeIds(Set.of(18, 20, 6, 9)).build(), nonAdmin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(1).settable(true).excludeIds(Set.of(18, 20, 6, 9)).build(), nonAdmin, 3, List.of(dataPoint1Type1Settable, dataPoint10Type2Settable, dataPoint2Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(2).settable(true).excludeIds(Set.of(18, 20, 6, 9)).build(), nonAdmin, 3, List.of(dataPoint3Type1Settable, dataPoint4Type1Settable, dataPoint5Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(3).settable(true).excludeIds(Set.of(18, 20, 6, 9)).build(), nonAdmin, 1, List.of(dataPoint7Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(4).settable(true).excludeIds(Set.of(18, 20, 6, 9)).build(), nonAdmin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(5).page(1).settable(true).excludeIds(Set.of(18, 20, 6, 9)).build(), admin, 5, List.of(dataPoint1Type1Settable, dataPoint10Type2Settable, dataPoint2Type1Settable, dataPoint3Type1Settable, dataPoint4Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(5).page(2).settable(true).excludeIds(Set.of(18, 20, 6, 9)).build(), admin, 3, List.of(dataPoint5Type1Settable, dataPoint7Type2Settable, dataPoint8Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(5).page(3).settable(true).excludeIds(Set.of(18, 20, 6, 9)).build(), admin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(1).settable(true).excludeIds(Set.of(18, 20, 6, 9)).build(), admin, 3, List.of(dataPoint1Type1Settable, dataPoint10Type2Settable, dataPoint2Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(2).settable(true).excludeIds(Set.of(18, 20, 6, 9)).build(), admin, 3, List.of(dataPoint3Type1Settable, dataPoint4Type1Settable, dataPoint5Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(3).settable(true).excludeIds(Set.of(18, 20, 6, 9)).build(), admin, 2, List.of(dataPoint7Type2Settable, dataPoint8Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(4).settable(true).excludeIds(Set.of(18, 20, 6, 9)).build(), admin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(5).page(1).settable(true).excludeIds(Set.of(18, 20, 6, 9)).build(), nonAdmin, 5, List.of(dataPoint1Type1Settable, dataPoint10Type2Settable, dataPoint2Type1Settable, dataPoint3Type1Settable, dataPoint4Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(5).page(2).settable(true).excludeIds(Set.of(18, 20, 6, 9)).build(), nonAdmin, 2, List.of(dataPoint5Type1Settable, dataPoint7Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(5).page(3).settable(true).excludeIds(Set.of(18, 20, 6, 9)).build(), nonAdmin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(1).settable(true).excludeIds(Set.of(18, 20, 6, 9)).build(), nonAdmin, 3, List.of(dataPoint1Type1Settable, dataPoint10Type2Settable, dataPoint2Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(2).settable(true).excludeIds(Set.of(18, 20, 6, 9)).build(), nonAdmin, 3, List.of(dataPoint3Type1Settable, dataPoint4Type1Settable, dataPoint5Type1Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(3).settable(true).excludeIds(Set.of(18, 20, 6, 9)).build(), nonAdmin, 1, List.of(dataPoint7Type2Settable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(4).settable(true).excludeIds(Set.of(18, 20, 6, 9)).build(), nonAdmin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(5).page(1).settable(false).excludeIds(Set.of(18, 20, 6, 9)).build(), admin, 5, List.of(dataPoint11Type1NonSettable, dataPoint12Type1NonSettable, dataPoint13Type1NonSettable, dataPoint14Type1NonSettable, dataPoint15Type1NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(5).page(2).settable(false).excludeIds(Set.of(18, 20, 6, 9)).build(), admin, 3, List.of(dataPoint16Type2NonSettable, dataPoint17Type2NonSettable, dataPoint19Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(5).page(3).settable(false).excludeIds(Set.of(18, 20, 6, 9)).build(), admin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(1).settable(false).excludeIds(Set.of(18, 20, 6, 9)).build(), admin, 3, List.of(dataPoint11Type1NonSettable, dataPoint12Type1NonSettable, dataPoint13Type1NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(2).settable(false).excludeIds(Set.of(18, 20, 6, 9)).build(), admin, 3, List.of(dataPoint14Type1NonSettable, dataPoint15Type1NonSettable, dataPoint16Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(3).settable(false).excludeIds(Set.of(18, 20, 6, 9)).build(), admin, 2, List.of(dataPoint17Type2NonSettable, dataPoint19Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(4).settable(false).excludeIds(Set.of(18, 20, 6, 9)).build(), admin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(5).page(1).settable(false).excludeIds(Set.of(18, 20, 6, 9)).build(), nonAdmin, 5, List.of(dataPoint11Type1NonSettable, dataPoint12Type1NonSettable, dataPoint13Type1NonSettable, dataPoint14Type1NonSettable, dataPoint15Type1NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(5).page(2).settable(false).excludeIds(Set.of(18, 20, 6, 9)).build(), nonAdmin, 3, List.of(dataPoint16Type2NonSettable, dataPoint17Type2NonSettable, dataPoint19Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(5).page(3).settable(false).excludeIds(Set.of(18, 20, 6, 9)).build(), nonAdmin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(1).settable(false).excludeIds(Set.of(18, 20, 6, 9)).build(), nonAdmin, 3, List.of(dataPoint11Type1NonSettable, dataPoint12Type1NonSettable, dataPoint13Type1NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(2).settable(false).excludeIds(Set.of(18, 20, 6, 9)).build(), nonAdmin, 3, List.of(dataPoint14Type1NonSettable, dataPoint15Type1NonSettable, dataPoint16Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(3).settable(false).excludeIds(Set.of(18, 20, 6, 9)).build(), nonAdmin, 2, List.of(dataPoint17Type2NonSettable, dataPoint19Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of(1, 2)).limit(3).page(4).settable(false).excludeIds(Set.of(18, 20, 6, 9)).build(), nonAdmin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(5).page(1).settable(false).excludeIds(Set.of(18, 20, 6, 9)).build(), admin, 5, List.of(dataPoint11Type1NonSettable, dataPoint12Type1NonSettable, dataPoint13Type1NonSettable, dataPoint14Type1NonSettable, dataPoint15Type1NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(5).page(2).settable(false).excludeIds(Set.of(18, 20, 6, 9)).build(), admin, 3, List.of(dataPoint16Type2NonSettable, dataPoint17Type2NonSettable, dataPoint19Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(5).page(3).settable(false).excludeIds(Set.of(18, 20, 6, 9)).build(), admin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(1).settable(false).excludeIds(Set.of(18, 20, 6, 9)).build(), admin, 3, List.of(dataPoint11Type1NonSettable, dataPoint12Type1NonSettable, dataPoint13Type1NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(2).settable(false).excludeIds(Set.of(18, 20, 6, 9)).build(), admin, 3, List.of(dataPoint14Type1NonSettable, dataPoint15Type1NonSettable, dataPoint16Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(3).settable(false).excludeIds(Set.of(18, 20, 6, 9)).build(), admin, 2, List.of(dataPoint17Type2NonSettable, dataPoint19Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(4).settable(false).excludeIds(Set.of(18, 20, 6, 9)).build(), admin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(5).page(1).settable(false).excludeIds(Set.of(18, 20, 6, 9)).build(), nonAdmin, 5, List.of(dataPoint11Type1NonSettable, dataPoint12Type1NonSettable, dataPoint13Type1NonSettable, dataPoint14Type1NonSettable, dataPoint15Type1NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(5).page(2).settable(false).excludeIds(Set.of(18, 20, 6, 9)).build(), nonAdmin, 3, List.of(dataPoint16Type2NonSettable, dataPoint17Type2NonSettable, dataPoint19Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(5).page(3).settable(false).excludeIds(Set.of(18, 20, 6, 9)).build(), nonAdmin, 0, List.of()},

            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(1).settable(false).excludeIds(Set.of(18, 20, 6, 9)).build(), nonAdmin, 3, List.of(dataPoint11Type1NonSettable, dataPoint12Type1NonSettable, dataPoint13Type1NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(2).settable(false).excludeIds(Set.of(18, 20, 6, 9)).build(), nonAdmin, 3, List.of(dataPoint14Type1NonSettable, dataPoint15Type1NonSettable, dataPoint16Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(3).settable(false).excludeIds(Set.of(18, 20, 6, 9)).build(), nonAdmin, 2, List.of(dataPoint17Type2NonSettable, dataPoint19Type2NonSettable)},
            new Object[]{SearchDataPointJson.builder().dataTypes(Set.of()).limit(3).page(4).settable(false).excludeIds(Set.of(18, 20, 6, 9)).build(), nonAdmin, 0, List.of()},
        };
    }
    private final SearchDataPointJson searchDataPointJson;
    private final User user;
    private final int size;
    private final List<DataPointVO> dataPoints;

    public DataPointApiServiceTest(SearchDataPointJson searchDataPointJson, User user, int size, List<DataPointVO> dataPoints) {
        this.searchDataPointJson = searchDataPointJson;
        this.user = user;
        this.size = size;
        this.dataPoints = dataPoints;
    }

    private HttpServletRequest requestMock;
    private DataPointApiService dataPointApiService;

    @Before
    public void config() {
        requestMock = mock(HttpServletRequest.class);
        when(requestMock.getAttribute(anyString())).thenReturn(user);

        DataPointService dataPointServiceMock = mock(DataPointService.class);
        DataSourceApiService dataSourceApiServiceMock = mock(DataSourceApiService.class);

        List<DataPointVO> points = List.of(dataPoint1Type1Settable, dataPoint2Type1Settable, dataPoint3Type1Settable, dataPoint4Type1Settable, dataPoint5Type1Settable,
                dataPoint6Type2Settable, dataPoint7Type2Settable, dataPoint8Type2Settable, dataPoint9Type2Settable, dataPoint10Type2Settable,
                dataPoint11Type1NonSettable, dataPoint12Type1NonSettable, dataPoint13Type1NonSettable, dataPoint14Type1NonSettable, dataPoint15Type1NonSettable,
                dataPoint16Type2NonSettable, dataPoint17Type2NonSettable, dataPoint18Type2NonSettable, dataPoint19Type2NonSettable, dataPoint20Type2NonSettable);

        when(dataPointServiceMock.getDataPoints(any(), any(), anyBoolean(), eq(-1), eq(-1)))
                .thenReturn(points);

        when(dataPointServiceMock.getDataPoints(any(), any(), anyBoolean(), eq(1), eq(5)))
                .thenReturn(List.of(dataPoint1Type1Settable, dataPoint10Type2Settable, dataPoint11Type1NonSettable, dataPoint12Type1NonSettable, dataPoint13Type1NonSettable));

        when(dataPointServiceMock.getDataPoints(any(), any(), anyBoolean(), eq(2), eq(5)))
                .thenReturn(List.of(dataPoint14Type1NonSettable, dataPoint15Type1NonSettable, dataPoint16Type2NonSettable, dataPoint17Type2NonSettable, dataPoint18Type2NonSettable));

        when(dataPointServiceMock.getDataPoints(any(), any(), anyBoolean(), eq(3), eq(5)))
                .thenReturn(List.of(dataPoint19Type2NonSettable, dataPoint2Type1Settable, dataPoint20Type2NonSettable, dataPoint3Type1Settable, dataPoint4Type1Settable));

        when(dataPointServiceMock.getDataPoints(any(), any(), anyBoolean(), eq(4), eq(5)))
                .thenReturn(List.of(dataPoint5Type1Settable, dataPoint6Type2Settable, dataPoint7Type2Settable, dataPoint8Type2Settable, dataPoint9Type2Settable));

        when(dataPointServiceMock.getDataPoints(any(), any(), anyBoolean(), eq(5), eq(5)))
                .thenReturn(List.of());

        when(dataPointServiceMock.getDataPoints(any(), any(), anyBoolean(), eq(1), eq(3)))
                .thenReturn(List.of(dataPoint1Type1Settable, dataPoint10Type2Settable, dataPoint11Type1NonSettable));

        when(dataPointServiceMock.getDataPoints(any(), any(), anyBoolean(), eq(2), eq(3)))
                .thenReturn(List.of(dataPoint12Type1NonSettable, dataPoint13Type1NonSettable, dataPoint14Type1NonSettable));

        when(dataPointServiceMock.getDataPoints(any(), any(), anyBoolean(), eq(3), eq(3)))
                .thenReturn(List.of(dataPoint15Type1NonSettable, dataPoint16Type2NonSettable, dataPoint17Type2NonSettable));

        when(dataPointServiceMock.getDataPoints(any(), any(), anyBoolean(), eq(4), eq(3)))
                .thenReturn(List.of(dataPoint18Type2NonSettable, dataPoint19Type2NonSettable, dataPoint2Type1Settable));

        when(dataPointServiceMock.getDataPoints(any(), any(), anyBoolean(), eq(5), eq(3)))
                .thenReturn(List.of(dataPoint20Type2NonSettable, dataPoint3Type1Settable, dataPoint4Type1Settable));

        when(dataPointServiceMock.getDataPoints(any(), any(), anyBoolean(), eq(6), eq(3)))
                .thenReturn(List.of(dataPoint5Type1Settable, dataPoint6Type2Settable, dataPoint7Type2Settable));

        when(dataPointServiceMock.getDataPoints(any(), any(), anyBoolean(), eq(7), eq(3)))
                .thenReturn(List.of(dataPoint8Type2Settable, dataPoint9Type2Settable));

        when(dataPointServiceMock.getDataPoints(any(), any(), anyBoolean(), eq(8), eq(3)))
                .thenReturn(List.of());

        when(dataPointServiceMock.getDataPointsWithAccess(any())).thenAnswer(a -> List.of(dataPoint1Type1Settable, dataPoint2Type1Settable, dataPoint3Type1Settable, dataPoint4Type1Settable, dataPoint5Type1Settable,
                dataPoint6Type2Settable, dataPoint7Type2Settable, dataPoint8Type2Settable, dataPoint9Type2Settable, dataPoint10Type2Settable,
                dataPoint11Type1NonSettable, dataPoint12Type1NonSettable, dataPoint13Type1NonSettable, dataPoint14Type1NonSettable, dataPoint15Type1NonSettable,
                dataPoint16Type2NonSettable, dataPoint17Type2NonSettable, dataPoint18Type2NonSettable, dataPoint19Type2NonSettable, dataPoint20Type2NonSettable));

        dataPointApiService = new DataPointApiService(dataPointServiceMock, dataSourceApiServiceMock);
    }

    @Test
    public void searchDataPoint_size() {

        List<DataPointVO> result = dataPointApiService.searchDataPoint(requestMock, searchDataPointJson);

        Assert.assertEquals(size, result.size());

    }

    @Test
    public void searchDataPoint_points() {

        List<DataPointVO> result = dataPointApiService.searchDataPoint(requestMock, searchDataPointJson);

        Assert.assertEquals(dataPoints, result);

    }
}