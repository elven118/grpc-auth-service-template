package org.example.util;

import io.jsonwebtoken.*;
import org.example.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public String generateToken(User user) {
        Claims claims = Jwts.claims();
        claims.put("id", user.getId().toString());
        return Jwts.builder()
                   .setClaims(claims)
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                   .signWith(SignatureAlgorithm.HS256, jwtSecret)
                   .compact();
    }

    public String getId(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().get("id", String.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            System.out.println("Invalid signature");
            return false;
        } catch (ExpiredJwtException e) {
            System.out.println("Token expired");
            return false;
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return false;
        }
    }
}
