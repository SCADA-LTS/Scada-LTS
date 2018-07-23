package br.org.scadabr.rt.dataSource.nodaves7;

import br.org.scadabr.vo.dataSource.nodaves7.NodaveS7PointLocatorVO;

import com.serotonin.mango.rt.dataSource.PointLocatorRT;

public class NodaveS7PointLocatorRT extends PointLocatorRT {

	private final NodaveS7PointLocatorVO vo;

	public NodaveS7PointLocatorRT(NodaveS7PointLocatorVO vo) {
		this.vo = vo;
	}

	@Override
	public boolean isSettable() {
		return vo.isSettable();
	}

	public NodaveS7PointLocatorVO getVo() {
		return vo;
	}

}
