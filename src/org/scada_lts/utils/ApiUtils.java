package org.scada_lts.utils;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.mailingList.EmailRecipient;
import com.serotonin.mango.vo.mailingList.UserEntry;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.UserService;

import java.util.List;
import java.util.Optional;

public final class ApiUtils {

    private static final Log LOG = LogFactory.getLog(ApiUtils.class);

    private ApiUtils() { }

    public static boolean userIdExists(Integer id, UserService userService){
        return getUser(id, userService).isPresent();
    }

    public static Optional<User> getUser(int id, UserService userService) {
        try {
            User user = userService.getUser(id);
            return Optional.ofNullable(user);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    public static boolean usersExist(List<EmailRecipient> entries, UserService userService) {
        for (EmailRecipient recipient : entries) {
            if (recipient != null) {
                if (recipient.getRecipientType() == EmailRecipient.TYPE_USER) {
                    UserEntry userEntry = (UserEntry) recipient;
                    if (!userIdExists(userEntry.getUserId(), userService))
                        return false;
                }
            }
        }
        return true;
    }

    public static boolean validateUser(User userToSave, UserService userService) {
        if(userToSave.getId() == Common.NEW_ID || userToSave.getId() == 0)
            return false;
        setPassword(userToSave, userService);
        return validateUserCreate(userToSave);
    }

    public static boolean validateUserCreate(User userToSave) {
        if(userToSave.getUserProfile() == 0)
            userToSave.setUserProfileId(-1);
        DwrResponseI18n response = new DwrResponseI18n();
        userToSave.validate(response);
        return !response.getHasMessages();
    }


    private static void setPassword(User userToSave, UserService userService) {
        if(StringUtils.isEmpty(userToSave.getPassword())) {
            getUser(userToSave.getId(), userService).ifPresent(userFromBase -> {
                userToSave.setPassword(userFromBase.getPassword());
            });
        }
    }
}
