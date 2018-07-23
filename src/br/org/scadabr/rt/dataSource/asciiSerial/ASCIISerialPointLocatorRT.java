package br.org.scadabr.rt.dataSource.asciiSerial;

import br.org.scadabr.vo.dataSource.asciiSerial.ASCIISerialPointLocatorVO;

import com.serotonin.mango.rt.dataSource.PointLocatorRT;

public class ASCIISerialPointLocatorRT extends PointLocatorRT {
	private final ASCIISerialPointLocatorVO vo;

	public ASCIISerialPointLocatorRT(ASCIISerialPointLocatorVO vo) {
		this.vo = vo;
	}

	@Override
	public boolean isSettable() {
		return vo.isSettable();
	}

	public ASCIISerialPointLocatorVO getVo() {
		return vo;
	}

}
