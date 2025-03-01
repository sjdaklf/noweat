package com.example.noweat.service.menu;

import com.example.noweat.domain.menu.Menu;
import com.example.noweat.domain.store.Store;
import com.example.noweat.dto.menu.request.MenuSaveRequest;
import com.example.noweat.dto.menu.request.MenuUpdateRequest;
import com.example.noweat.dto.menu.response.MenuResponse;
import com.example.noweat.dto.menu.response.MenuSaveResponse;
import com.example.noweat.dto.menu.response.MenuUpdateResponse;
import com.example.noweat.repository.menu.MenuRepository;
import com.example.noweat.repository.store.StoreRepository;
import com.example.noweat.service.exception.NotFoundException;
import com.example.noweat.service.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public MenuSaveResponse saveMenu(Long storeId, MenuSaveRequest request) {

        Store store = storeRepository.findById(storeId).orElseThrow(() ->
                new NotFoundException(ErrorCode.STORE_NOT_EXIST));

        Menu menu = Menu.builder()
//                .user()
                .store(store)
                .menuName(request.getMenuName())
                .menuPrice(request.getMenuPrice())
                .build();

        Menu savedMenu = menuRepository.save(menu);

        return new MenuSaveResponse(
                savedMenu.getId(),
                savedMenu.getMenuName(),
                savedMenu.getMenuPrice(),
                savedMenu.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<MenuResponse> findAllMenu(Long storeId) {

        if(!storeRepository.existsById(storeId)) { // store가 없을 때
            throw new NotFoundException(ErrorCode.STORE_NOT_EXIST);
        }

        List<Menu> findMenus = new ArrayList<>(); // 나중에 지우기

        Menu menu1 = Menu.builder()
                .menuName("메뉴이름1")
                .menuPrice(1000L)
                .build();

        menu1.setId(1L);
        findMenus.add(menu1);

        Menu menu2 = Menu.builder()
                .menuName("메뉴이름2")
                .menuPrice(1000L)
                .build();

        menu2.setId(2L);
        findMenus.add(menu2); // 여기까지 지우기

//        List<Menu> findMenus = menuRepository.findMenuByStore_Id(storeId);

        List<MenuResponse> dtoList = new ArrayList<>();
        for (Menu menu : findMenus) {
            MenuResponse dto = new MenuResponse(
                    menu.getId(),
                    menu.getMenuName(),
                    menu.getMenuPrice()
            );
            dtoList.add(dto);
        }
        return dtoList;
    }


    public MenuUpdateResponse updateMenu(Long menuId, MenuUpdateRequest request) {

        Menu menu = Menu.builder() // 지우기
                .menuName("메뉴이름1")
                .menuPrice(1000L)
                .build();

        menu.setId(1L);

//        Menu menu = menuRepository.findById(menuId).orElseThrow(() ->
//                new NotFoundException(ErrorCode.MENU_NOT_EXIST));

        menu.updateMenu(request.getMenuName(), request.getMenuPrice());

        return new MenuUpdateResponse(menu.getMenuName(), menu.getMenuPrice(), menu.getCreatedAt(), menu.getUpdatedAt());
    }

    public void deleteMenu(Long menuId) {
        Menu menu = Menu.builder() // 지우기
                .menuName("메뉴이름1")
                .menuPrice(1000L)
                .build();

        menu.setId(1L);

//        Menu menu = menuRepository.findById(menuId).orElseThrow(() ->
//                new NotFoundException(ErrorCode.MENU_NOT_EXIST));

        menu.deleteMenu();
    }
}
