package com.example.noweat.controller.review;

import com.example.noweat.dto.review.request.ReviewCreateRequestDto;
import com.example.noweat.dto.review.response.ReviewCreateResponseDto;
import com.example.noweat.dto.review.response.ReviewListResponseDto;
import com.example.noweat.global.argumentResolver.AuthUser;
import com.example.noweat.service.review.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/orders/{orderId}/reviews")
    public ResponseEntity<ReviewCreateResponseDto> createReview(AuthUser authUser, @PathVariable("orderId") Long orderId, @Valid @RequestBody ReviewCreateRequestDto reviewCreateRequestDto){
        ReviewCreateResponseDto reviewCreateResponseDto = reviewService.createReview(authUser, orderId, reviewCreateRequestDto);
        return new ResponseEntity<>(reviewCreateResponseDto, HttpStatus.OK);
    }

    @GetMapping("/stores/{storeId}/reviews")
    public ResponseEntity<List<ReviewListResponseDto>> findAllReviews(@PathVariable("storeId") Long storeId, @RequestParam(value = "minRating", required = false) Long minRating,
                                                                      @RequestParam(value = "maxRating", required = false) Long maxRating){
        List<ReviewListResponseDto> reviewListResponseDtos = reviewService.findAllReviews(storeId, minRating, maxRating);
        return new ResponseEntity<>(reviewListResponseDtos, HttpStatus.OK);
    }
}
