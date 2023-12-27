package org.scada_lts.ds.messaging.protocol.mqtt;

import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;

public class MqttPointLocatorRT extends PointLocatorRT {
    private final MqttPointLocatorVO vo;
    private MangoValue currentValue;

    public MqttPointLocatorRT(MqttPointLocatorVO vo) {
        this.vo = vo;
    }

    public MqttPointLocatorVO getVO() {return vo;}

    @Override
    public boolean isSettable() {
        return vo.isSettable();
    }

    public void setCurrentValue(MangoValue currentValue) {
        this.currentValue = currentValue;
    }

    public MangoValue getCurrentValue() {
        return this.currentValue;
    }
}
