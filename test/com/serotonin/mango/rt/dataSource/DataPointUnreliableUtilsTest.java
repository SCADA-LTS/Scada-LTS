package com.serotonin.mango.rt.dataSource;

import br.org.scadabr.db.utils.TestUtils;
import com.serotonin.db.IntValuePair;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueCache;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataSource.meta.MetaPointLocatorRT;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.rt.dataSource.virtual.VirtualPointLocatorRT;
import com.serotonin.mango.vo.dataSource.meta.MetaPointLocatorVO;
import com.serotonin.mango.vo.dataSource.virtual.ChangeTypeVO;
import com.serotonin.mango.vo.dataSource.virtual.NoChangeVO;
import com.serotonin.mango.web.dwr.MiscDwr;
import com.serotonin.web.content.ContentGenerator;
import org.directwebremoting.WebContextFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.scada_lts.dao.SystemSettingsDAO;
import utils.mock.MockUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.serotonin.mango.rt.dataSource.DataPointUnreliableUtils.*;
import static org.mockito.ArgumentMatchers.*;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({WebContextFactory.class, Common.class, MiscDwr.class, SystemSettingsDAO.class,
        ContentGenerator.class, PointValueCache.class, DataPointRT.class})
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "com.sun.org.apache.xalan.*",
        "javax.activation.*", "javax.management.*"})
public class DataPointUnreliableUtilsTest {

    private List<DataPointRT> metaDataPoints;


    @Before
    public void config() throws Exception {
        RuntimeManager runtimeManager = mock(RuntimeManager.class);
        metaDataPoints = new ArrayList<>();
        when(runtimeManager.getRunningMetaDataPoints(anyInt())).thenReturn(metaDataPoints);
        MockUtils.configMock(runtimeManager, new User());
        PointValueCache pointValueCache = mock(PointValueCache.class);
        whenNew(PointValueCache.class)
                .withArguments(anyInt(), anyInt())
                .thenReturn(pointValueCache);

        MetaPointLocatorVO metaPointLocatorVO = new MetaPointLocatorVO();
        metaPointLocatorVO.setContext(Arrays.asList(new IntValuePair(11, "")));
        MetaPointLocatorRT metaPointLocatorRT = new MetaPointLocatorRT(metaPointLocatorVO);

        DataPointRT dataPointRT1 = new DataPointRT(TestUtils.newPointSettable(111,-1), metaPointLocatorRT);
        DataPointRT dataPointRT2 = new DataPointRT(TestUtils.newPointSettable(112,-1), new MetaPointLocatorRT(new MetaPointLocatorVO()));
        DataPointRT dataPointRT3 = new DataPointRT(TestUtils.newPointSettable(113,-1), new MetaPointLocatorRT(new MetaPointLocatorVO()));
        metaDataPoints.addAll(Arrays.asList(dataPointRT1, dataPointRT2, dataPointRT3));
    }


    @Test
    public void when_setUnreliableDataPoint_with_point_then_unreliable() {
        //given:
        ChangeTypeVO changeTypeVO = new NoChangeVO();
        DataPointRT dataPointRT1 = new DataPointRT(TestUtils.newPointSettable(110,-1),
                new VirtualPointLocatorRT(changeTypeVO.createRuntime(), MangoValue.objectToValue(1), true));

        //when:
        setUnreliableDataPoint(dataPointRT1);

        //then:
        Assert.assertEquals(true, dataPointRT1.getAttribute(DataPointUnreliableUtils.ATTR_UNRELIABLE_KEY));
    }

    @Test
    public void when_setUnreliableDataPoints_with_points_then_unreliable() {
        //given:
        ChangeTypeVO changeTypeVO = new NoChangeVO();
        DataPointRT dataPointRT1 = new DataPointRT(TestUtils.newPointSettable(110,-1),
                new VirtualPointLocatorRT(changeTypeVO.createRuntime(), MangoValue.objectToValue(1), true));
        DataPointRT dataPointRT2 = new DataPointRT(TestUtils.newPointSettable(109,-1),
                new VirtualPointLocatorRT(changeTypeVO.createRuntime(), MangoValue.objectToValue(1), true));
        List<DataPointRT> dataPoints = Arrays.asList(dataPointRT1, dataPointRT2);

        //when:
        setUnreliableDataPoints(dataPoints);

        //then:
        Assert.assertEquals(true, dataPointRT1.getAttribute(DataPointUnreliableUtils.ATTR_UNRELIABLE_KEY));
        Assert.assertEquals(true, dataPointRT2.getAttribute(DataPointUnreliableUtils.ATTR_UNRELIABLE_KEY));
    }

    @Test
    public void when_resetUnreliableDataPoints_with_point_then_reliable() {
        //given:
        ChangeTypeVO changeTypeVO = new NoChangeVO();
        DataPointRT dataPointRT1 = new DataPointRT(TestUtils.newPointSettable(110,-1),
                new VirtualPointLocatorRT(changeTypeVO.createRuntime(), MangoValue.objectToValue(1), true));

        //when:
        resetUnreliableDataPoint(dataPointRT1);

        //then:
        Assert.assertEquals(false, dataPointRT1.getAttribute(DataPointUnreliableUtils.ATTR_UNRELIABLE_KEY));
    }

