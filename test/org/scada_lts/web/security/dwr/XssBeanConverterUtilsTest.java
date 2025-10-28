package org.scada_lts.web.security.dwr;

import br.org.scadabr.vo.permission.WatchListAccess;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.virtual.VirtualDataSourceVO;
import net.sf.mbus4j.SerialPortConnection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.scada_lts.dao.model.UserIdentifier;

@RunWith(Parameterized.class)
public class XssBeanConverterUtilsTest {

    @Parameterized.Parameters(name = "{index}: value: {0}, type: {1}")
    public static Object[][] data() {
        return new Object[][] {
                {new VirtualDataSourceVO(), DataSourceVO.class},
                {new WatchListAccess(), WatchListAccess.class},
                {new SerialPortConnection(), SerialPortConnection.class},
                {new UserIdentifier(), UserIdentifier.class},
                {new Exception(), Exception.class},
        };
    }

    private final Object value;

    public XssBeanConverterUtilsTest(Object value, Class<?> type) {
        this.value = value;
    }

    @Test
    public void when_convertObjectEscaped_for_object_with_type_no_source_then_IllegalArgumentException() throws ScadaMarshallException {

        //when:
        XssBeanConverterUtils.convertObjectEscaped(value);
    }

    @Test
    public void when_convertObjectUnescaped_for_object_with_type_no_source_then_IllegalArgumentException() throws ScadaMarshallException {

        //when:
        XssBeanConverterUtils.convertObjectUnescaped(value);
    }

}