package com.example.noweat.dto.store.response;

import lombok.Builder;

public class StoreMenuResponseDto {
    private final Long menuId;
    private final String menuName;
    private final Long menuPrice;

    @Builder
    public StoreMenuResponseDto(Long menuId, String menuName, Long menuPrice) {
        this.menuId = menuId;
        this.menuName = menuName;
        this.menuPrice = menuPrice;
    }
}