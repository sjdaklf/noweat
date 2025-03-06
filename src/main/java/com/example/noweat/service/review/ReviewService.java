package com.example.noweat.service.review;

import com.example.noweat.domain.order.Order;
import com.example.noweat.domain.order.OrderStatus;
import com.example.noweat.domain.review.Review;
import com.example.noweat.domain.review.StarRating;
import com.example.noweat.domain.store.Store;
import com.example.noweat.domain.user.User;
import com.example.noweat.domain.user.UserRole;
import com.example.noweat.dto.review.request.ReviewCreateRequestDto;
import com.example.noweat.dto.review.request.ReviewUpdateRequestDto;
import com.example.noweat.dto.review.response.ReviewCreateResponseDto;
import com.example.noweat.dto.review.response.ReviewListResponseDto;
import com.example.noweat.dto.review.response.ReviewUpdateResponseDto;
import com.example.noweat.global.argumentResolver.AuthUser;
import com.example.noweat.repository.order.OrderRepository;
import com.example.noweat.repository.review.ReviewRepository;
import com.example.noweat.repository.store.StoreRepository;
import com.example.noweat.repository.user.UserRepository;
import com.example.noweat.service.exception.*;
import com.example.noweat.service.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public ReviewCreateResponseDto createReview(AuthUser authUser, Long orderId, ReviewCreateRequestDto reviewCreateRequestDto){

        LocalDateTime requestTime = LocalDateTime.now();

        // ONE, TWO, THREE, FOUR, FIVE 이외라면 예외가 발생
        StarRating starRating = StarRating.of(reviewCreateRequestDto.getStarRating());

        // USER만이 리뷰를 작성 가능
        if(authUser.getUserRole() != UserRole.USER){
            throw new ForbiddenException(ErrorCode.NOT_USER);
        }

        // 유저가 존재하는지, 유저가 삭제된 유저인지 판단
        User findUser = userRepository.findById(authUser.getId()).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
        verifyUser(findUser);

        // 존재하는 주문인지 확인
        Order findOrder = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException(ErrorCode.ORDER_NOT_EXIST));

        // 현재 유저의 주문인지 확인
        if(authUser.getId() != findOrder.getUser().getId()){
            throw new ForbiddenException(ErrorCode.NOT_USERS_ORDER);
        }

        // 리뷰가 존재하면 작성할 수 없음
        if(reviewRepository.existsByOrder_Id(findOrder.getId())){
            throw new ConflictException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        // 주문의 상태가 COMPLETED 일 때 리뷰 작성이 가능함
        if(findOrder.getOrderStatus() != OrderStatus.COMPLETED){
            throw new BadRequestException(ErrorCode.REVIEW_NOT_ALLOWED);
        }

        // 주문이 완료된지 일주일이 지나면 리뷰를 작성 불가
        LocalDateTime oneWeekBefore = requestTime.minusWeeks(1);
        if(findOrder.getUpdatedAt().isBefore(oneWeekBefore)){
            throw new BadRequestException(ErrorCode.REVIEW_PERIOD_EXPIRED);
        }

        Review review = Review.builder()
                .user(findUser)
                .store(findOrder.getStore())
                .order(findOrder)
                .title(reviewCreateRequestDto.getTitle())
                .content(reviewCreateRequestDto.getContent())
                .starRating(starRating)
                .build();

        Review savedReview = reviewRepository.save(review);

        Store store = findOrder.getStore();
        store.addRatingSum((long) (savedReview.getStarRating().ordinal() + 1));
        store.addReviewCount();
        store.calculateAverageRating();

        return ReviewCreateResponseDto.builder()
                .id(savedReview.getId())
                .title(savedReview.getTitle())
                .content(savedReview.getContent())
                .starRating(savedReview.getStarRating())
                .createAt(savedReview.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ReviewListResponseDto> findAllReviews(Long storeId, Integer minRating, Integer maxRating){

        Store findStore = storeRepository.findById(storeId).orElseThrow(() -> new NotFoundException(ErrorCode.STORE_NOT_EXIST));
        verifyStore(findStore);

        List<Review> reviews;

        // 최대와 최소 별점 모두 null 아닐때만 동작
        if(minRating != null && maxRating != null){
            // 최소와 최대별점 둘 다 범위를 만족하는지 검사
            if (!validateMinRating(minRating) || !validateMaxRating(maxRating)) {
                throw new BadRequestException(ErrorCode.INVALID_RATING_RANGE);
            }

            if(minRating > maxRating){
                throw new BadRequestException(ErrorCode.MIN_RATING_LARGER_THAN_MAX_RATING);
            }

            reviews = reviewRepository.findAllByMinRatingAndMaxRating(minRating, maxRating);

            return reviews.stream().map(review -> ReviewListResponseDto.builder()
                    .id(review.getId())
                    .title(review.getTitle())
                    .content(review.getContent())
                    .starRating(review.getStarRating())
                    .createAt(review.getCreatedAt())
                    .updatedAt(review.getUpdatedAt())
                    .build()).collect(Collectors.toList());
        }

        reviews = reviewRepository.findAllByStore_IdOrderByCreatedAtDesc(storeId);

        return reviews.stream().map(review -> ReviewListResponseDto.builder()
                .id(review.getId())
                .title(review.getTitle())
                .content(review.getContent())
                .starRating(review.getStarRating())
                .createAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build()).collect(Collectors.toList());
    }

    public ReviewUpdateResponseDto updateReview(AuthUser authUser, Long reviewId, ReviewUpdateRequestDto reviewUpdateRequestDto){

        LocalDateTime requestTime = LocalDateTime.now();

        // ONE, TWO, THREE, FOUR, FIVE 이외라면 예외가 발생
        StarRating updateStarRating = StarRating.of(reviewUpdateRequestDto.getStarRating());

        // USER만이 리뷰를 수정 가능
        if(authUser.getUserRole() != UserRole.USER){
            throw new ForbiddenException(ErrorCode.NOT_USER);
        }

        // 유저가 존재하는지, 유저가 삭제된 유저인지 판단
        User findUser = userRepository.findById(authUser.getId()).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
        verifyUser(findUser);

        // 리뷰를 찾는다.
        Review findReview = reviewRepository.findById(reviewId).orElseThrow(() -> new NotFoundException(ErrorCode.REVIEW_NOT_EXIST));

        // 찾은 리뷰가 요청의 사용자의 것인지 검증
        if(authUser.getId() != findReview.getUser().getId()){
            throw new ForbiddenException(ErrorCode.NOT_USERS_REVIEW);
        }

        // 리뷰가 작성된지 일주일이 지나면 리뷰를 작성 불가
        LocalDateTime oneWeekBefore = requestTime.minusWeeks(1);
        if(findReview.getCreatedAt().isBefore(oneWeekBefore)){
            throw new BadRequestException(ErrorCode.REVIEW_UPDATE_PERIOD_EXPIRED);
        }

        Review savedReview = updateStoreAndReview(reviewUpdateRequestDto, findReview, updateStarRating);

        return ReviewUpdateResponseDto.builder()
                .id(savedReview.getId())
                .title(savedReview.getTitle())
                .content(savedReview.getContent())
                .starRating(savedReview.getStarRating())
                .createAt(savedReview.getCreatedAt())
                .updatedAt(savedReview.getUpdatedAt())
                .build();
    }

    @Transactional
    public Review updateStoreAndReview(ReviewUpdateRequestDto reviewUpdateRequestDto, Review findReview, StarRating updateStarRating) {
        Store findStore = findReview.getStore();
        findStore.minusRatingSum((long)(findReview.getStarRating().ordinal() + 1));
        findStore.addRatingSum((long)(updateStarRating.ordinal() + 1));
        findStore.calculateAverageRating();
        storeRepository.save(findStore);

        findReview.updateTitle(reviewUpdateRequestDto.getTitle());
        findReview.updateContent(reviewUpdateRequestDto.getContent());
        findReview.updateStarRating(updateStarRating);
        return reviewRepository.save(findReview);
    }

    public void verifyUser(User findUser) {
        if (findUser.isDeleted()) {
            throw new UnauthorizedException(ErrorCode.USER_ALREADY_DELETED);
        }
    }

    public void verifyStore(Store findStore) {
        if (findStore.isClosed()) {
            throw new GoneException(ErrorCode.STORE_CLOSED);
        }
    }

    private boolean validateMinRating(Integer minRating){
        if(minRating < 1 || minRating > 5){
            return false;
        }
        return true;
    }

    private boolean validateMaxRating(Integer maxRating){
        if(maxRating < 1 || maxRating > 5){
            return false;
        }
        return true;
    }
}
