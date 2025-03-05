package com.example.noweat.service.store;

import com.example.noweat.domain.menu.Menu;
import com.example.noweat.domain.store.Store;
import com.example.noweat.domain.store.StoreCategory;
import com.example.noweat.domain.user.User;
import com.example.noweat.domain.user.UserRole;
import com.example.noweat.dto.store.request.StoreSaveRequestDto;
import com.example.noweat.dto.store.request.StoreUpdateRequestDto;
import com.example.noweat.dto.store.response.*;
import com.example.noweat.global.argumentResolver.AuthUser;
import com.example.noweat.repository.menu.MenuRepository;
import com.example.noweat.repository.store.StoreRepository;
import com.example.noweat.repository.user.UserRepository;
import com.example.noweat.service.exception.*;
import com.example.noweat.service.exception.enums.ErrorCode;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;

    @Transactional
    public StoreSaveResponseDto saveStore(AuthUser authUser, StoreSaveRequestDto storeSaveRequestDto) {
        StoreCategory storeCategory = StoreCategory.of(storeSaveRequestDto.getStoreCategory());

        User findUser = userRepository.findById(authUser.getId()).orElseThrow(
                () -> new NotFoundException(ErrorCode.NOT_FOUND_USER));

        verifyUser(findUser);

        if (findUser.getUserRole() != UserRole.OWNER) {
            throw new UnauthorizedException(ErrorCode.NOT_OWNER);
        }

        if (findUser.getStoreCount() >= 3) {
            throw new ConflictException(ErrorCode.MAX_STORE_LIMIT_EXCEEDED);
        }

        if (storeSaveRequestDto.getOpenTime().isAfter(storeSaveRequestDto.getClosedTime()) ||
                storeSaveRequestDto.getOpenTime().equals(storeSaveRequestDto.getClosedTime())) {
            throw new BadRequestException(ErrorCode.INVALID_OPENTIME_CLOSEDTIME);
        }

        Store store = Store.builder()
                .user(findUser)
                .name(storeSaveRequestDto.getName())
                .address(storeSaveRequestDto.getAddress())
                .storeCategory(storeCategory)
                .minOrderPrice(storeSaveRequestDto.getMinOrderPrice())
                .openTime(storeSaveRequestDto.getOpenTime())
                .closedTime(storeSaveRequestDto.getClosedTime())
                .build();

        Store saveStore = storeRepository.save(store);

        findUser.increaseStoreCount();
        userRepository.save(findUser);

        return StoreSaveResponseDto.builder()
                .id(saveStore.getId())
                .name(saveStore.getName())
                .createdAt(saveStore.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<StoreFindAllResponseDto> findAllStore(String name) {
        List<Store> stores;

        if (name != null && !name.isBlank()) {
            stores = storeRepository.findByStoreNameContaining(name);
        } else {
            stores = storeRepository.findAllStore();
        }

        List<StoreFindAllResponseDto> dtos = new ArrayList<>();
        for (Store store : stores) {
            dtos.add(StoreFindAllResponseDto.builder()
                    .id(store.getId())
                    .name(store.getName())
                    .storeCategory(store.getStoreCategory())
                    .minOrderPrice(store.getMinOrderPrice())
                    .averageRating(store.getAverageRating())
                    .build());
        }

        return dtos;
    }


    @Transactional(readOnly = true)
    public StoreFindOneResponseDto findOneStore(Long storeId) {
        Store findStore = storeRepository.findById(storeId).orElseThrow(
                () -> new NotFoundException(ErrorCode.STORE_NOT_EXIST));

        verifyStore(findStore);

        List<Menu> findMenus = menuRepository.findMenuByStoreId(storeId);

        List<StoreMenuResponseDto> dtos = new ArrayList<>();
        for (Menu menu : findMenus) {
            dtos.add(StoreMenuResponseDto.builder()
                    .menuId(menu.getId())
                    .menuName(menu.getName())
                    .menuPrice(menu.getPrice())
                    .build());
        }

        return StoreFindOneResponseDto.builder()
                .id(findStore.getId())
                .name(findStore.getName())
                .address(findStore.getAddress())
                .storeCategory(findStore.getStoreCategory())
                .minOrderPrice(findStore.getMinOrderPrice())
                .openTime(findStore.getOpenTime())
                .closedTime(findStore.getClosedTime())
                .averageRating(findStore.getAverageRating())
                .menuList(dtos)
                .createdAt(findStore.getCreatedAt())
                .updatedAt(findStore.getUpdatedAt())
                .build();
    }

    public StoreUpdateResponseDto updateStore(AuthUser authUser, Long storeId, StoreUpdateRequestDto storeUpdateRequestDto) {
        StoreCategory storeCategory = StoreCategory.of(storeUpdateRequestDto.getStoreCategory());

        User findUser = userRepository.findById(authUser.getId()).orElseThrow(
                () -> new NotFoundException(ErrorCode.NOT_FOUND_USER));

        verifyUser(findUser);

        if (findUser.getUserRole() != UserRole.OWNER) {
            throw new UnauthorizedException(ErrorCode.NOT_OWNER);
        }

        Store findStore = storeRepository.findById(storeId).orElseThrow(
                () -> new NotFoundException(ErrorCode.STORE_NOT_EXIST));

        verifyStore(findStore);

        if (!findUser.getId().equals(findStore.getUser().getId())) {
            throw new UnauthorizedException(ErrorCode.NOT_STORE_OWNER);
        }

        if (storeUpdateRequestDto.getOpenTime().isAfter(storeUpdateRequestDto.getClosedTime()) ||
                storeUpdateRequestDto.getOpenTime().equals(storeUpdateRequestDto.getClosedTime())) {
            throw new BadRequestException(ErrorCode.INVALID_OPENTIME_CLOSEDTIME);
        }

        findStore.updateStore(
                storeUpdateRequestDto.getName(),
                storeUpdateRequestDto.getAddress(),
                storeCategory,
                storeUpdateRequestDto.getMinOrderPrice(),
                storeUpdateRequestDto.getOpenTime(),
                storeUpdateRequestDto.getClosedTime()
        );

        Store savedStore = storeRepository.save(findStore);

        return StoreUpdateResponseDto.builder()
                .id(savedStore.getId())
                .name(savedStore.getName())
                .address(savedStore.getAddress())
                .storeCategory(savedStore.getStoreCategory())
                .minOrderPrice(savedStore.getMinOrderPrice())
                .openTime(savedStore.getOpenTime())
                .closedTime(savedStore.getClosedTime())
                .createdAt(savedStore.getCreatedAt())
                .updatedAt(savedStore.getUpdatedAt())
                .build();
    }

    @Transactional
    public void deleteStore(AuthUser authUser, Long storeId) {
        User findUser = userRepository.findById(authUser.getId()).orElseThrow(
                () -> new NotFoundException(ErrorCode.NOT_FOUND_USER));

        verifyUser(findUser);

        if (findUser.getUserRole() != UserRole.OWNER) {
            throw new UnauthorizedException(ErrorCode.NOT_OWNER);
        }

        Store findStore = storeRepository.findById(storeId).orElseThrow(
                () -> new NotFoundException(ErrorCode.STORE_NOT_EXIST));

        verifyStore(findStore);

        if (!findUser.getId().equals(findStore.getUser().getId())) {
            throw new UnauthorizedException(ErrorCode.NOT_STORE_OWNER);
        }

        findStore.deleteStore(true);

        findUser.decreaseStoreCount();
        userRepository.save(findUser);
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
}