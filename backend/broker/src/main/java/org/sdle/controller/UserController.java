package org.sdle.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.sdle.model.Token;
import org.sdle.service.TokenService;
import org.sdle.repository.UserRepository;
import org.sdle.model.User;


public class UserController {
    UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    public String login(String username, String password) {
        User user = repository.getUserByUsername(username);

        if (user == null) {
            return null;
        }

        if (!password.equals(user.getPassword())) {
            return null;
        }

        Token token = TokenService.generateToken(username);

        try {
            return new ObjectMapper().writeValueAsString(token);
        } catch (JsonProcessingException e) {
            System.err.println("Json Processing exception: " + e.getMessage());
        }

        return null;
    }

    public String verifyToken(Token token) {
        Claims claims = TokenService.verifyToken(token);

        if(claims == null) return "";

        String name = TokenService.getUsernameFromToken(token);

        return String.format("{ \"username\":\"%s\" }", name);
    }
}
