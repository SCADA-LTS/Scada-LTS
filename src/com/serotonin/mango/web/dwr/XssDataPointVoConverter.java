package com.serotonin.mango.web.dwr;

import com.serotonin.mango.vo.DataPointVO;
import org.directwebremoting.convert.BeanConverter;
import org.directwebremoting.extend.*;

import static org.scada_lts.web.security.XssProtectUtils.escapeHtml;

public class XssDataPointVoConverter extends BeanConverter {

    @Override
    public OutboundVariable convertOutbound(Object data, OutboundContext outctx) throws MarshallException {
        DataPointVO dataPointVo = (DataPointVO)data;
        dataPointVo.setName(escapeHtml(dataPointVo.getName()));
        dataPointVo.setXid(escapeHtml(dataPointVo.getXid()));
        dataPointVo.setDataSourceXid(escapeHtml(dataPointVo.getDataSourceXid()));
        dataPointVo.setChartColour(escapeHtml(dataPointVo.getChartColour()));
        dataPointVo.setDataSourceName(escapeHtml(dataPointVo.getDataSourceName()));
        dataPointVo.setDescription(escapeHtml(dataPointVo.getDescription()));
        dataPointVo.setDeviceName(escapeHtml(dataPointVo.getDeviceName()));
        return super.convertOutbound(dataPointVo, outctx);
    }
}
