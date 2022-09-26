package org.scada_lts.dao;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.UserComment;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

public interface IUserCommentDAO {
    List<UserComment> getEventComments(EventInstance event);

    List<UserComment> getPointComments(DataPointVO dataPoint);

    int insert(UserComment userComment, int typeId, int referenceId);

    void update(int userId);

    int deleteUserComment(int userId, int typeId, int referenceId, long ts);

    void deleteUserCommentPoint(String dataPointIdList);

}
