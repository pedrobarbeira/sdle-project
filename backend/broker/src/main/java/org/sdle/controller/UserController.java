package org.sdle.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.sdle.model.Token;
import org.sdle.service.TokenService;
import org.sdle.repository.UserRepository;
import org.sdle.model.User;

import java.util.HashMap;


public class UserController {
    UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    public Token login(String username, String password) {
        User user = repository.getUserByUsername(username);

        if (user == null) {
            return null;
        }

        if (!password.equals(user.getPassword())) {
            return null;
        }

        return TokenService.generateToken(username);
    }

    public HashMap<String, String> verifyToken(Token token) {
        Claims claims = TokenService.verifyToken(token);

        if(claims == null) return null;

        String name = TokenService.getUsernameFromToken(token);
        HashMap<String, String> response = new HashMap<>();
        response.put("username", name);
        return response;
    }
}
