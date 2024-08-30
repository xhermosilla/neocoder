package org.example;

import org.mindrot.jbcrypt.BCrypt;

public class AuthService {

    public static String hashPassword(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean comparePassword(String password, String hashedpassword){
        return BCrypt.checkpw(password, hashedpassword);
    }
}
