package com.example.noweat.dto.user.response;

import com.example.noweat.domain.review.StarRating;
import com.example.noweat.domain.store.StoreCategory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserReviewResponseDto {
    private final Long id;
    private final String title;
    private final String content;
    private final StarRating starRating;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @Builder
    public UserReviewResponseDto(Long id, String title, String content, StarRating starRating, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.starRating = starRating;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
