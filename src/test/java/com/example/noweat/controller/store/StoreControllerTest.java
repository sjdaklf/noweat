package com.example.noweat.controller.store;

import com.example.noweat.domain.store.StoreCategory;
import com.example.noweat.domain.user.UserRole;
import com.example.noweat.dto.store.response.*;
import com.example.noweat.global.jwt.JwtUtil;
import com.example.noweat.service.store.StoreService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StoreControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtUtil jwtUtil;

    @MockitoBean
    private StoreService storeService;

    @Test
    @DisplayName("가게 등록 테스트")
    void saveStoreTest() throws Exception{
        // given
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String accessToken = jwtUtil.createAccessToken(1L, "email", UserRole.OWNER);

        StoreSaveResponseDto response = StoreSaveResponseDto.builder()
                .id(1L)
                .name("가게이름")
                .createdAt(localDateTime)
                .build();
        given(storeService.saveStore(any(), any())).willReturn(response);

        // when, then
        mockMvc.perform(post("/api/stores")
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content("{\"name\" : \"가게이름\"," +
                        " \"address\" : \"주소\", " +
                        " \"storeCategory\" : \"KOREAN\", " +
                        " \"minOrderPrice\" : \"12000\", " +
                        " \"openTime\" : \"08:30\", " +
                        " \"closedTime\" : \"18:00\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.name").value(response.getName()))
                .andExpect(jsonPath("$.createdAt").value(dateTimeFormatter.format(localDateTime)));
    }

    @Test
    @DisplayName("가게 전체 조회 테스트")
    void findAllStoreTest() throws Exception{
        // given
        List<StoreFindAllResponseDto> storeList = List.of(
                StoreFindAllResponseDto.builder()
                        .id(1L)
                        .name("가게이름1")
                        .storeCategory(StoreCategory.KOREAN)
                        .minOrderPrice(12000L)
                        .averageRating(3.5)
                        .build(),
                StoreFindAllResponseDto.builder()
                        .id(2L)
                        .name("가게이름2")
                        .storeCategory(StoreCategory.JAPANESE)
                        .minOrderPrice(15000L)
                        .averageRating(4.5)
                        .build()
        );
        given(storeService.findAllStore(any())).willReturn(storeList);

        // when, then
        mockMvc.perform(get("/api/stores"))
                .andExpect(status().isOk())
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
    @DisplayName("가게 단건 조회")
    void findOneStoreTest() throws Exception{
        // given
        long storeId = 1L;
        LocalTime openTime = LocalTime.of(9, 0);
        LocalTime closedTime = LocalTime.of(21, 0);
        LocalDateTime creatDateTime = LocalDateTime.of(2025, 3, 6, 9, 30);
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        List<StoreMenuResponseDto> menuList = List.of(
                StoreMenuResponseDto.builder()
                        .menuId(1L)
                        .menuName("메뉴이름1")
                        .menuPrice(1000L)
                        .build(),
                StoreMenuResponseDto.builder()
                        .menuId(2L)
                        .menuName("메뉴이름2")
                        .menuPrice(2000L)
                        .build()
        );

        StoreFindOneResponseDto response = StoreFindOneResponseDto.builder()
                .id(1L)
                .name("가게이름")
                .address("주소")
                .storeCategory(StoreCategory.KOREAN)
                .minOrderPrice(12000L)
                .openTime(openTime)
                .closedTime(closedTime)
                .averageRating(3.5)
                .menuList(menuList)
                .createdAt(creatDateTime)
                .updatedAt(localDateTime)
                .build();
        given(storeService.findOneStore(any())).willReturn(response);

        // when, then
        mockMvc.perform(get("/api/stores/{storeId}", storeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.name").value(response.getName()))
                .andExpect(jsonPath("$.address").value(response.getAddress()))
                .andExpect(jsonPath("$.storeCategory").value(response.getStoreCategory().toString()))
                .andExpect(jsonPath("$.minOrderPrice").value(response.getMinOrderPrice()))
                .andExpect(jsonPath("$.openTime").value(timeFormatter.format(openTime)))
                .andExpect(jsonPath("$.closedTime").value(timeFormatter.format(closedTime)))
                .andExpect(jsonPath("$.averageRating").value(response.getAverageRating()))
                .andExpect(jsonPath("$.menuList[0].menuId").value(response.getMenuList().get(0).getMenuId()))
                .andExpect(jsonPath("$.menuList[0].menuName").value(response.getMenuList().get(0).getMenuName()))
                .andExpect(jsonPath("$.menuList[0].menuPrice").value(response.getMenuList().get(0).getMenuPrice()))
                .andExpect(jsonPath("$.menuList[1].menuId").value(response.getMenuList().get(1).getMenuId()))
                .andExpect(jsonPath("$.menuList[1].menuName").value(response.getMenuList().get(1).getMenuName()))
                .andExpect(jsonPath("$.menuList[1].menuPrice").value(response.getMenuList().get(1).getMenuPrice()))
                .andExpect(jsonPath("$.createdAt").value(dateTimeFormatter.format(creatDateTime)))
                .andExpect(jsonPath("$.updatedAt").value(dateTimeFormatter.format(localDateTime)));
    }

    @Test
    @DisplayName("가게 정보 수정 테스트")
    void updateStoreTest() throws Exception{
        // given
        long storeId = 1L;
        LocalTime openTime = LocalTime.of(9, 0);
        LocalTime closedTime = LocalTime.of(21, 0);
        LocalDateTime creatDateTime = LocalDateTime.of(2025, 3, 6, 9, 30);
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String accessToken = jwtUtil.createAccessToken(1L, "email", UserRole.OWNER);

        StoreUpdateResponseDto response = StoreUpdateResponseDto.builder()
                .id(1L)
                .name("가게수정이름")
                .address("주소수정")
                .storeCategory(StoreCategory.KOREAN)
                .minOrderPrice(10000L)
                .openTime(openTime)
                .closedTime(closedTime)
                .createdAt(creatDateTime)
                .updatedAt(localDateTime)
                .build();
        given(storeService.updateStore(any(), any(), any())).willReturn(response);

        // when, then
        mockMvc.perform(patch("/api/stores/{storeId}", storeId)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"name\" : \"가게수정이름\"," +
                                " \"address\" : \"주소수정\", " +
                                " \"storeCategory\" : \"KOREAN\", " +
                                " \"minOrderPrice\" : \"10000\", " +
                                " \"openTime\" : \"09:00\", " +
                                " \"closedTime\" : \"21:00\"}"))
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.name").value(response.getName()))
                .andExpect(jsonPath("$.address").value(response.getAddress()))
                .andExpect(jsonPath("$.storeCategory").value(response.getStoreCategory().toString()))
                .andExpect(jsonPath("$.minOrderPrice").value(response.getMinOrderPrice()))
                .andExpect(jsonPath("$.openTime").value(timeFormatter.format(openTime)))
                .andExpect(jsonPath("$.closedTime").value(timeFormatter.format(closedTime)))
                .andExpect(jsonPath("$.createdAt").value(dateTimeFormatter.format(creatDateTime)))
                .andExpect(jsonPath("$.updatedAt").value(dateTimeFormatter.format(localDateTime)));
    }

    @Test
    @DisplayName("가게 폐업 테스트")
    void deleteStoreTest() throws Exception{
        // given
        long storeId = 1L;
        String accessToken = jwtUtil.createAccessToken(1L, "email", UserRole.OWNER);

        // when, then
        mockMvc.perform(delete("/api/stores/{storeId}", storeId)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk());
    }
}