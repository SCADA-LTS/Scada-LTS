package br.org.scadabr.rt.dataSource.drStorageHt5b;

import br.org.scadabr.vo.dataSource.drStorageHt5b.DrStorageHt5bPointLocatorVO;

import com.serotonin.mango.rt.dataSource.PointLocatorRT;

public class DrStorageHt5bPointLocatorRT extends PointLocatorRT {

	private final DrStorageHt5bPointLocatorVO vo;

	public DrStorageHt5bPointLocatorRT(DrStorageHt5bPointLocatorVO vo) {
		this.vo = vo;
	}

	@Override
	public boolean isSettable() {
		// TODO Auto-generated method stub
		return false;
	}

	public DrStorageHt5bPointLocatorVO getVo() {
		return vo;
	}

}
