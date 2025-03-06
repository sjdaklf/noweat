package com.example.noweat.dto.review.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ReviewListRequestDto {
    @NotNull(message = "최소 별점은 필수 입력 값 입니다.")
    @Size(min = 1, max = 5, message = "별점은 1~5점 사이입니다.")
    private Long minRating;

    @NotNull(message = "최대 별점은 필수 입력 값 입니다.")
    @Size(min = 1, max = 5, message = "별점은 1~5점 사이입니다.")
    private Long maxRating;

}
