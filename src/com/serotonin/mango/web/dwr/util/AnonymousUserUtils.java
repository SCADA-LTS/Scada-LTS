package com.serotonin.mango.web.dwr.util;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.scada_lts.mango.service.UserService;

import java.util.Optional;

@Deprecated
public final class AnonymousUserUtils {

    private AnonymousUserUtils() {}

    @Deprecated
    public static Optional<User> getUser(UserService userService) {
        User user = Common.getUser();
        if (user == null) {
            user = getAnonymousUser(userService);
        }
        return Optional.ofNullable(user);
    }

    @Deprecated
    private static User getAnonymousUser(UserService userService) {
        User user = userService.getUser("anonymous-user");
        if(user != null) {
            user.setDisabled(false);
        }
        return user;
    }
}
