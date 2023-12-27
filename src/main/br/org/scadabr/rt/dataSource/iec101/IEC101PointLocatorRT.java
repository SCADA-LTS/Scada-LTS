package br.org.scadabr.rt.dataSource.iec101;

import br.org.scadabr.vo.dataSource.iec101.IEC101PointLocatorVO;

import com.serotonin.mango.rt.dataSource.PointLocatorRT;

public class IEC101PointLocatorRT extends PointLocatorRT {
	private final IEC101PointLocatorVO vo;

	public IEC101PointLocatorRT(IEC101PointLocatorVO vo) {
		this.vo = vo;
	}

	@Override
	public boolean isSettable() {
		return vo.isSettable();
	}

	public IEC101PointLocatorVO getVo() {
		return vo;
	}
}
