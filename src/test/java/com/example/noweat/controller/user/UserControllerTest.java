package com.example.noweat.controller.user;

import com.example.noweat.domain.review.StarRating;
import com.example.noweat.domain.store.StoreCategory;
import com.example.noweat.domain.user.User;
import com.example.noweat.domain.user.UserRole;
import com.example.noweat.dto.user.response.UserResponseDto;
import com.example.noweat.dto.user.response.UserReviewResponseDto;
import com.example.noweat.dto.user.response.UserStoreResponseDto;
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


import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
    private UserResponseDto userResponseDto;

    private AuthUser authUser;

    @Test
    @DisplayName("유저 조회 성공")
    void findUser() throws Exception {
        // given
        long userId = 1L;
        authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        userResponseDto = new UserResponseDto(1L, "유저", "서울", UserRole.USER, null, null);
        String accessToken = jwtUtil.createAccessToken(1L, "a@a.com", UserRole.USER);

        given(userService.findUser(any(), any())).willReturn(userResponseDto);

        // when & then
        mockMvc.perform(get("/api/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponseDto.getId()));
    }

    @Test
    @DisplayName("UserId로 Store 찾기 성공")
    void findStoresByUserId() throws Exception {
        // given
        authUser = new AuthUser(2L, "b@b.com", UserRole.OWNER);
        String accessToken = jwtUtil.createAccessToken(2L, "b@b.com", UserRole.OWNER);

        List<UserStoreResponseDto> storeList = List.of(
                UserStoreResponseDto.builder()
                        .id(1L)
                        .name("Store1")
                        .storeCategory(StoreCategory.KOREAN)
                        .minOrderPrice(15000L)
                        .averageRating(3.2)
                        .build(),
                UserStoreResponseDto.builder()
                        .id(2L)
                        .name("Store2")
                        .storeCategory(StoreCategory.WESTERN)
                        .minOrderPrice(13000L)
                        .averageRating(4.5)
                        .build()
        );

        given(userService.findStoresByUserId(any())).willReturn(storeList);

        // when & then
        mockMvc.perform(get("/api/users/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))

                .andExpect(jsonPath("$[0].id").value(storeList.get(0).getId()))
                .andExpect(jsonPath("$[0].name").value(storeList.get(0).getName()))
                .andExpect(jsonPath("$[0].storeCategory").value(storeList.get(0).getStoreCategory().toString()))
                .andExpect(jsonPath("$[0].minOrderPrice").value(storeList.get(0).getMinOrderPrice()))
                .andExpect(jsonPath("$[0].averageRating").value(storeList.get(0).getAverageRating()))

                .andExpect(jsonPath("$[1].id").value(storeList.get(1).getId()))
                .andExpect(jsonPath("$[1].name").value(storeList.get(1).getName()))
                .andExpect(jsonPath("$[1].storeCategory").value(storeList.get(1).getStoreCategory().toString()))
                .andExpect(jsonPath("$[1].minOrderPrice").value(storeList.get(1).getMinOrderPrice()))
                .andExpect(jsonPath("$[1].averageRating").value(storeList.get(1).getAverageRating()));
    }

    @Test
    @DisplayName("UserId로 Review 찾기 성공")
    void findReviewsByUserId() throws Exception {
        // given
        authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        String accessToken = jwtUtil.createAccessToken(1L, "a@a.com", UserRole.USER);

        List<UserReviewResponseDto> reviewList = List.of(
                UserReviewResponseDto.builder()
                        .id(1L)
                        .title("Review1")
                        .content("test1")
                        .starRating(StarRating.FOUR)
                        .createdAt(null)
                        .updatedAt(null)
                        .build(),
                UserReviewResponseDto.builder()
                        .id(2L)
                        .title("Review1")
                        .content("test2")
                        .starRating(StarRating.FIVE)
                        .createdAt(null)
                        .updatedAt(null)
                        .build()
        );

        given(userService.findReviewsByUserId(any())).willReturn(reviewList);

        // when & then
        mockMvc.perform(get("/api/users/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))

                .andExpect(jsonPath("$[0].id").value(reviewList.get(0).getId()))
                .andExpect(jsonPath("$[0].title").value(reviewList.get(0).getTitle()))
                .andExpect(jsonPath("$[0].content").value(reviewList.get(0).getContent()))
                .andExpect(jsonPath("$[0].starRating").value(reviewList.get(0).getStarRating().toString()))

                .andExpect(jsonPath("$[1].id").value(reviewList.get(1).getId()))
                .andExpect(jsonPath("$[1].title").value(reviewList.get(1).getTitle()))
                .andExpect(jsonPath("$[1].content").value(reviewList.get(1).getContent()))
                .andExpect(jsonPath("$[1].starRating").value(reviewList.get(1).getStarRating().toString()));
    }
}
