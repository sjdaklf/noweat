package com.example.noweat.dto.store.response;

import com.example.noweat.domain.store.StoreCategory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
public class StoreFindAllResponseDto {

    private final Long id;
    private final String storeName;
    private final StoreCategory storeCategory;
    private final Long minOrderPrice;
    private final Double averageRating;

    @Builder
    public StoreFindAllResponseDto(Long id, String storeName, StoreCategory storeCategory, Long minOrderPrice, Double averageRating) {
        this.id = id;
        this.storeName = storeName;
        this.storeCategory = storeCategory;
        this.minOrderPrice = minOrderPrice;
        this.averageRating = averageRating;
    }
}
