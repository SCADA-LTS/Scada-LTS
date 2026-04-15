package org.scada_lts.dao;

import br.org.scadabr.vo.eventDetectorTemplate.EventDetectorTemplateVO;

import java.util.List;

public interface IEventDetectorTemplateDAO {

    EventDetectorTemplateVO getEventDetectorTemplate(int id);

    List<EventDetectorTemplateVO> getEventDetectorTemplatesWithoutDetectors();

    int insertEventDetectorTemplate(EventDetectorTemplateVO eventDetectorTemplate);
}
