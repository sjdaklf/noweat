package com.example.noweat.domain.store;

import com.sun.jdi.request.InvalidRequestStateException;

import java.util.Arrays;

public enum StoreCategory {
    KOREAN,       // 한식
    CHINESE,      // 중식
    JAPANESE,     // 일식
    WESTERN;

    public static StoreCategory of(String storeCategory) {
        return Arrays.stream(StoreCategory.values())
                .filter(r -> r.name().equalsIgnoreCase(storeCategory))
                .findFirst()
                .orElseThrow(() -> new InvalidRequestStateException("유효하지 않은 StoreCategory"));
    }
}
