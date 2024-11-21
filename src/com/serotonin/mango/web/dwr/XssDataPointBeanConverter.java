package com.serotonin.mango.web.dwr;

import com.serotonin.mango.web.dwr.beans.DataPointBean;
import org.directwebremoting.convert.BeanConverter;
import org.directwebremoting.extend.MarshallException;
import org.directwebremoting.extend.OutboundContext;
import org.directwebremoting.extend.OutboundVariable;

import static org.scada_lts.web.security.XssProtectHtmlUtils.escape;

public class XssDataPointBeanConverter extends BeanConverter {

    @Override
    public OutboundVariable convertOutbound(Object data, OutboundContext outctx) throws MarshallException {
        DataPointBean dataPointBean = (DataPointBean)data;
        dataPointBean.setName(escape(dataPointBean.getName()));
        dataPointBean.setXid(escape(dataPointBean.getXid()));
        return super.convertOutbound(dataPointBean, outctx);
    }
}
