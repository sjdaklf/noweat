package com.example.noweat.repository.menu;

import com.example.noweat.domain.menu.Menu;
import com.example.noweat.domain.store.Store;
import com.example.noweat.repository.store.StoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MenuRepositoryTest {

    @Autowired MenuRepository menuRepository;
    @Autowired StoreRepository storeRepository;

    @Test
    @DisplayName("storeId로 메뉴 찾기")
    void findMenuByStoreId() {
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
                .isDeleted(true)
                .build();
        menus.add(menu2);

        menuRepository.save(menu1);
        menuRepository.save(menu2);

        // when
        List<Menu> findMenus = menuRepository.findMenuByStoreId(1L);

        // then
        assertNotNull(findMenus);
        assertEquals(menus.get(0).getName(), findMenus.get(0).getName());
    }
}
