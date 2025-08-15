package com.project.accountservice.util;

import io.jsonwebtoken.*;
//import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static io.jsonwebtoken.Jwts.parser;

public class JwtUtil {

    private static final String SECRET_KEY = "M+v1kOKhvyF6gk7lYJ4jLr9C8wQtOzhW8+EJ6pqnv8Y="; // Base64 字符串

//    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_KEY_STR.getBytes(StandardCharsets.UTF_8));

    public static String generateTokenSubjectIsAccountId(String accountId, String email, String[] roles) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + 3600_000); // 1h
        return Jwts.builder()
                .setSubject(accountId)                 // ★ sub = accountId
                .claim("email", email)                 // 可选，给下游看
                .claim("roles", roles)                 // 可选，做 RBAC
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }


    public static String extractSubject(String token) {
        return parser().setSigningKey(SECRET_KEY)
                .parseClaimsJws(token).getBody().getSubject();
    }

    public static String extractEmail(String token) {
        Object v = parser().setSigningKey(SECRET_KEY)
                .parseClaimsJws(token).getBody().get("email");
        return v == null ? null : v.toString();
    }

    public static List<String> extractRoles(String token) {
        Object raw = parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().get("roles");
        if (raw instanceof List<?>) {
            return ((List<?>) raw).stream().map(String::valueOf).collect(Collectors.toList());
        }
        return List.of();
    }

    public static boolean validateToken(String token, String expectedSubject) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            System.out.println("✅ Token is valid");
            return true;
        } catch (Exception e) {
            System.out.println("❌ Token validation failed: " + e.getMessage());
            return false;
        }
    }
}