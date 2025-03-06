package com.example.noweat.dto.user.response;

import com.example.noweat.domain.store.StoreCategory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
public class UserStoreResponseDto {
    private final Long id;
    private final String name;
    private final StoreCategory storeCategory;
    private final Long minOrderPrice;
    private final Double averageRating;

    @Builder
    public UserStoreResponseDto(Long id, String name, StoreCategory storeCategory, Long minOrderPrice, Double averageRating) {
        this.id = id;
        this.name = name;
        this.storeCategory = storeCategory;
        this.minOrderPrice = minOrderPrice;
        this.averageRating = averageRating;
    }
}
