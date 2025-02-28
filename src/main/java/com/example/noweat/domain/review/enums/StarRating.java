package com.example.noweat.domain.review.enums;


import com.sun.jdi.request.InvalidRequestStateException;

import java.util.Arrays;

public enum StarRating {
    ONE,
    TWO,
    THREE,
    FOUR,
    FIVE;

    public static StarRating of(String starRating) {
        return Arrays.stream(StarRating.values())
                .filter(r -> r.name().equalsIgnoreCase(starRating))
                .findFirst()
                .orElseThrow(() -> new InvalidRequestStateException("유효하지 않은 starRating 입니다."));
    }
}
