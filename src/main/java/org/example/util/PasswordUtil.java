package org.example.util;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {

    public String encode(String password) {
        String salt = BCrypt.gensalt(12);
        return BCrypt.hashpw(password, salt);
    }

    public Boolean validate(String password, String encodedPassword) {
        return BCrypt.checkpw(password, encodedPassword);
    }
}
