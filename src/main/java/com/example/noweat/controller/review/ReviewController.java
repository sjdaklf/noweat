package com.example.noweat.controller.review;

import com.example.noweat.dto.review.request.ReviewCreateRequestDto;
import com.example.noweat.dto.review.response.ReviewCreateResponseDto;
import com.example.noweat.global.argumentResolver.AuthUser;
import com.example.noweat.service.review.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/api/orders/{orderId}/reviews")
    public ResponseEntity<ReviewCreateResponseDto> createReview(AuthUser authUser, @PathVariable("orderId") Long orderId, @Valid @RequestBody ReviewCreateRequestDto reviewCreateRequestDto){
        ReviewCreateResponseDto reviewCreateResponseDto = reviewService.createReview(authUser, orderId, reviewCreateRequestDto);
        return new ResponseEntity<>(reviewCreateResponseDto, HttpStatus.OK);
    }
}