    @Test
    public void when_resetUnreliableDataPoints_with_points_then_reliable() {
        //given:
        ChangeTypeVO changeTypeVO = new NoChangeVO();
        DataPointRT dataPointRT1 = new DataPointRT(TestUtils.newPointSettable(110,-1),
                new VirtualPointLocatorRT(changeTypeVO.createRuntime(), MangoValue.objectToValue(1), true));
        DataPointRT dataPointRT2 = new DataPointRT(TestUtils.newPointSettable(109,-1),
                new VirtualPointLocatorRT(changeTypeVO.createRuntime(), MangoValue.objectToValue(1), true));
        List<DataPointRT> dataPoints = Arrays.asList(dataPointRT1, dataPointRT2);

        //when:
        resetUnreliableDataPoints(dataPoints);

        //then:
        Assert.assertEquals(false, dataPointRT1.getAttribute(DataPointUnreliableUtils.ATTR_UNRELIABLE_KEY));
        Assert.assertEquals(false, dataPointRT2.getAttribute(DataPointUnreliableUtils.ATTR_UNRELIABLE_KEY));
    }

    ///

    @Test
    public void when_setUnreliableDataPoint_with_point_then_meta_points_unreliable() {
        //given:
        ChangeTypeVO changeTypeVO = new NoChangeVO();
        DataPointRT dataPointRT1 = new DataPointRT(TestUtils.newPointSettable(110,-1),
                new VirtualPointLocatorRT(changeTypeVO.createRuntime(), MangoValue.objectToValue(1), true));

        //when:
        setUnreliableDataPoint(dataPointRT1);

        //then:
        for(DataPointRT dataPointRT: metaDataPoints) {
            Assert.assertEquals(true, dataPointRT.getAttribute(DataPointUnreliableUtils.ATTR_UNRELIABLE_KEY));
        }
    }

    @Test
    public void when_setUnreliableDataPoints_with_points_then_meta_points_unreliable() {
        //given:
        ChangeTypeVO changeTypeVO = new NoChangeVO();
        DataPointRT dataPointRT1 = new DataPointRT(TestUtils.newPointSettable(110,-1),
                new VirtualPointLocatorRT(changeTypeVO.createRuntime(), MangoValue.objectToValue(1), true));
        DataPointRT dataPointRT2 = new DataPointRT(TestUtils.newPointSettable(109,-1),
                new VirtualPointLocatorRT(changeTypeVO.createRuntime(), MangoValue.objectToValue(1), true));
        List<DataPointRT> dataPoints = Arrays.asList(dataPointRT1, dataPointRT2);

        //when:
        setUnreliableDataPoints(dataPoints);

        //then:
        for(DataPointRT dataPointRT: metaDataPoints) {
            Assert.assertEquals(true, dataPointRT.getAttribute(DataPointUnreliableUtils.ATTR_UNRELIABLE_KEY));
        }
    }

    @Test
    public void when_resetUnreliableDataPoints_with_point_then_meta_points_reliable() {
        //given:
        ChangeTypeVO changeTypeVO = new NoChangeVO();
        DataPointRT dataPointRT1 = new DataPointRT(TestUtils.newPointSettable(110,-1),
                new VirtualPointLocatorRT(changeTypeVO.createRuntime(), MangoValue.objectToValue(1), true));

        //when:
        resetUnreliableDataPoint(dataPointRT1);

        //then:
        for(DataPointRT dataPointRT: metaDataPoints) {
            Assert.assertEquals(false, dataPointRT.getAttribute(DataPointUnreliableUtils.ATTR_UNRELIABLE_KEY));
        }
    }

    @Test
    public void when_resetUnreliableDataPoints_with_points_then_meta_points_reliable() {
        //given:
        ChangeTypeVO changeTypeVO = new NoChangeVO();
        DataPointRT dataPointRT1 = new DataPointRT(TestUtils.newPointSettable(110,-1),
                new VirtualPointLocatorRT(changeTypeVO.createRuntime(), MangoValue.objectToValue(1), true));
        DataPointRT dataPointRT2 = new DataPointRT(TestUtils.newPointSettable(109,-1),
                new VirtualPointLocatorRT(changeTypeVO.createRuntime(), MangoValue.objectToValue(1), true));
        List<DataPointRT> dataPoints = Arrays.asList(dataPointRT1, dataPointRT2);

        //when:
        resetUnreliableDataPoints(dataPoints);

        //then:
        for(DataPointRT dataPointRT: metaDataPoints) {
            Assert.assertEquals(false, dataPointRT.getAttribute(DataPointUnreliableUtils.ATTR_UNRELIABLE_KEY));
        }
    }

    ///

    @Test
    public void when_not_invoke_setUnreliableDataPoints_or_setUnreliableDataPoint_then_false() {
        //given:
        ChangeTypeVO changeTypeVO = new NoChangeVO();
        DataPointRT dataPointRT1 = new DataPointRT(TestUtils.newPointSettable(110,-1),
                new VirtualPointLocatorRT(changeTypeVO.createRuntime(), MangoValue.objectToValue(1), true));

        //then:
        Assert.assertEquals(false, dataPointRT1.getAttribute(DataPointUnreliableUtils.ATTR_UNRELIABLE_KEY) != null);
    }
}