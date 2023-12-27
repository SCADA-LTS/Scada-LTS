package br.org.scadabr.rt.dataSource.alpha2;

import br.org.scadabr.vo.dataSource.alpha2.Alpha2PointLocatorVO;

import com.serotonin.mango.rt.dataSource.PointLocatorRT;

public class Alpha2PointLocatorRT extends PointLocatorRT {
	private final Alpha2PointLocatorVO vo;

	public Alpha2PointLocatorRT(Alpha2PointLocatorVO vo) {
		this.vo = vo;
	}

	@Override
	public boolean isSettable() {
		return vo.isSettable();
	}

}
