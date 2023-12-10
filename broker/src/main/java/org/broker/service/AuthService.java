package org.broker.service;

import org.broker.model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class AuthService {

    private final UserRepository repository;
    private final HashMap<String, String> tokenMap;

    public AuthService(UserRepository repository) {
        this.repository = repository;
        this.tokenMap = new HashMap<>();
    }


    private String encrypt(String toEncrypt){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(toEncrypt.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean validateUser(String userName, String password){
        User user = repository.getUser(userName);
        String encrypted = encrypt(password);
        return encrypted.equals(user.getPassword());
    }

    public String generateToken(String userName, String password){
        if(validateUser(userName, password)){
            String token = encrypt(userName);
            tokenMap.put(userName, token);
            return token;
        }
        return null;
    }

    public boolean validateToken(String user, String token){
        String validToken = tokenMap.get(user);
        return validToken.equals(token);
    }
}
