package com.example.noweat.domain.store;

import com.example.noweat.service.exception.BadRequestException;
import com.example.noweat.service.exception.enums.ErrorCode;
import com.sun.jdi.request.InvalidRequestStateException;

import java.util.Arrays;

public enum StoreCategory {
    KOREAN,       // 한식
    CHINESE,      // 중식
    JAPANESE,     // 일식
    WESTERN;      // 양식

    public static StoreCategory of(String storeCategory) {
        return Arrays.stream(StoreCategory.values())
                .filter(r -> r.name().equalsIgnoreCase(storeCategory))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(ErrorCode.INVALID_STORE_CATEGORY));
    }
}