package com.example.noweat.global.jwt;

import com.example.noweat.domain.user.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 60 * 60 * 1000;

    @Value("${jwt.secret.key}")
    private String encodedKey;

    private Key secretKey;

    @PostConstruct
    private void init(){
        byte[] bytes = Base64.getDecoder().decode(encodedKey);
        this.secretKey = Keys.hmacShaKeyFor(bytes);
    }

    public String createAccessToken(Long userId, String email, UserRole userRole){

        Date date = new Date();

        return BEARER_PREFIX + Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .claim("userRole", userRole)
                .setExpiration(new Date(date.getTime() + ACCESS_TOKEN_EXPIRATION_TIME))
                .setIssuedAt(date)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims getAccessTokenClaims(String accessToken){
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
    }
}
