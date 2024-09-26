package org.scada_lts.web.mvc.api;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.UserCommentService;
import org.scada_lts.web.beans.ApplicationBeans;
import org.scada_lts.web.mvc.api.json.JsonUserComment;
import org.scada_lts.web.beans.validation.xss.XssProtect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static org.scada_lts.utils.UserCommentApiUtils.validUserComment;
import static org.scada_lts.utils.UserCommentApiUtils.validUserCommentWithTs;
import static org.scada_lts.utils.ValidationUtils.formatErrorsJson;

@Controller
@RequestMapping(path = "/api/userComment")
public class UserCommentAPI {

    private static final Log LOG = LogFactory.getLog(UserCommentAPI.class);

    private final UserCommentService userCommentService;

    public UserCommentAPI() {
        this.userCommentService = ApplicationBeans.getUserCommentServiceBean();
    }

    /**
     * Create User Comment
     *
     * @param request HTTP request
     * @param createUserComment String comment
     * @param typeId UserComment type (1 - Event or 2 - Point)
     * @param refId Reference ID of the object
     *
     * @return Status
     */
    @PostMapping(value = "/{typeId}/{refId}")
    public ResponseEntity<?> createUserComment(HttpServletRequest request, @RequestBody CreateUserComment createUserComment, @PathVariable("typeId") Integer typeId, @PathVariable("refId") Integer refId) {
        try {
            User user = Common.getUser(request);
            if(user != null) {
                String error = validUserComment(typeId, refId, createUserComment.getCommentText());
                if (!error.isEmpty()) {
                    return ResponseEntity.badRequest().body(formatErrorsJson(error));
                }
                return userCommentService.addUserComment(createUserComment.getCommentText(), typeId, refId, user).map(a -> {
                            JsonUserComment comment = new JsonUserComment(a.getUserId(), typeId, a.getTypeKey(),
                                    a.getTs(), a.getComment(), a.getUsername(), a.getPrettyTime());
                            return new ResponseEntity<>(comment, HttpStatus.CREATED);
                        })
                        .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete User Comment
     *
     * @param request HTTP request
     * @param typeId UserComment type (1 - Event or 2 - Point)
     * @param refId Id of the object
     * @param userId Author User Id
     * @param ts Timestamp
     *
     * @return Status
     */
    @DeleteMapping(value = "/{typeId}/{refId}/{userId}/{ts}")
    public ResponseEntity<String> deleteUserComment(HttpServletRequest request, @PathVariable("typeId") Integer typeId, @PathVariable("refId") Integer refId, @PathVariable("userId") Integer userId , @PathVariable("ts") Long ts)  {
        try {
            User user = Common.getUser(request);
            if(user != null) {
                String error = validUserCommentWithTs(typeId, refId, userId, ts);
                if (!error.isEmpty()) {
                    return ResponseEntity.badRequest().body(formatErrorsJson(error));
                }
                int result = userCommentService.deleteUserComment(userId, typeId, refId, ts);
                if(result != 0) {
                    return new ResponseEntity<>(String.valueOf(result), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(String.valueOf(result), HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    static class CreateUserComment {

        @XssProtect
        private String commentText;

        public CreateUserComment() {
        }

        public CreateUserComment(String commentText) {
            this.commentText = commentText;
        }

        public String getCommentText() {
            return commentText;
        }

        public void setCommentText(String commentText) {
            this.commentText = commentText;
        }
    }
}
