package com.example.noweat.service.menu;

import com.example.noweat.domain.menu.Menu;
import com.example.noweat.domain.store.Store;
import com.example.noweat.domain.user.User;
import com.example.noweat.domain.user.UserRole;
import com.example.noweat.dto.menu.request.MenuSaveRequestDto;
import com.example.noweat.dto.menu.request.MenuUpdateRequestDto;
import com.example.noweat.dto.menu.response.MenuResponseDto;
import com.example.noweat.dto.menu.response.MenuSaveResponseDto;
import com.example.noweat.dto.menu.response.MenuUpdateResponseDto;
import com.example.noweat.global.argumentResolver.AuthUser;
import com.example.noweat.repository.menu.MenuRepository;
import com.example.noweat.repository.store.StoreRepository;
import com.example.noweat.repository.user.UserRepository;
import com.example.noweat.service.exception.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private MenuService menuService;

    @Test
    @DisplayName("메뉴 저장 성공")
    void saveMenuTest() {
        // given
        long storeId = 1L;
        MenuSaveRequestDto request = new MenuSaveRequestDto("메뉴이름", 1000L);

        AuthUser authUser = new AuthUser(1L, "email", UserRole.OWNER);

        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);

        Store store = new Store();
        ReflectionTestUtils.setField(store, "user", user);

        Menu menu = new Menu();
        LocalDateTime localDateTime = LocalDateTime.now();
        ReflectionTestUtils.setField(menu, "name", request.getName());
        ReflectionTestUtils.setField(menu, "price", request.getPrice());
        ReflectionTestUtils.setField(menu, "createdAt", localDateTime);

        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(storeRepository.findById(any())).willReturn(Optional.of(store));
        given(menuRepository.existsByUser_IdAndName(any(), any())).willReturn(false);
        given(menuRepository.save(any())).willReturn(menu);

        // when
        MenuSaveResponseDto menuSaveResponse = menuService.saveMenu(authUser, storeId, request);

        // then
        assertNotNull(menuSaveResponse);
        assertEquals(menu.getName(), menuSaveResponse.getName());
        assertEquals(menu.getPrice(), menuSaveResponse.getPrice());
        assertEquals(menu.getCreatedAt(), menuSaveResponse.getCreatedAt());

        verify(userRepository, times(1)).findById(any());
        verify(storeRepository, times(1)).findById(any());
        verify(menuRepository, times(1)).existsByUser_IdAndName(any(), any());
        verify(menuRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("메뉴 전체 조회 성공")
    void findAllMenuTest() {
        // given
        long storeId = 1L;
        MenuSaveRequestDto request1 = new MenuSaveRequestDto("메뉴이름1", 1000L);
        MenuSaveRequestDto request2 = new MenuSaveRequestDto("메뉴이름2", 1000L);

        List<Menu> menuList = new ArrayList<>();

        Store store = new Store();

        Menu menu1 = new Menu();
        ReflectionTestUtils.setField(menu1, "name", request1.getName());
        ReflectionTestUtils.setField(menu1, "price", request1.getPrice());
        menuList.add(menu1);

        Menu menu2 = new Menu();
        ReflectionTestUtils.setField(menu2, "name", request2.getName());
        ReflectionTestUtils.setField(menu2, "price", request2.getPrice());
        menuList.add(menu2);

        List<MenuResponseDto> list = menuList.stream().map(menu -> new MenuResponseDto(menu.getId(), menu.getName(), menu.getPrice())).toList();

        given(storeRepository.findById(any())).willReturn(Optional.of(store));
        given(menuRepository.findMenuByStoreId(any())).willReturn(menuList);

        // when
        List<MenuResponseDto> allMenu = menuService.findAllMenu(storeId);

        // then
        assertEquals(list.get(0).getName(), allMenu.get(0).getName());
        assertEquals(list.get(0).getPrice(), allMenu.get(0).getPrice());
        assertEquals(list.get(1).getName(), allMenu.get(1).getName());
        assertEquals(list.get(1).getPrice(), allMenu.get(1).getPrice());

        verify(storeRepository, times(1)).findById(any());
        verify(menuRepository, times(1)).findMenuByStoreId(any());
    }


    @Test
    @DisplayName("메뉴 수정 성공")
    void updateMenuTest() {
        // given
        long menuId = 1L;
        AuthUser authUser = new AuthUser(1L, "email", UserRole.OWNER);
        MenuUpdateRequestDto request = new MenuUpdateRequestDto("메뉴수정이름", 1000L);

        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);

        Menu menu = new Menu();
        LocalDateTime localDateTime = LocalDateTime.now();
        ReflectionTestUtils.setField(menu, "user", user);
        ReflectionTestUtils.setField(menu, "name", "메뉴이름");
        ReflectionTestUtils.setField(menu, "price", 2000L);
        ReflectionTestUtils.setField(menu, "createdAt", localDateTime);

        Menu updateMenu = new Menu();
        ReflectionTestUtils.setField(updateMenu, "name", request.getName());
        ReflectionTestUtils.setField(updateMenu, "price", request.getPrice());
        ReflectionTestUtils.setField(updateMenu, "createdAt", menu.getCreatedAt());

        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));
        given(menuRepository.save(any())).willReturn(updateMenu);

        // when
        MenuUpdateResponseDto updatedMenu = menuService.updateMenu(authUser, menuId, request);

        // then
        assertEquals(updateMenu.getName(), updatedMenu.getName());
        assertEquals(updateMenu.getPrice(), updatedMenu.getPrice());
        assertEquals(updateMenu.getCreatedAt(), updatedMenu.getCreatedAt());

        verify(userRepository, times(1)).findById(any());
        verify(menuRepository, times(1)).findById(any());
        verify(menuRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("메뉴 삭제 성공")
    void deleteMenuTest() {
        // given
        long menuId = 1L;
        AuthUser authUser = new AuthUser(1L, "email", UserRole.OWNER);

        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "userRole", UserRole.OWNER);

        Menu menu = new Menu();
        ReflectionTestUtils.setField(menu, "user", user);

        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        // when
        menuService.deleteMenu(authUser, menuId);

        // then
        assertTrue(menu.isDeleted());

        verify(userRepository, times(1)).findById(any());
        verify(menuRepository, times(1)).findById(any());
    }

    @Test
    @DisplayName("사용자 역할이 OWNER가 아닐 때 예외")
    void InvalidUserRoleExceptionTest() {
        // given
        long storeId = 1L;
        long menuId = 1L;
        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
        MenuSaveRequestDto saveRequest = new MenuSaveRequestDto("메뉴이름", 1000L);
        MenuUpdateRequestDto updateRequest = new MenuUpdateRequestDto("메뉴수정이름", 2000L);

        // when, then
        assertThatThrownBy(() -> menuService.saveMenu(authUser, storeId, saveRequest)).isInstanceOf(ForbiddenException.class);
        assertThatThrownBy(() -> menuService.updateMenu(authUser, menuId, updateRequest)).isInstanceOf(ForbiddenException.class);
        assertThatThrownBy(() -> menuService.deleteMenu(authUser, menuId)).isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("authUserId로 조회한 결과 User 조회 실패")
    void NotFoundUserExceptionTest() {
        // given
        long storeId = 1L;
        AuthUser authUser = new AuthUser(1L, "email", UserRole.OWNER);
        MenuSaveRequestDto saveRequest = new MenuSaveRequestDto("메뉴이름", 1000L);
        MenuUpdateRequestDto updateRequest = new MenuUpdateRequestDto("메뉴수정이름", 2000L);

        given(userRepository.findById(any())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> menuService.saveMenu(authUser, storeId, saveRequest)).isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> menuService.updateMenu(authUser, storeId, updateRequest)).isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> menuService.deleteMenu(authUser, storeId)).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("이미 탈퇴한 사용자일 경우 예외")
    void UserAlReadyDeletedExceptionTest() {
        // given
        long storeId = 1L;
        AuthUser authUser = new AuthUser(1L, "email", UserRole.OWNER);
        MenuSaveRequestDto saveRequest = new MenuSaveRequestDto("메뉴이름", 1000L);
        MenuUpdateRequestDto updateRequest = new MenuUpdateRequestDto("메뉴수정이름", 2000L);

        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "isDeleted", true);

        given(userRepository.findById(any())).willReturn(Optional.of(user));

        // when, then
        assertThatThrownBy(() -> menuService.saveMenu(authUser, storeId, saveRequest)).isInstanceOf(GoneException.class);
        assertThatThrownBy(() -> menuService.updateMenu(authUser, storeId, updateRequest)).isInstanceOf(GoneException.class);
        assertThatThrownBy(() -> menuService.deleteMenu(authUser, storeId)).isInstanceOf(GoneException.class);
    }

    @Test
    @DisplayName("storeId로 조회를 한 결과 가게 조회 실패")
    void StoreNotExistExceptionTest() {
        // given
        long storeId = 1L;
        AuthUser authUser = new AuthUser(1L, "email", UserRole.OWNER);
        MenuSaveRequestDto saveRequest = new MenuSaveRequestDto("메뉴이름", 1000L);

        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);

        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(storeRepository.findById(any())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> menuService.saveMenu(authUser, storeId, saveRequest)).isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> menuService.findAllMenu(storeId)).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("요청한 사용자의 가게가 아닐 때 예외")
    void StoreNotMatchExceptionTest() {
        // given
        long storeId = 1L;
        AuthUser authUser = new AuthUser(1L, "email", UserRole.OWNER);
        MenuSaveRequestDto saveRequest = new MenuSaveRequestDto("메뉴이름", 1000L);

        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);

        User storeUser = new User();
        ReflectionTestUtils.setField(storeUser, "id", 2L);

        Store store = new Store();
        ReflectionTestUtils.setField(store, "user", storeUser);

        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(storeRepository.findById(any())).willReturn(Optional.of(store));

        // when, then
        assertThatThrownBy(() -> menuService.saveMenu(authUser, storeId, saveRequest)).isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("이미 폐업한 가게일 경우 예외")
    void StoreClosedExceptionTest() {
        // given
        long storeId = 1L;
        AuthUser authUser = new AuthUser(1L, "email", UserRole.OWNER);
        MenuSaveRequestDto saveRequest = new MenuSaveRequestDto("메뉴이름", 1000L);

        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);

        Store store = new Store();
        ReflectionTestUtils.setField(store, "user", user);
        ReflectionTestUtils.setField(store, "isClosed", true);

        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(storeRepository.findById(any())).willReturn(Optional.of(store));

        // when, then
        assertThatThrownBy(() -> menuService.saveMenu(authUser, storeId, saveRequest)).isInstanceOf(GoneException.class);
        assertThatThrownBy(() -> menuService.findAllMenu(storeId)).isInstanceOf(GoneException.class);
    }

    @Test
    @DisplayName("중복된 이름의 메뉴일 경우 예외")
    void DuplicateMenuExceptionTest() {
        long storeId = 1L;
        AuthUser authUser = new AuthUser(1L, "email", UserRole.OWNER);
        MenuSaveRequestDto saveRequest = new MenuSaveRequestDto("메뉴이름", 1000L);

        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);

        Store store = new Store();
        ReflectionTestUtils.setField(store, "user", user);

        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(storeRepository.findById(any())).willReturn(Optional.of(store));
        given(menuRepository.existsByUser_IdAndName(any(), any())).willReturn(true);

        // when, then
        assertThatThrownBy(() -> menuService.saveMenu(authUser, storeId, saveRequest)).isInstanceOf(ConflictException.class);
    }

    @Test
    @DisplayName("menuId로 조회를 한 결과 메뉴 조회 실패")
    void MenuNotExistExceptionTest() {
        // given
        long storeId = 1L;
        AuthUser authUser = new AuthUser(1L, "email", UserRole.OWNER);
        MenuUpdateRequestDto updateRequest = new MenuUpdateRequestDto("메뉴수정이름", 2000L);

        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);

        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(menuRepository.findById(any())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> menuService.updateMenu(authUser, storeId, updateRequest)).isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> menuService.deleteMenu(authUser, storeId)).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("자신이 만든 메뉴가 맞지 않으면 예외")
    void MenuNotMatchExceptionTest() {
        // given
        long storeId = 1L;
        AuthUser authUser = new AuthUser(1L, "email", UserRole.OWNER);
        MenuUpdateRequestDto updateRequest = new MenuUpdateRequestDto("메뉴수정이름", 2000L);

        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);

        User storeUser = new User();
        ReflectionTestUtils.setField(storeUser, "id", 2L);

        Menu menu = new Menu();
        ReflectionTestUtils.setField(menu, "user", storeUser);

        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        //when, then
        assertThatThrownBy(() -> menuService.updateMenu(authUser, storeId, updateRequest)).isInstanceOf(ForbiddenException.class);
        assertThatThrownBy(() -> menuService.deleteMenu(authUser, storeId)).isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("이미 삭제된 메뉴일 경우 예외")
    void MenuDeletedExceptionTest() {
        // given
        long storeId = 1L;
        AuthUser authUser = new AuthUser(1L, "email", UserRole.OWNER);
        MenuUpdateRequestDto updateRequest = new MenuUpdateRequestDto("메뉴수정이름", 2000L);

        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);

        Menu menu = new Menu();
        ReflectionTestUtils.setField(menu, "user", user);
        ReflectionTestUtils.setField(menu, "isDeleted", true);

        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        // when, then
        assertThatThrownBy(() -> menuService.updateMenu(authUser, storeId, updateRequest)).isInstanceOf(GoneException.class);
        assertThatThrownBy(() -> menuService.deleteMenu(authUser, storeId)).isInstanceOf(GoneException.class);
    }


}
