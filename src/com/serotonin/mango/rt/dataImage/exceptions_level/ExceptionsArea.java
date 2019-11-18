package com.serotonin.mango.rt.dataImage.exceptions_level;

import com.serotonin.mango.rt.dataImage.PointValueTime;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public class ExceptionsArea {

    public Log LOG = LogFactory.getLog(ExceptionsArea.class);

    public boolean doListIsNull(List<PointValueTime> pointValueTimes){

        if(pointValueTimes == null) {
            try {
                throw  new Exception("We have problem with get needed data From database.");
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
            return Boolean.TRUE;
        }
        return Boolean.FALSE;

    }
}
