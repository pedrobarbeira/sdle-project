package org.sdle.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.sdle.api.Request;
import org.sdle.model.Token;

public class AuthService {
    private static final ObjectMapper mapper = new ObjectMapper();
    public static String authenticateRequest(Request request) {
        Token token = mapper.convertValue(request.getHeaders(), Token.class);
        Claims claims = TokenService.verifyToken(token);

        if(claims == null) return null;

        return TokenService.getUsernameFromToken(token);
    }
}
