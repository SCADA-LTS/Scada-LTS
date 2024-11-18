package com.serotonin.mango.web.dwr;

import com.serotonin.mango.vo.DataPointVO;
import org.directwebremoting.convert.BeanConverter;
import org.directwebremoting.extend.*;

import static org.scada_lts.web.security.XssProtectHtmlEscapeUtils.escape;

public class XssDataPointVoConverter extends BeanConverter {

    @Override
    public OutboundVariable convertOutbound(Object data, OutboundContext outctx) throws MarshallException {
        DataPointVO dataPointVo = (DataPointVO)data;
        dataPointVo.setName(escape(dataPointVo.getName()));
        dataPointVo.setXid(escape(dataPointVo.getXid()));
        dataPointVo.setDataSourceXid(escape(dataPointVo.getDataSourceXid()));
        dataPointVo.setChartColour(escape(dataPointVo.getChartColour()));
        dataPointVo.setDataSourceName(escape(dataPointVo.getDataSourceName()));
        dataPointVo.setDescription(escape(dataPointVo.getDescription()));
        dataPointVo.setDeviceName(escape(dataPointVo.getDeviceName()));
        return super.convertOutbound(dataPointVo, outctx);
    }
}
