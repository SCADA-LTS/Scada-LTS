/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.rt.dataSource.http;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.BinaryValue;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataSource.EventDataSource;
import com.serotonin.mango.vo.dataSource.http.HttpReceiverDataSourceVO;
import com.serotonin.mango.vo.dataSource.http.HttpReceiverPointLocatorVO;
import com.serotonin.util.StringUtils;

/**
 * @author Matthew Lohbihler
 */
public class HttpReceiverDataSourceRT extends EventDataSource implements HttpMulticastListener {
    private final Log log = LogFactory.getLog(HttpReceiverDataSourceRT.class);
    private final HttpReceiverDataSourceVO vo;

    public HttpReceiverDataSourceRT(HttpReceiverDataSourceVO vo) {
        super(vo);
        this.vo = vo;
    }

    //
    // /
    // / Lifecycle
    // /
    //
    @Override
    public void initialize() {
        Common.ctx.getHttpReceiverMulticaster().addListener(this);
        super.initialize();
    }

    @Override
    public void terminate() {
        super.terminate();
        Common.ctx.getHttpReceiverMulticaster().removeListener(this);
    }

    //
    // /
    // / HttpMulticastListener
    // /
    //
    public String[] getDeviceIdWhiteList() {
        return vo.getDeviceIdWhiteList();
    }

    public String[] getIpWhiteList() {
        return vo.getIpWhiteList();
    }

    public void ipWhiteListError(String message) {
        log.warn("Error checking white list: " + message);
    }

    public void data(HttpReceiverData data) {
        // Match data points in the received set with point locators.
        synchronized (pointListChangeLock) {
            for (DataPointRT dp : dataPoints) {
                HttpReceiverPointLocatorVO locator = ((HttpReceiverPointLocatorRT) dp.getPointLocator())
                        .getPointLocatorVO();
                String paramName = locator.getParameterName();

                for (HttpReceiverPointSample sample : data.getData()) {
                    if (sample.getKey().equals(paramName)) {
                        // This just informs the data object that the key was used so that it knows what was used
                        // and what wasn't.
                        data.consume(paramName);

                        String valueStr = sample.getValue();
                        long time = sample.getTime();
                        if (time == 0)
                            time = data.getTime();

                        MangoValue value;
                        if (locator.getDataTypeId() == DataTypes.BINARY
                                && !StringUtils.isEmpty(locator.getBinary0Value())) {
                            if (valueStr.equalsIgnoreCase(locator.getBinary0Value()))
                                value = BinaryValue.ZERO;
                            else
                                value = BinaryValue.ONE;
                        }
                        else
                            value = MangoValue.stringToValue(valueStr, locator.getDataTypeId());

                        dp.updatePointValue(new PointValueTime(value, time));
                    }
                }
            }
        }
    }
}
