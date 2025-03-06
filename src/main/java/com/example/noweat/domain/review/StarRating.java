package com.example.noweat.domain.review;


import com.example.noweat.service.exception.BadRequestException;
import com.example.noweat.service.exception.enums.ErrorCode;
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
                .orElseThrow(() -> new BadRequestException(ErrorCode.INVALID_STAR_RATING));
    }
}
