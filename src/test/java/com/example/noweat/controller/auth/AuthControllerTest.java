package com.example.noweat.controller.auth;

import com.example.noweat.domain.user.UserRole;
import com.example.noweat.dto.auth.reponse.UserSignupResponseDto;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    AuthService authService;

    @Test
    @DisplayName("회원가입 테스트")
    void signupTest() throws Exception {
        // given
        LocalDateTime localDateTime = LocalDateTime.now();

        UserSignupResponseDto responseDto = UserSignupResponseDto.builder()
                .id(1L)
                .username("김민재")
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
                                " \"userAddress\" : \"전남 신안군\"," +
                                " \"username\" : \"username\"," +
                                " \"userRole\" : \"USER\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.username").value("김민재"))
                .andExpect(jsonPath("$.userRole").value("USER"))
                .andExpect(jsonPath("$.createdAt").value(localDateTime));
    }
}