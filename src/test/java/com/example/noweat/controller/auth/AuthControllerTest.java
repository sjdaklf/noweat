package com.example.noweat.controller.auth;

import com.example.noweat.domain.user.UserRole;
import com.example.noweat.dto.auth.reponse.RefreshTokenResponseDto;
import com.example.noweat.dto.auth.reponse.UserSigninResponseDto;
import com.example.noweat.dto.auth.reponse.UserSignupResponseDto;
import com.example.noweat.dto.auth.request.UserSigninRequestDto;
import com.example.noweat.global.argumentResolver.AuthUser;
import com.example.noweat.global.jwt.JwtUtil;
import com.example.noweat.service.auth.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtUtil jwtUtil;

    @MockitoBean
    AuthService authService;

    @Test
    @DisplayName("회원가입 테스트")
    void signupTest() throws Exception {
        // given
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        UserSignupResponseDto responseDto = UserSignupResponseDto.builder()
                .id(1L)
                .name("김민재")
                .userRole(UserRole.USER)
                .createdAt(localDateTime)
                .build();
        when(authService.signupUser(any())).thenReturn(responseDto);

        // when, then
        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"email\" : \"scie429@gmail.com\"," +
                                " \"password\" : \"Sukim2919@\"," +
                                " \"address\" : \"전남 신안군\"," +
                                " \"name\" : \"username\"," +
                                " \"userRole\" : \"USER\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.name").value("김민재"))
                .andExpect(jsonPath("$.userRole").value("USER"))
                .andExpect(jsonPath("$.createdAt").value(dateTimeFormatter.format(localDateTime)));
    }

    @Test
    @DisplayName("로그인 테스트")
    void signinTest() throws Exception{
        // given
        UserSigninResponseDto responseDto = UserSigninResponseDto.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();

        when(authService.signinUser(any())).thenReturn(responseDto);

        // when, then
        mockMvc.perform(post("/api/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"email\" : \"scie429@gmail.com\"," +
                                " \"password\" : \"Sukim2929@\"," +
                                " \"deviceId\" : \"1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"));
    }

    @Test
    @DisplayName("로그아웃 테스트")
    void signoutTest() throws Exception {
        // given
        doNothing().when(authService).signout(any(), any());

        String accessToken = jwtUtil.createAccessToken(1L, "email", UserRole.USER);


        // when, then
        mockMvc.perform(post("/api/signout")
                        .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content("{\"deviceId\" : \"1\"}"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(new byte[0]));
    }

    @Test
    @DisplayName("토큰 재발급 테스트")
    void refreshTest() throws Exception {
        // given
        RefreshTokenResponseDto responseDto = RefreshTokenResponseDto.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();

        when(authService.refreshToken(any())).thenReturn(responseDto);

        String refreshToken = jwtUtil.createRefreshToken(1L, "email", UserRole.USER);

        // when, then
        mockMvc.perform(post("/api/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content("{\"refreshToken\" :  \"" + refreshToken + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"));

    }
}