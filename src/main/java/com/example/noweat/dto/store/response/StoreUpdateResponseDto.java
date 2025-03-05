package com.example.noweat.dto.store.response;

import com.example.noweat.domain.store.StoreCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
public class StoreUpdateResponseDto {

    private final Long id;
    private final String name;
    private final String address;
    private final StoreCategory storeCategory;
    private final Long minOrderPrice;
    private final LocalTime openTime;
    private final LocalTime closedTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime updatedAt;

    @Builder
    public StoreUpdateResponseDto(Long id, String name, String address, StoreCategory storeCategory, Long minOrderPrice, LocalTime openTime, LocalTime closedTime, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.storeCategory = storeCategory;
        this.minOrderPrice = minOrderPrice;
        this.openTime = openTime;
        this.closedTime = closedTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}