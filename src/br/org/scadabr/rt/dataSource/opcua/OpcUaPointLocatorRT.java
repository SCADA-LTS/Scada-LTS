package br.org.scadabr.rt.dataSource.opcua;

import br.org.scadabr.vo.dataSource.opcua.OpcUaPointLocatorVO;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;

public class OpcUaPointLocatorRT extends PointLocatorRT {
	private final OpcUaPointLocatorVO vo;

	public OpcUaPointLocatorRT(OpcUaPointLocatorVO vo) {
		this.vo = vo;
	}

	@Override
	public boolean isSettable() {
		// TODO Auto-generated method stub
		return vo.isSettable();
	}

	public OpcUaPointLocatorVO getVo() {
		return vo;
	}

}
