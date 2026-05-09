package com.xtu.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {

    private static final String SECRET_KEY_STRING = "xtuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu";
    private static final SecretKey SECRET_KEY = Keys
            .hmacShaKeyFor(SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8));
    private static final long EXPIRATION_TIME = 12 * 60 * 60 * 1000;

    public static String generateToken(Map<String, Object> claims) {
        //SecretKey key = Jwts.SIG.HS256.key().build();

        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public static Claims parseToken(String token) {
        //SecretKey key = Jwts.SIG.HS256.key().build();

        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public Object getClaimFromToken(String token, String claimName) {
        Claims claims = parseToken(token);
        return claims.get(claimName);
    }
}
