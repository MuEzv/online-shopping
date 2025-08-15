package com.project.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtil {
//    private static final String SECRET_KEY = System.getenv("JWT_SECRET");
    private static final String SECRET_KEY = "M+v1kOKhvyF6gk7lYJ4jLr9C8wQtOzhW8+EJ6pqnv8Y=";

    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static String extractUsername(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY)
                .parseClaimsJws(token).getBody().getSubject();
    }

    public static boolean validateToken(String token, String username) {
        try {
            Claims claims = Jwts.parser().setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token).getBody();
            String tokenUsername = claims.getSubject();
            Date expiration = claims.getExpiration();
            return (tokenUsername.equals(username) && expiration.after(new Date()));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}