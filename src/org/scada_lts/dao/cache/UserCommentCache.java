package org.scada_lts.dao.cache;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.UserComment;
import org.scada_lts.dao.UserCommentDAO;

import java.util.List;

public class UserCommentCache implements UserCommentCachable {

    private final UserCommentDAO userCommentDAO;

    public UserCommentCache(UserCommentDAO userCommentDAO) {
        this.userCommentDAO = userCommentDAO;
    }

    @Override
    public List<UserComment> findByDataPoint(DataPointVO dataPoint) {
        return userCommentDAO.getPointComments(dataPoint);
    }

    @Override
    public List<UserComment> findByEvent(EventInstance event) {
        return userCommentDAO.getEventComments(event);
    }

    @Override
    public void removeAll() {}

    @Override
    public void removeByDataPoint(int dataPointId) {}

    @Override
    public void removeByEvent(long eventId) {}
}
