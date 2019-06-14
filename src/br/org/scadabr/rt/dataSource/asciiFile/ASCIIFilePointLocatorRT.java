package br.org.scadabr.rt.dataSource.asciiFile;

import br.org.scadabr.vo.dataSource.asciiFile.ASCIIFilePointLocatorVO;

import com.serotonin.mango.rt.dataSource.PointLocatorRT;

public class ASCIIFilePointLocatorRT extends PointLocatorRT {
	private final ASCIIFilePointLocatorVO vo;

	public ASCIIFilePointLocatorRT(ASCIIFilePointLocatorVO vo) {
		this.vo = vo;
	}

	@Override
	public boolean isSettable() {
		return vo.isSettable();
	}

	public ASCIIFilePointLocatorVO getVo() {
		return vo;
	}

}
