package com.example.noweat.controller.user;

import com.example.noweat.domain.user.User;
import com.example.noweat.domain.user.UserRole;
import com.example.noweat.dto.user.response.UserResponseDto;
import com.example.noweat.global.argumentResolver.AuthUser;
import com.example.noweat.global.jwt.JwtUtil;
import com.example.noweat.service.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtUtil jwtUtil;

    @MockitoBean
    private UserService userService;

    private AuthUser authUser;

    @Test
    @DisplayName("유저 조회 성공")
    void findUser() throws Exception {
        // given
        long userId = 1L;
        authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        UserResponseDto userResponseDto = new UserResponseDto(1L, "유저", "서울", UserRole.USER, null, null);

        String accessToken = jwtUtil.createAccessToken(1L, "a@a.com", UserRole.USER);
        given(userService.findUser(any(), any())).willReturn(userResponseDto);

        // when & then
        mockMvc.perform(get("/api/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponseDto.getId()));
    }
}
