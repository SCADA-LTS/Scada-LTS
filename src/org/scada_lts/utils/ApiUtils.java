package org.scada_lts.utils;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.mailingList.EmailRecipient;
import com.serotonin.mango.vo.mailingList.UserEntry;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrMessageI18n;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.SystemSettingsService;
import org.scada_lts.mango.service.UserService;
import org.scada_lts.web.mvc.api.json.JsonSettingsMisc;
import org.scada_lts.web.mvc.api.user.UserInfo;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
            if (recipient != null && recipient.getRecipientType() == EmailRecipient.TYPE_USER) {
                    UserEntry userEntry = (UserEntry) recipient;
                    if (!userIdExists(userEntry.getUserId(), userService))
                        return false;
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
        DwrResponseI18n response = validate(userToSave);
        return !response.getHasMessages();
    }

    public static DwrResponseI18n validate(User userToSave) {
        if(userToSave.getUserProfile() == 0)
            userToSave.setUserProfileId(-1);
        DwrResponseI18n response = new DwrResponseI18n();
        userToSave.validate(response);
        return response;
    }

    public static Map<String, String> toMapMessages(DwrResponseI18n responseI18n) {
        if(responseI18n.getHasMessages()) {
            AtomicInteger counter = new AtomicInteger();
            return responseI18n.getMessages().stream()
                    .collect(Collectors.toMap(a -> a.getContextKey() == null ? "message" + counter.incrementAndGet() : a.getContextKey(),
                            ApiUtils::getMessage, (a, b) -> b));
        }
        return Collections.emptyMap();
    }

    public static Map<String, String> toMapMessages(List<String> messages) {
        if(!messages.isEmpty()) {
            AtomicInteger counter = new AtomicInteger();
            return messages.stream()
                    .collect(Collectors.toMap(a -> "message" + counter.incrementAndGet(), a -> a, (a, b) -> b));
        }
        return Collections.emptyMap();
    }

    private static String getMessage(DwrMessageI18n a) {
        LocalizableMessage contextualMessage = a.getContextualMessage();
        if(contextualMessage == null) {
            LocalizableMessage genericMessage = a.getGenericMessage();
            if(genericMessage == null)
                return "unknown";
            return Common.getMessage(genericMessage.getKey(), genericMessage.getArgs());
        } else {
            return Common.getMessage(contextualMessage.getKey(), contextualMessage.getArgs());
        }
    }

    public static void setPassword(User userToSave, UserService userService) {
        if(StringUtils.isEmpty(userToSave.getPassword())) {
            getUser(userToSave.getId(), userService).ifPresent(userFromBase -> {
                userToSave.setPassword(userFromBase.getPassword());
            });
        }
    }

    public static UserInfo toUserInfo(User user) {
        JsonSettingsMisc jsonSettingsMisc = new SystemSettingsService().getMiscSettings();
        return new UserInfo(user, jsonSettingsMisc.isEnableFullScreen(), jsonSettingsMisc.isHideShortcutDisableFullScreen());
    }
}
