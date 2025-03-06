package com.example.noweat.dto.review.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReviewCreateRequestDto {
    @NotBlank(message = "리뷰 제목은 필수 입력 값 입니다.")
    private String title;

    @NotBlank(message = "리뷰 내용은 필수 입력 값 입니다.")
    private String content;

    @NotBlank(message = "별점은 필수 입력 값 입니다.")
    private String starRating;

}
