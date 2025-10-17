package org.scada_lts.web.mvc.api.user;
import com.serotonin.mango.vo.User;

import com.serotonin.mango.Common;

public class UserInfoPassword extends UserInfo {

    private String password;

    public UserInfoPassword() {}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = Common.encrypt(password);
    }

    public void setPassword(String password, boolean skipEncryption) {
        if(skipEncryption) {
            this.password = password;
        } else {
            setPassword(password);
        }
    }

    @Override
    public User toUser() {
        User u = super.toUser();
        u.setPassword(this.getPassword());
        return u;
    }
}
