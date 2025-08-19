package org.scada_lts.dao.event;

import com.serotonin.mango.vo.event.CompoundEventDetectorVO;
import org.scada_lts.dao.GenericDAO;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

public interface ICompoundEventDetectorDAO extends GenericDAO<CompoundEventDetectorVO> {

    CompoundEventDetectorVO findByXId(Object[] pk);
}
