package org.scada_lts.mango.service;

import com.serotonin.mango.vo.UserComment;
import org.scada_lts.dao.UserCommentDAO;
import org.springframework.stereotype.Service;

/**
 * Service for Comments
 *
 * @author Radoslaw Jajko, rjajko@softq.pl
 */
@Service
public class UserCommentService {

    private final UserCommentDAO userCommentDAO = new UserCommentDAO();

    /**
     * Save user comment to database.
     *
     * @param userComment User comment object data
     * @param typeComment Type of the comment (Event or Point)
     * @param referenceId Reference ID of the object (EventID or PointID)
     *
     * @return Result status.
     */
    public int setUserComment(UserComment userComment, int typeComment, int referenceId) {
        return userCommentDAO.insert(userComment, typeComment, referenceId);
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
