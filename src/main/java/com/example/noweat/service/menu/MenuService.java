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
import com.example.noweat.service.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;

    @Transactional
    public MenuSaveResponseDto saveMenu(AuthUser authUser, Long storeId, MenuSaveRequestDto request) {

        User findUser = userRepository.findById(authUser.getId()).orElseThrow(() ->
                new NotFoundException(ErrorCode.NOT_FOUND_USER));

        verifyUser(findUser); // 탈퇴한 유저인지 확인

        if (!findUser.getUserRole().equals(UserRole.OWNER)) { // 사용자 역할이 OWNER 가 아니면 예외
            throw new BadRequestException(ErrorCode.INVALID_USER_ROLE);
        }

        Store findStore = storeRepository.findById(storeId).orElseThrow(() ->
                new NotFoundException(ErrorCode.STORE_NOT_EXIST));

        if (!findStore.getUser().getId().equals(findUser.getId())) { // 요청한 사용자의 가게가 맞는지 확인
            throw new ForbiddenException(ErrorCode.STORE_NOT_MATCH);
        }

        verifyStore(findStore); // 폐업한 가게인지 확인

        if (menuRepository.existsByUser_IdAndName(findUser.getId(), request.getName())) { // 중복된 메뉴 검증
            throw new ConflictException(ErrorCode.DUPLICATE_MENU);
        }

        Menu menu = Menu.builder()
                .user(findUser)
                .store(findStore)
                .name(request.getName())
                .price(request.getPrice())
                .build();

        Menu savedMenu = menuRepository.save(menu);

        return MenuSaveResponseDto.builder()
                .id(savedMenu.getId())
                .name(savedMenu.getName())
                .price(savedMenu.getPrice())
                .createdAt(savedMenu.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<MenuResponseDto> findAllMenu(Long storeId) {

        if (!storeRepository.existsStoreById(storeId)) { // store가 없을 때 (폐업된 가게는 조회하지 않음)
            throw new NotFoundException(ErrorCode.STORE_NOT_EXIST);
        }

        List<Menu> findMenus = menuRepository.findMenuByStoreId(storeId);

        return findMenus.stream()
                .map(menu -> new MenuResponseDto(menu.getId(), menu.getName(), menu.getPrice()))
                .toList();
    }

    public MenuUpdateResponseDto updateMenu(AuthUser authUser, Long menuId, MenuUpdateRequestDto request) {

        User findUser = userRepository.findById(authUser.getId()).orElseThrow(() ->
                new NotFoundException(ErrorCode.NOT_FOUND_USER));

        verifyUser(findUser); // 탈퇴한 유저인지 확인

        if (!findUser.getUserRole().equals(UserRole.OWNER)) { // 사용자 역할이 OWNER 가 아니면 예외
            throw new BadRequestException(ErrorCode.INVALID_USER_ROLE);
        }

        Menu findMenu = menuRepository.findById(menuId).orElseThrow(() ->
                new NotFoundException(ErrorCode.MENU_NOT_EXIST));

        if (!findMenu.getUser().getId().equals(findUser.getId())){ // 자신이 만든 메뉴가 맞는지 확인
            throw new ForbiddenException(ErrorCode.MENU_NOT_MATCH);
        }

        verifyMenu(findMenu); // 삭제된 메뉴인지 확인

        findMenu.updateMenu(request.getName(), request.getPrice());

        Menu savedMenu = menuRepository.save(findMenu);

        return MenuUpdateResponseDto.builder()
                .name(savedMenu.getName())
                .price(savedMenu.getPrice())
                .createdAt(savedMenu.getCreatedAt())
                .updatedAt(savedMenu.getUpdatedAt())
                .build();
    }

    @Transactional
    public void deleteMenu(AuthUser authUser, Long menuId) {

        User findUser = userRepository.findById(authUser.getId()).orElseThrow(() ->
                new NotFoundException(ErrorCode.NOT_FOUND_USER));

        verifyUser(findUser); // 탈퇴한 유저인지 확인

        if (!findUser.getUserRole().equals(UserRole.OWNER)) { // 사용자 역할이 OWNER 가 아니면 예외
            throw new BadRequestException(ErrorCode.INVALID_USER_ROLE);
        }

        Menu findMenu = menuRepository.findById(menuId).orElseThrow(() ->
                new NotFoundException(ErrorCode.MENU_NOT_EXIST));

        if (!findMenu.getUser().getId().equals(findUser.getId())){ // 자신이 만든 메뉴가 맞는지 확인
            throw new ForbiddenException(ErrorCode.MENU_NOT_MATCH);
        }

        verifyMenu(findMenu); // 삭제된 메뉴인지 확인

        findMenu.deleteMenu(); // 소프트 delete
    }

    public void verifyUser(User findUser) {
        if (findUser.isDeleted()) {
            throw new GoneException(ErrorCode.USER_ALREADY_DELETED);
        }
    }

    public void verifyStore(Store findStore) {
        if (findStore.isClosed()) {
            throw new GoneException(ErrorCode.STORE_CLOSED);
        }
    }

    public void verifyMenu(Menu findMenu) {
        if (findMenu.isDeleted()) {
            throw new GoneException(ErrorCode.MENU_DELETED);
        }
    }
}
