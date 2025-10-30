package org.scada_lts.web.security.dwr;

import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.db.IntValuePair;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.DataPointSaveHandler;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;
import com.serotonin.mango.vo.dataSource.virtual.VirtualDataSourceVO;
import com.serotonin.mango.vo.event.CompoundEventDetectorVO;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.vo.event.EventTypeVO;
import com.serotonin.mango.vo.mailingList.AddressEntry;
import com.serotonin.mango.vo.mailingList.MailingList;
import com.serotonin.mango.vo.mailingList.UserEntry;
import com.serotonin.mango.vo.report.ReportInstance;
import com.serotonin.mango.vo.report.ReportVO;
import com.serotonin.mango.web.dwr.beans.DataPointBean;
import com.serotonin.mango.web.dwr.beans.EventSourceBean;
import com.serotonin.mango.web.dwr.beans.RecipientListEntryBean;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;
import net.sf.mbus4j.SerialPortConnection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.scada_lts.dao.model.UserIdentifier;

import java.util.List;

@RunWith(Parameterized.class)
public class XssBeanConverterUtilsTest {

    @Parameterized.Parameters(name = "{index}: value: {0}, type: {1}")
    public static Object[][] data() {
        DataPointVO dataPoint = new DataPointVO(-1, -1, -1);
        dataPoint.setPointLocator(new PointLocatorVO() {
            @Override
            public int getDataTypeId() {
                return 0;
            }

            @Override
            public LocalizableMessage getDataTypeMessage() {
                return null;
            }

            @Override
            public LocalizableMessage getConfigurationDescription() {
                return null;
            }

            @Override
            public boolean isSettable() {
                return false;
            }

            @Override
            public boolean isRelinquishable() {
                return false;
            }

            @Override
            public PointLocatorRT createRuntime() {
                return null;
            }

            @Override
            public void validate(DwrResponseI18n response) {

            }

            @Override
            public DataPointSaveHandler getDataPointSaveHandler() {
                return null;
            }

            @Override
            public void addProperties(List<LocalizableMessage> list) {

            }

            @Override
            public void addPropertyChanges(List<LocalizableMessage> list, Object o) {

            }
        });
        return new Object[][] {
                {new VirtualDataSourceVO(), DataSourceVO.class},
                {new WatchListAccess(), WatchListAccess.class},
                {new SerialPortConnection(), SerialPortConnection.class},
                {new UserIdentifier(), UserIdentifier.class},
                {new Exception(), Exception.class},
                {new UsersProfileVO(), UsersProfileVO.class},
                {new View(), View.class},
                {dataPoint, DataPointVO.class},
                {new DataPointBean(dataPoint), DataPointBean.class},
                {new CompoundEventDetectorVO(), CompoundEventDetectorVO.class},
                {new EventHandlerVO(), EventHandlerVO.class},
                {new EventTypeVO(-1, -1, -1), EventTypeVO.class},
                {new IntValuePair(), IntValuePair.class},
                {new EventSourceBean(), EventSourceBean.class},
                {new AddressEntry(), AddressEntry.class},
                {new MailingList(), MailingList.class},
                {new UserEntry(), UserEntry.class},
                {new ReportInstance(), ReportInstance.class},
                {new ReportVO(), ReportVO.class},
                {new RecipientListEntryBean(), RecipientListEntryBean.class},

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