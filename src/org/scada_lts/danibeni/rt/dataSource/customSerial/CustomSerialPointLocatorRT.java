package org.scada_lts.danibeni.rt.dataSource.customSerial;

import com.serotonin.mango.rt.dataSource.PointLocatorRT;

import org.scada_lts.danibeni.vo.dataSource.customSerial.CustomSerialPointLocatorVO;

public class CustomSerialPointLocatorRT extends PointLocatorRT {
	private final CustomSerialPointLocatorVO vo;

	public CustomSerialPointLocatorRT(CustomSerialPointLocatorVO vo) {
		this.vo = vo;
	}

	@Override
	public boolean isSettable() {
		return vo.isSettable();
	}

	public CustomSerialPointLocatorVO getVo() {
		return vo;
	}

}
