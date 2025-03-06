package com.example.noweat.global.jwt;

import com.example.noweat.domain.user.UserRole;
import com.example.noweat.service.exception.BadRequestException;
import com.example.noweat.service.exception.UnauthorizedException;
import com.example.noweat.service.exception.enums.ErrorCode;
import io.jsonwebtoken.*;
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
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 120 ;
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 14;

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
                .claim("access", "access")
                .setExpiration(new Date(date.getTime() + ACCESS_TOKEN_EXPIRATION_TIME))
                .setIssuedAt(date)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(Long userId, String email, UserRole userRole){

        Date date = new Date();

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .claim("userRole", userRole)
                .claim("refresh", "refresh")
                .setExpiration(new Date(date.getTime() + REFRESH_TOKEN_EXPIRATION_TIME))
                .setIssuedAt(date)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims getAccessTokenClaims(String token){
        Claims claims = null;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            if(claims.get("access") == null){
                throw new BadRequestException(ErrorCode.ACCESS_TOKEN_REQUIRED);
            }
        } catch (ExpiredJwtException e) {

            if(e.getClaims().get("access") == null){
                throw new BadRequestException(ErrorCode.ACCESS_TOKEN_REQUIRED);
            }

            // 토큰이 만료되었습니다.
            throw new UnauthorizedException(ErrorCode.ACCESS_TOKEN_EXPIRED);
        } catch (JwtException e) {
            // 토큰 처리중 오류가 발생
            throw new UnauthorizedException(ErrorCode.JWT_ERROR);
        }

        return claims;
    }

    public Claims getRefreshTokenClaims(String token){
        Claims claims = null;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            if(claims.get("refresh") == null){
                throw new BadRequestException(ErrorCode.REFRESH_TOKEN_REQUIRED);
            }
        } catch (ExpiredJwtException e) {

            if(e.getClaims().get("refresh") == null){
                throw new BadRequestException(ErrorCode.REFRESH_TOKEN_REQUIRED);
            }

            // 토큰이 만료되었습니다.
            throw new UnauthorizedException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        } catch (JwtException e) {
            // 토큰 처리중 오류가 발생
            throw new UnauthorizedException(ErrorCode.JWT_ERROR);
        }

        return claims;
    }
}
