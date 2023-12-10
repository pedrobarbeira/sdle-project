package org.sdle.service;

import org.sdle.model.User;

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


    public static String encrypt(String toEncrypt){
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

    private boolean validateUser(String username, String password){
        User user = repository.getUser(username);
        return password.equals(user.getPassword());
    }

    public String generateToken(String username, String password){
        if(validateUser(username, password)){
            String token = encrypt(username);
            tokenMap.put(username, token);
            return token;
        }
        return null;
    }

    public boolean validateToken(String user, String token){
        String validToken = tokenMap.get(user);
        return validToken != null && validToken.equals(token);
    }

    public String register(String username, String password){
        try {
            repository.createUser(username, password);
            return generateToken(username, password);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
