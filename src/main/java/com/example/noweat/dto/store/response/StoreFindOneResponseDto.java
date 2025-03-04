package com.example.noweat.dto.store.response;

import com.example.noweat.domain.menu.Menu;
import com.example.noweat.domain.store.StoreCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
public class StoreFindOneResponseDto {

    private final Long id;
    private final String storeName;
    private final String storeAddress;
    private final StoreCategory storeCategory;
    private final Long minOrderPrice;
    private final LocalTime openTime;
    private final LocalTime closedTime;
    private final Double averageRating;
    private final List<StoreMenuResponseDto> menuList;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime updatedAt;

    @Builder
    public StoreFindOneResponseDto(Long id, String storeName, String storeAddress, StoreCategory storeCategory, Long minOrderPrice, LocalTime openTime, LocalTime closedTime, Double averageRating, List<StoreMenuResponseDto> menuList, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.storeName = storeName;
        this.storeAddress = storeAddress;
        this.storeCategory = storeCategory;
        this.minOrderPrice = minOrderPrice;
        this.openTime = openTime;
        this.closedTime = closedTime;
        this.averageRating = averageRating;
        this.menuList = menuList;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
