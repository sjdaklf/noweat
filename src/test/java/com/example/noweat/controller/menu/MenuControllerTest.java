package com.example.noweat.controller.menu;

import com.example.noweat.domain.user.UserRole;
import com.example.noweat.dto.menu.response.MenuResponseDto;
import com.example.noweat.dto.menu.response.MenuSaveResponseDto;
import com.example.noweat.global.jwt.JwtUtil;
import com.example.noweat.service.menu.MenuService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MenuControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtUtil jwtUtil;

    @MockitoBean
    private MenuService menuService;

    @Test
    @DisplayName("메뉴 저장 테스트")
    void saveMenuTest() throws Exception{
        // given
        long storeId = 1L;
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String accessToken = jwtUtil.createAccessToken(1L, "email", UserRole.OWNER);

        MenuSaveResponseDto response = MenuSaveResponseDto.builder()
                .id(1L)
                .name("메뉴이름")
                .price(1000L)
                .createdAt(localDateTime)
                .build();
        given(menuService.saveMenu(any(), any(), any())).willReturn(response);

        // when, then
        mockMvc.perform(post("/api/stores/{storeId}/menus", storeId)
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content("{\"name\" : \"메뉴이름\"," +
                        " \"price\" : \"1000\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.name").value(response.getName()))
                .andExpect(jsonPath("$.price").value(response.getPrice()))
                .andExpect(jsonPath("$.createdAt").value(dateTimeFormatter.format(localDateTime)));

    }

    @Test
    @DisplayName("모든 메뉴 조회 테스트")
    void findAllMenuTest() throws Exception{
        // given
        long storeId = 1L;

        List<MenuResponseDto> menuList = List.of(
                MenuResponseDto.builder()
                        .id(1L)
                        .name("메뉴이름1")
                        .price(1000L)
                        .build(),
                MenuResponseDto.builder()
                        .id(2L)
                        .name("메뉴이름2")
                        .price(2000L)
                        .build()
        );

        given()

        // when, then
        mockMvc.perform(get("/api/stores/{storeId}/menus", storeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(menuList.get(0).getId()))
                .andExpect(jsonPath("$[0].name").value(menuList.get(0).getName()))
                .andExpect(jsonPath("$[0].price").value(menuList.get(0).getPrice()))
                .andExpect(jsonPath("$[1].id").value(menuList.get(1).getId()))
                .andExpect(jsonPath("$[1].name").value(menuList.get(1).getName()))
                .andExpect(jsonPath("$[1].price").value(menuList.get(1).getPrice()));
    }
}
