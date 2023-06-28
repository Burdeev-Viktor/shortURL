package org.example;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;

public class Encoder {
    private final MessageDigest md5;

    {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public Encoder() {
    }

    public String encode(String rawPassword) {
        return Arrays.toString(md5.digest(rawPassword.getBytes()));
    }

    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String encodePas = Arrays.toString(md5.digest(rawPassword.toString().getBytes()));
        return Objects.equals(encodePas, encodedPassword);
    }

}
