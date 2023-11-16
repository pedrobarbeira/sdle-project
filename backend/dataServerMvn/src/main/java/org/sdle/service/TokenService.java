package org.sdle.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.sdle.model.Token;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;

public class TokenService {
    private static final String base64EncodedSecretKey = "VGhpcyBpcyBhIHNlY3JldCE=";

    private static final long tokenValidity = 3600000;

    private static final SecretKey secretKey;

    static {
        byte[] decodedSecretKey = Base64.getDecoder().decode(base64EncodedSecretKey);

        secretKey = new SecretKeySpec(decodedSecretKey, 0, decodedSecretKey.length, SignatureAlgorithm.HS256.getJcaName());
    }

    public static Token generateToken(String username) {
        long now = System.currentTimeMillis();
        Date expirationDate = new Date(now + tokenValidity);

        return  new Token(Jwts.builder()
                .setSubject(username)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact());
    }

    public static String getUsernameFromToken(Token token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token.getToken())
                    .getBody();
            return claims.getSubject();
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            System.err.println("Expired JWT Exception: " + e.getMessage());
        }

        return null;
    }

    public static Claims verifyToken(Token token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token.getToken())
                    .getBody();
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            System.err.println("Malformed JWT Exception: " + e.getMessage());
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            System.err.println("Expired JWT Exception: " + e.getMessage());
        }

        return null;
    }
}