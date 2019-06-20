package cc.radiuino.scadabr.rt.datasource.radiuino;

import cc.radiuino.scadabr.vo.datasource.radiuino.RadiuinoPointLocatorVO;

import com.serotonin.mango.rt.dataSource.PointLocatorRT;

public class RadiuinoPointLocatorRT extends PointLocatorRT {
	private final RadiuinoPointLocatorVO vo;

	public RadiuinoPointLocatorRT(RadiuinoPointLocatorVO vo) {
		this.vo = vo;
	}

	@Override
	public boolean isSettable() {
		return vo.isSettable();
	}

	public RadiuinoPointLocatorVO getVo() {
		return vo;
	}

}
