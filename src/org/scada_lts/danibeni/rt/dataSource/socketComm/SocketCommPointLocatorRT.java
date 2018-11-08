package org.scada_lts.danibeni.rt.dataSource.socketComm;

import com.serotonin.mango.rt.dataSource.PointLocatorRT;

import org.scada_lts.danibeni.vo.dataSource.socketComm.SocketCommPointLocatorVO;

public class SocketCommPointLocatorRT extends PointLocatorRT {
	private final SocketCommPointLocatorVO vo;

	public SocketCommPointLocatorRT(SocketCommPointLocatorVO vo) {
		this.vo = vo;
	}

	@Override
	public boolean isSettable() {
		return vo.isSettable();
	}

	public SocketCommPointLocatorVO getVo() {
		return vo;
	}

}
