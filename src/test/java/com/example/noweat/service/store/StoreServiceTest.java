package com.example.noweat.service.store;

import com.example.noweat.domain.menu.Menu;
import com.example.noweat.domain.store.Store;
import com.example.noweat.domain.store.StoreCategory;
import com.example.noweat.domain.user.User;
import com.example.noweat.domain.user.UserRole;
import com.example.noweat.dto.store.request.StoreSaveRequestDto;
import com.example.noweat.dto.store.request.StoreUpdateRequestDto;
import com.example.noweat.dto.store.response.StoreFindAllResponseDto;
import com.example.noweat.dto.store.response.StoreFindOneResponseDto;
import com.example.noweat.dto.store.response.StoreSaveResponseDto;
import com.example.noweat.dto.store.response.StoreUpdateResponseDto;
import com.example.noweat.global.argumentResolver.AuthUser;
import com.example.noweat.repository.menu.MenuRepository;
import com.example.noweat.repository.store.StoreRepository;
import com.example.noweat.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private MenuRepository menuRepository;

    @InjectMocks
    private StoreService storeService;

    @Test
    @DisplayName("가게 등록")
    void saveStore()  {

        // given
        AuthUser authUser = new AuthUser(1L, "email", UserRole.OWNER);

        User user = User.builder()
                .email(authUser.getEmail())
                .password("encodedPassword")
                .name("홍길동")
                .address("서울시 중구")
                .userRole(authUser.getUserRole())
                .storeCount(0L)
                .isDeleted(false)
                .build();
        ReflectionTestUtils.setField(user, "id", 1L);

        StoreSaveRequestDto requestDto = new StoreSaveRequestDto();
        ReflectionTestUtils.setField(requestDto, "name", "김밥천국");
        ReflectionTestUtils.setField(requestDto, "address", "서울시 중구");
        ReflectionTestUtils.setField(requestDto, "storeCategory", "KOREAN");
        ReflectionTestUtils.setField(requestDto, "minOrderPrice", 10000L);
        ReflectionTestUtils.setField(requestDto, "openTime", LocalTime.of(9, 0));
        ReflectionTestUtils.setField(requestDto, "closedTime", LocalTime.of(21, 0));

        Store store = Store.builder()
                .user(user)
                .name(requestDto.getName())
                .address(requestDto.getAddress())
                .storeCategory(StoreCategory.of(requestDto.getStoreCategory()))
                .minOrderPrice(requestDto.getMinOrderPrice())
                .openTime(requestDto.getOpenTime())
                .closedTime(requestDto.getClosedTime())
                .build();

        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(storeRepository.save(any())).willReturn(store);

        // when
        StoreSaveResponseDto storeSaveResponseDto = storeService.saveStore(authUser, requestDto);

        // then
        assertNotNull(storeSaveResponseDto);
        assertEquals(store.getName(), storeSaveResponseDto.getName());
        assertEquals(store.getCreatedAt(), storeSaveResponseDto.getCreatedAt());

        verify(userRepository, times(1)).findById(any());
        verify(storeRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("가게 전체 조회")
    void findAllStore()  {

        // given
        AuthUser authUser = new AuthUser(1L, "email", UserRole.OWNER);

        User user = User.builder()
                .email(authUser.getEmail())
                .password("encodedPassword")
                .name("홍길동")
                .address("서울시 중구")
                .userRole(authUser.getUserRole())
                .storeCount(0L)
                .isDeleted(false)
                .build();
        ReflectionTestUtils.setField(user, "id", 1L);

        StoreSaveRequestDto requestDto = new StoreSaveRequestDto();
        ReflectionTestUtils.setField(requestDto, "name", "김밥천국");
        ReflectionTestUtils.setField(requestDto, "address", "서울시 중구");
        ReflectionTestUtils.setField(requestDto, "storeCategory", "KOREAN");
        ReflectionTestUtils.setField(requestDto, "minOrderPrice", 10000L);
        ReflectionTestUtils.setField(requestDto, "openTime", LocalTime.now());
        ReflectionTestUtils.setField(requestDto, "closedTime", LocalTime.now());

        Store store = Store.builder()
                .user(user)
                .name(requestDto.getName())
                .address(requestDto.getAddress())
                .storeCategory(StoreCategory.of(requestDto.getStoreCategory()))
                .minOrderPrice(requestDto.getMinOrderPrice())
                .openTime(requestDto.getOpenTime())
                .closedTime(requestDto.getClosedTime())
                .build();

        given(storeRepository.findAllStore()).willReturn(Collections.singletonList(store));

        // when
        List<StoreFindAllResponseDto> allStores = storeService.findAllStore(null);

        // then
        assertNotNull(allStores);
        assertEquals(1, allStores.size());
        assertEquals("김밥천국", allStores.get(0).getName());

        verify(storeRepository, times(1)).findAllStore();
    }

    @Test
    @DisplayName("가게 단건 조회")
    void findOneStore()  {
        // given
        AuthUser authUser = new AuthUser(1L, "email", UserRole.OWNER);

        User user = User.builder()
                .email(authUser.getEmail())
                .password("encodedPassword")
                .name("홍길동")
                .address("서울시 중구")
                .userRole(authUser.getUserRole())
                .storeCount(0L)
                .isDeleted(false)
                .build();
        ReflectionTestUtils.setField(user, "id", 1L);

        Store store = Store.builder()
                .user(user)
                .name("김밥천국")
                .address("서울시 중구")
                .storeCategory(StoreCategory.KOREAN)
                .minOrderPrice(10000L)
                .openTime(LocalTime.of(9, 0))
                .closedTime(LocalTime.of(22, 0))
                .averageRating(4.5)
                .isClosed(false)
                .build();

        Menu menu1 = Menu.builder()
                .store(store)
                .name("김밥")
                .price(3000L)
                .build();

        Menu menu2 = Menu.builder()
                .store(store)
                .name("라면")
                .price(4000L)
                .build();

        List<Menu> menus = Arrays.asList(menu1, menu2);

        given(storeRepository.findById(store.getId())).willReturn(Optional.of(store));
        given(menuRepository.findMenuByStoreId(store.getId())).willReturn(menus);


        // when
        StoreFindOneResponseDto findStore = storeService.findOneStore(store.getId());

        // then
        assertEquals(store.getName(), findStore.getName());
        assertEquals("김밥", findStore.getMenuList().get(0).getMenuName());
        assertEquals(3000L, findStore.getMenuList().get(0).getMenuPrice());
        assertEquals("라면", findStore.getMenuList().get(1).getMenuName());
        assertEquals(4000L, findStore.getMenuList().get(1).getMenuPrice());

        verify(storeRepository, times(1)).findById(store.getId());
        verify(menuRepository, times(1)).findMenuByStoreId(store.getId());
    }

    @Test
    @DisplayName("가게 정보 수정")
    void updateStore() {
        // given
        AuthUser authUser = new AuthUser(1L, "email", UserRole.OWNER);

        User user = User.builder()
                .email(authUser.getEmail())
                .password("encodedPassword")
                .name("홍길동")
                .address("서울시 중구")
                .userRole(authUser.getUserRole())
                .storeCount(1L)
                .isDeleted(false)
                .build();
        ReflectionTestUtils.setField(user, "id", 1L);

        Store store = Store.builder()
                .user(user)
                .name("김밥천국")
                .address("서울시 중구")
                .storeCategory(StoreCategory.KOREAN)
                .minOrderPrice(10000L)
                .openTime(LocalTime.of(9, 0))
                .closedTime(LocalTime.of(22, 0))
                .isClosed(false)
                .build();

        StoreUpdateRequestDto requestDto = new StoreUpdateRequestDto();
        ReflectionTestUtils.setField(requestDto, "name", "우리김밥");
        ReflectionTestUtils.setField(requestDto, "address", "서울시 강남구");
        ReflectionTestUtils.setField(requestDto, "storeCategory", "KOREAN");
        ReflectionTestUtils.setField(requestDto, "minOrderPrice", 10000L);
        ReflectionTestUtils.setField(requestDto, "openTime", LocalTime.of(8, 30));
        ReflectionTestUtils.setField(requestDto, "closedTime", LocalTime.of(21, 0));


        given(userRepository.findById(authUser.getId())).willReturn(Optional.of(user));
        given(storeRepository.findById(store.getId())).willReturn(Optional.of(store));
        given(storeRepository.save(any())).willReturn(store);

        // when
        StoreUpdateResponseDto responseDto = storeService.updateStore(authUser, store.getId(), requestDto);

        // then
        assertNotNull(responseDto);
        assertEquals(requestDto.getName(), responseDto.getName());
        assertEquals(requestDto.getAddress(), responseDto.getAddress());

        verify(userRepository, times(1)).findById(authUser.getId());
        verify(storeRepository, times(1)).findById(store.getId());
        verify(storeRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("가게 삭제")
    void deleteStore() {
        // given
        AuthUser authUser = new AuthUser(1L, "email", UserRole.OWNER);

        User user = User.builder()
                .email(authUser.getEmail())
                .password("encodedPassword")
                .name("홍길동")
                .address("서울시 중구")
                .userRole(authUser.getUserRole())
                .storeCount(1L)
                .isDeleted(false)
                .build();
        ReflectionTestUtils.setField(user, "id", 1L);

        Store store = Store.builder()
                .user(user)
                .name("김밥천국")
                .address("서울시 중구")
                .storeCategory(StoreCategory.KOREAN)
                .minOrderPrice(10000L)
                .openTime(LocalTime.of(9, 0))
                .closedTime(LocalTime.of(22, 0))
                .isClosed(false)
                .build();

        given(userRepository.findById(authUser.getId())).willReturn(Optional.of(user));
        given(storeRepository.findById(store.getId())).willReturn(Optional.of(store));

        // when
        storeService.deleteStore(authUser, store.getId());

        // then
        assertTrue(store.isClosed());
        assertEquals(0, user.getStoreCount());

        verify(userRepository, times(1)).findById(authUser.getId());
        verify(storeRepository, times(1)).findById(store.getId());
        verify(userRepository, times(1)).save(user);
    }
}