package br.org.scadabr.rt;

import java.util.ArrayList;
import java.util.List;

import br.org.scadabr.vo.eventDetectorTemplate.EventDetectorTemplateVO;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;

public class EventDetectorTemplateRT {

	private EventDetectorTemplateVO eventDetectorTemplateVO;

	public EventDetectorTemplateRT(
			EventDetectorTemplateVO eventDetectorTemplateVO) {
		this.eventDetectorTemplateVO = eventDetectorTemplateVO;
	}

	public void applyTemplate(DataPointVO datapointVO) {
		List<PointEventDetectorVO> eventDetectors = eventDetectorTemplateVO
				.getEventDetectors();

		List<PointEventDetectorVO> eventDetectorsCopies = new ArrayList<PointEventDetectorVO>();

		for (PointEventDetectorVO pointEventDetectorVO : eventDetectors) {
			pointEventDetectorVO.njbSetDataPoint(datapointVO);
			PointEventDetectorVO copy = pointEventDetectorVO.copy();
			copy.setId(Common.NEW_ID);
			eventDetectorsCopies.add(copy);
		}

		datapointVO.setEventDetectors(eventDetectorsCopies);
	}
}
