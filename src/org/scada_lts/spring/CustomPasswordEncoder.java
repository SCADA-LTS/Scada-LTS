package org.scada_lts.spring;

import com.serotonin.mango.Common;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CustomPasswordEncoder implements PasswordEncoder{

    public String encode(CharSequence rawPassword) {

        String hashed = Common.encrypt(rawPassword.toString());

        return hashed;
    }

    public boolean matches(CharSequence rawPassword, String encodedPassword) {

        return rawPassword.toString().equals(encodedPassword);
    }
}
