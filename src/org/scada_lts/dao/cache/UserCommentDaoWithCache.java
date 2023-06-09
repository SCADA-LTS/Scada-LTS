package org.scada_lts.dao.cache;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.UserComment;
import org.scada_lts.dao.IUserCommentDAO;
import org.scada_lts.dao.UserCommentDAO;

import java.util.*;
import java.util.stream.Collectors;

public class UserCommentDaoWithCache implements IUserCommentDAO {

    public final UserCommentCachable cache;
    public final UserCommentDAO userCommentDAO;

    public UserCommentDaoWithCache(UserCommentCachable cache, UserCommentDAO userCommentDAO) {
        this.cache = cache;
        this.userCommentDAO = userCommentDAO;
    }

    @Override
    public List<UserComment> getEventComments(EventInstance event) {
        return cache.findByEvent(event);
    }

    @Override
    public List<UserComment> getPointComments(DataPointVO point) {
        return cache.findByDataPoint(point);
    }

    @Override
    public int insert(UserComment userComment, int typeId, int referenceId) {
        try {
            return userCommentDAO.insert(userComment, typeId, referenceId);
        } finally {
            if(typeId == UserComment.TYPE_EVENT)
                cache.removeByEvent(referenceId);
            if(typeId == UserComment.TYPE_POINT)
                cache.removeByDataPoint(referenceId);
        }
    }

    @Override
    public int deleteUserComment(int userId, int typeId, int referenceId, long ts) {
        try {
            return userCommentDAO.deleteUserComment(userId, typeId, referenceId, ts);
        } finally {
            if(typeId == UserComment.TYPE_EVENT)
                cache.removeByEvent(referenceId);
            if(typeId == UserComment.TYPE_POINT)
                cache.removeByDataPoint(referenceId);
        }
    }

    @Override
    public void update(int userId) {
        try {
            userCommentDAO.update(userId);
        } finally {
            cache.removeAll();
        }
    }

    @Override
    public void deleteUserCommentPoint(String dataPointIdList) {
        try {
            userCommentDAO.deleteUserCommentPoint(dataPointIdList);
        } finally {
            List<Integer> ids = Arrays.stream(dataPointIdList.split(","))
                    .map(this::toInt).filter(a -> a != -1)
                    .collect(Collectors.toList());
            for(Integer id: ids) {
                cache.removeByDataPoint(id);
            }
        }
    }

    @Override
    public List<UserComment> getEventComments() {
        return cache.findByEventAll();
    }

    private int toInt(String a) {
        try {
            return Integer.parseInt(a);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

}
