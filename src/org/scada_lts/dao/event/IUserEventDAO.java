package org.scada_lts.dao.event;

import org.scada_lts.dao.GenericDaoCR;
import org.scada_lts.dao.model.event.UserEvent;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

public interface IUserEventDAO extends GenericDaoCR<UserEvent> {
    void batchUpdate(int eventId, List<Integer> userIds, boolean alarm);

    void updateAck(long eventId, boolean silenced);

    @Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
    void delete(int userId);
}
