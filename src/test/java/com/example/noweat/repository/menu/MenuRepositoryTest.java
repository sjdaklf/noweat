package com.example.noweat.repository.menu;

import com.example.noweat.domain.menu.Menu;
import com.example.noweat.domain.store.Store;
import com.example.noweat.domain.user.User;
import com.example.noweat.repository.store.StoreRepository;
import com.example.noweat.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MenuRepositoryTest {

    @Autowired UserRepository userRepository;
    @Autowired StoreRepository storeRepository;
    @Autowired MenuRepository menuRepository;

    @Test
    @Transactional
    @DisplayName("storeId로 메뉴 리스트 조회")
    void findMenuByStoreIdTest() {
        // given
        String menuName1 = "메뉴이름1";
        String menuName2 = "메뉴이름2";

        Store store = new Store();
        storeRepository.save(store);

        List<Menu> menus = new ArrayList<>();

        Menu menu1 = Menu.builder()
                .store(store)
                .name(menuName1)
                .build();
        menus.add(menu1);

        Menu menu2 = Menu.builder()
                .store(store)
                .name(menuName2)
                .build();
        menus.add(menu2);

        menuRepository.save(menu1);
        menuRepository.save(menu2);

        System.out.println(menu1.getStore().getId());

        // when
        List<Menu> findMenus = menuRepository.findMenuByStoreId(1L);

        // then
        assertNotNull(findMenus);
        assertEquals(menus.get(0).getId(), findMenus.get(0).getId());
        assertEquals(menus.get(0).getName(), findMenus.get(0).getName());
        assertEquals(menus.get(1).getId(), findMenus.get(1).getId());
        assertEquals(menus.get(1).getName(), findMenus.get(1).getName());
    }

    @Test
    @Transactional
    @DisplayName("menuId로 메뉴 단건 조회")
    void findByIdTest() {
        // given
        String menuName = "메뉴이름";

        Menu menu = Menu.builder()
                .name(menuName)
                .isDeleted(false)
                .build();
        menuRepository.save(menu);

        // when
        Optional<Menu> findMenu = menuRepository.findById(menu.getId());

        // then
        assertNotNull(findMenu);
        assertEquals(menu.getId(), findMenu.get().getId());
        assertEquals(menu.getName(), findMenu.get().getName());
    }

    @Test
    @Transactional
    @DisplayName("중복된 메뉴가 있는지 조회")
    void existsByUser_IdAndName() {
        // given
        String menuName = "메뉴이름";

        User user = new User();
        userRepository.save(user);

        Menu menu = Menu.builder()
                .user(user)
                .name(menuName)
                .isDeleted(false)
                .build();
        menuRepository.save(menu);

        // when
        boolean result = menuRepository.existsByUser_IdAndName(1L, menuName);

        // then
        assertTrue(result);
    }
}
