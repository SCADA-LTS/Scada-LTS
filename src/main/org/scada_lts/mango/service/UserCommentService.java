package org.scada_lts.mango.service;

import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.UserComment;
import org.scada_lts.dao.IUserCommentDAO;

/**
 * Service for Comments
 *
 * @author Radoslaw Jajko, rjajko@softq.pl
 */
public class UserCommentService {

    private final IUserCommentDAO userCommentDAO;

    public UserCommentService(IUserCommentDAO userCommentDAO) {
        this.userCommentDAO = userCommentDAO;
    }

    /**
     * Save user comment to database.
     *
     * @param comment String
     * @param typeComment Type of the comment (Event or Point)
     * @param referenceId Reference ID of the object (EventID or PointID)
     *
     * @return Result status.
     */
    public int setUserComment(String comment, int typeComment, int referenceId, User user) {
        UserComment c = new UserComment();
        c.setComment(comment);
        c.setTs(System.currentTimeMillis());
        c.setUserId(user.getId());
        c.setUsername(user.getUsername());
        c.setTypeKey(referenceId);
        return userCommentDAO.insert(c, typeComment, referenceId);
    }

    /**
     * Delete User Comment from specific DataPoint
     *
     * @param userId User ID
     * @param typeComment Type of the comment (Event or Point)
     * @param referenceId Reference of the object to delete
     * @param timestamp Comment timestamp
     * @return Result status
     */
    public int deleteUserComment(int userId, int typeComment, int referenceId, long timestamp) {
        return userCommentDAO.deleteUserComment(userId, typeComment, referenceId, timestamp);
    }
}
