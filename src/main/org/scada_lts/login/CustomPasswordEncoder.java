package org.scada_lts.login;

import com.serotonin.mango.Common;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CustomPasswordEncoder implements PasswordEncoder{

    public String encode(CharSequence rawPassword) {
        return Common.encrypt(rawPassword.toString());
    }

    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return Common.encrypt(rawPassword.toString()).equals(encodedPassword);
    }
}
