package br.org.scadabr.rt.dataSource.opc;

import br.org.scadabr.vo.dataSource.opc.OPCPointLocatorVO;

import com.serotonin.mango.rt.dataSource.PointLocatorRT;

public class OPCPointLocatorRT extends PointLocatorRT {
	private final OPCPointLocatorVO vo;

	public OPCPointLocatorRT(OPCPointLocatorVO vo) {
		this.vo = vo;
	}

	@Override
	public boolean isSettable() {
		// TODO Auto-generated method stub
		return vo.isSettable();
	}

	public OPCPointLocatorVO getVo() {
		return vo;
	}

}
