package org.gestion.gestionstock.config;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HexFormat;

@Component
public class JetUtils {

    @Value("${app.jwt.secret-key}")
    private String secretkey;

    @Value("${app.application-time}")
    private long expirationTime;

    private Key getSigningKey() {
        byte[] keyBytes = HexFormat.of().parseHex(secretkey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username) {
        return generateToken(username, java.util.List.of());
    }

    /** Le claim "roles" (ex. ROLE_ADMIN_ENTREPRISE) sert aux contrôles d'accès de l'API. */
    public String generateToken(String username, java.util.Collection<String> roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", java.util.List.copyOf(roles))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}