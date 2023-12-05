package org.sdle.utils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UtilsHash {
    public static String hashSHA256(String input) {
        byte[] bytes;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Hash exception: " + e);
            return null;
        }
        BigInteger number = new BigInteger(1, bytes);

        StringBuilder hexString = new StringBuilder(number.toString(16));
        while (hexString.length() < 64) { hexString.insert(0, '0'); }

        return hexString.toString();
    }

    public static String generateMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));

            BigInteger no = new BigInteger(1, messageDigest);

            StringBuilder hashText = new StringBuilder(no.toString(16));

            while (hashText.length() < 32) {
                hashText.insert(0, "0");
            }

            return hashText.toString();
        } catch (NoSuchAlgorithmException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
}
