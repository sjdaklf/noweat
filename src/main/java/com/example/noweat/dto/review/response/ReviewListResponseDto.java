package com.example.noweat.dto.review.response;

import com.example.noweat.domain.review.StarRating;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReviewListResponseDto {
    private final Long id;

    private final String title;

    private final String content;

    private final StarRating starRating;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime updatedAt;

    @Builder
    public ReviewListResponseDto(Long id, String title, String content, StarRating starRating, LocalDateTime createAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.starRating = starRating;
        this.createAt = createAt;
        this.updatedAt = updatedAt;
    }
}
