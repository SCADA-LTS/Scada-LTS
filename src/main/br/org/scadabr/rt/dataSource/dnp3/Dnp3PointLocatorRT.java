package br.org.scadabr.rt.dataSource.dnp3;

import br.org.scadabr.vo.dataSource.dnp3.Dnp3PointLocatorVO;

import com.serotonin.mango.rt.dataSource.PointLocatorRT;

public class Dnp3PointLocatorRT extends PointLocatorRT {
	private final Dnp3PointLocatorVO vo;

	public Dnp3PointLocatorRT(Dnp3PointLocatorVO vo) {
		this.vo = vo;
	}

	@Override
	public boolean isSettable() {
		// TODO Auto-generated method stub
		return vo.isSettable();
	}

	public Dnp3PointLocatorVO getVO() {
		return vo;
	}

}
