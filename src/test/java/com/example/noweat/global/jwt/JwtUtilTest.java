package com.example.noweat.global.jwt;

import com.example.noweat.domain.user.UserRole;
import com.example.noweat.service.exception.BadRequestException;
import com.example.noweat.service.exception.UnauthorizedException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import io.jsonwebtoken.*;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class JwtUtilTest {
    @Autowired
    JwtUtil jwtUtil;

    @Value("${jwt.secret.key}")
    private String encodedKey;

    private Key secretKey;

    @PostConstruct
    private void init(){
        byte[] bytes = Base64.getDecoder().decode(encodedKey);
        this.secretKey = Keys.hmacShaKeyFor(bytes);
    }

    @Test
    @DisplayName("AccessToken 생성 확인")
    void createAccessTokenTest(){
        // given
        String accessToken = jwtUtil.createAccessToken(1L,"scie429@gmail.com", UserRole.USER);

        // when, then
        assertThat(accessToken.isBlank()).isFalse();
    }

    @Test
    @DisplayName("RefreshToken 생성 확인")
    void createRefreshTokenTest(){
        // given
        String refreshToken = jwtUtil.createRefreshToken(1L,"scie429@gmail.com", UserRole.USER);

        // when, then
        assertThat(refreshToken.isBlank()).isFalse();
    }

    @Test
    @DisplayName("엑세스 토큰으로 payload 조회")
    void getAccessTokenClaimsTest(){
        // given
        String accessToken = jwtUtil.createAccessToken(1L,"scie429@gmail.com", UserRole.USER);

        // when
        Claims claims = jwtUtil.getAccessTokenClaims(accessToken.substring(7));

        // then
        assertThat((String)claims.getSubject()).isEqualTo("1");
        assertThat((String)claims.get("email")).isEqualTo("scie429@gmail.com");
        assertThat((String)claims.get("userRole")).isEqualTo("USER");
    }

    @Test
    @DisplayName("리프레시 토큰으로 payload 조회")
    void getRefreshTokenClaimsTest(){
        // given
        String refreshToken = jwtUtil.createRefreshToken(1L,"scie429@gmail.com", UserRole.USER);

        // when
        Claims claims = jwtUtil.getRefreshTokenClaims(refreshToken);

        // then
        assertThat((String)claims.getSubject()).isEqualTo("1");
        assertThat((String)claims.get("email")).isEqualTo("scie429@gmail.com");
        assertThat((String)claims.get("userRole")).isEqualTo("USER");
    }

    @Test
    @DisplayName("잘못된 형식의 토큰으로 payload 조회하려하면 오류 발생")
    void badTokenTest(){
        //given
        String noToken = "1234";

        // when,then
        assertThatThrownBy(() -> {
            jwtUtil.getAccessTokenClaims(noToken);
        }).isInstanceOf(UnauthorizedException.class);

        assertThatThrownBy(() -> {
            jwtUtil.getRefreshTokenClaims(noToken);
        }).isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("리프레시 토큰을 getAccessTokenClaims()으로 검증하려하면 오류 발생")
    void test6_1(){
        // given
        String refreshToken = jwtUtil.createRefreshToken(1L,"scie429@gmail.com", UserRole.USER);

        // when, then
        assertThatThrownBy(() -> {
            jwtUtil.getAccessTokenClaims(refreshToken);
        }).isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("엑세스 토큰을 getRefreshTokenClaims()으로 검증하려하면 오류 발생")
    void test6_2(){
        // given
        String accessToken = jwtUtil.createAccessToken(1L,"scie429@gmail.com", UserRole.USER);

        // when, then
        assertThatThrownBy(() -> {
            jwtUtil.getRefreshTokenClaims(accessToken.substring(7));
        }).isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("만료된 엑세스 토큰을 getAccessTokenClaims()으로 검증하려하면 오류 발생")
    void test7_1(){
        // given
        Date date = new Date();
        String accessToken = Jwts.builder()
                .claim("access", "access")
                .setExpiration(new Date(date.getTime() + 1000))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        try{
            Thread.sleep(1000);
        }catch (Exception e){

        }

        // when, then
        assertThatThrownBy(() -> {
            jwtUtil.getAccessTokenClaims(accessToken);
        }).isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("만료된 리프레시 토큰을 getRefreshTokenClaims()으로 검증하려하면 오류 발생")
    void test7_2(){
        // given
        Date date = new Date();
        String refreshToken = Jwts.builder()
                .claim("refresh", "refresh")
                .setExpiration(new Date(date.getTime() + 1000))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        try{
            Thread.sleep(1000);
        }catch (Exception e){

        }

        // when, then
        assertThatThrownBy(() -> {
            jwtUtil.getRefreshTokenClaims(refreshToken);
        }).isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("만료된 리프레시 토큰을 getAccessTokenClaims()으로 검증하려하면 오류 발생")
    void test8_1(){
        // given
        Date date = new Date();
        String refreshToken = Jwts.builder()
                .claim("refresh", "refresh")
                .setExpiration(new Date(date.getTime() + 1000))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        try{
            Thread.sleep(1000);
        }catch (Exception e){

        }

        // when, then
        assertThatThrownBy(() -> {
            jwtUtil.getAccessTokenClaims(refreshToken);
        }).isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("만료된 access 토큰을 getRefreshTokenClaims()으로 검증하려하면 오류 발생")
    void test8_2(){
        // given
        Date date = new Date();
        String access = Jwts.builder()
                .claim("access", "access")
                .setExpiration(new Date(date.getTime() + 1000))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        try{
            Thread.sleep(1000);
        }catch (Exception e){

        }

        // when, then
        assertThatThrownBy(() -> {
            jwtUtil.getRefreshTokenClaims(access);
        }).isInstanceOf(BadRequestException.class);
    }
}