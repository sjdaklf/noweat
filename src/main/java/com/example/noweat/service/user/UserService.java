package com.example.noweat.service.user;

import com.example.noweat.domain.review.Review;
import com.example.noweat.domain.store.Store;
import com.example.noweat.domain.user.User;
import com.example.noweat.domain.user.UserRole;
import com.example.noweat.dto.user.request.UserUpdatePasswordRequestDto;
import com.example.noweat.dto.user.request.UserDeleteRequestDto;
import com.example.noweat.dto.user.request.UserUpdateNameAndAddressRequestDto;
import com.example.noweat.dto.user.response.*;
import com.example.noweat.global.argumentResolver.AuthUser;
import com.example.noweat.global.config.PasswordEncoder;
import com.example.noweat.repository.review.ReviewRepository;
import com.example.noweat.repository.store.StoreRepository;
import com.example.noweat.repository.user.UserRepository;
import com.example.noweat.service.exception.ConflictException;
import com.example.noweat.service.exception.NotFoundException;
import com.example.noweat.service.exception.UnauthorizedException;
import com.example.noweat.service.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public UserResponseDto getUser(AuthUser authUser) {

        User findUser = userRepository.findById(authUser.getId()).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));

        // 유저 삭제 됐는지 확인하는 함수
        // 어떻게 하면 좋을까요? 필터?
        verifyUser(findUser);

        return UserResponseDto.builder()
                .id(findUser.getId())
                .username(findUser.getUsername())
                .userAddress(findUser.getUserAddress())
                .userRole(findUser.getUserRole())
                .storeCount(findUser.getStoreCount())
                .createdAt(findUser.getCreatedAt())
                .updatedAt(findUser.getUpdatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public UserStoreResponseDto getStoreByUser(AuthUser authUser) {

        if (authUser.getUserRole() != UserRole.OWNER) {
            throw new UnauthorizedException(ErrorCode.NOT_OWNER);
        }

        User findUser = userRepository.findById(authUser.getId()).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));

        verifyUser(findUser);

        Store findStore = storeRepository.findById(authUser.getId()).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_STORE));

        return UserStoreResponseDto.builder()
                .id(findUser.getId())
                .storeName(findStore.getStoreName())
                .storeCategory(findStore.getStoreCategory())
                .minOrderPrice(findStore.getMinOrderPrice())
                .averageRating(findStore.getAverageRating())
                .build();
    }

    @Transactional(readOnly = true)
    public UserReviewResponseDto getReviewByUser(AuthUser authUser) {

        if (authUser.getUserRole() != UserRole.USER) {
            throw new UnauthorizedException(ErrorCode.NOT_USER);
        }

        User findUser = userRepository.findById(authUser.getId()).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));

        verifyUser(findUser);

        Review findReview = reviewRepository.findById(authUser.getId()).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_REVIEW));

        return UserReviewResponseDto.builder()
                .id(findUser.getId())
                .title(findReview.getTitle())
                .content(findReview.getContent())
                .starRating(findReview.getStarRating())
                .createdAt(findReview.getCreatedAt())
                .updatedAt(findReview.getUpdatedAt())
                .build();
    }

    @Transactional
    public UserUpdateNameAndAddressResponseDto updateUserNameAndAddress(AuthUser authUser, UserUpdateNameAndAddressRequestDto userUpdateNameAndAddressRequestDto) {

        User findUser = userRepository.findById(authUser.getId()).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));

        verifyUser(findUser);

        findUser.updateUserNameAndAddress(userUpdateNameAndAddressRequestDto.getUsername(), userUpdateNameAndAddressRequestDto.getUserAddress());

        return UserUpdateNameAndAddressResponseDto.builder()
                .id(findUser.getId())
                .username(findUser.getUsername())
                .userAddress(findUser.getUserAddress())
                .userRole(findUser.getUserRole())
                .storeCount(findUser.getStoreCount())
                .createdAt(findUser.getCreatedAt())
                .updatedAt(findUser.getUpdatedAt())
                .build();
    }

    @Transactional
    public UserUpdatePasswordResponseDto updateUserPassword(AuthUser authUser, UserUpdatePasswordRequestDto userUpdatePasswordRequestDto) {

        User findUser = userRepository.findById(authUser.getId()).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));

        verifyUser(findUser);

        if (passwordEncoder.matches(userUpdatePasswordRequestDto.getNewPassword(), findUser.getPassword())) {
            throw new ConflictException(ErrorCode.SAME_AS_PREVIOUS_PASSWORD);
        }

        if (!passwordEncoder.matches(userUpdatePasswordRequestDto.getOldPassword(), findUser.getPassword())) {
            throw new UnauthorizedException(ErrorCode.INVALID_PASSWORD);
        }

        findUser.updatePassword(passwordEncoder.encode(userUpdatePasswordRequestDto.getNewPassword()));

        return UserUpdatePasswordResponseDto.builder()
                .id(findUser.getId())
                .username(findUser.getUsername())
                .userAddress(findUser.getUserAddress())
                .userRole(findUser.getUserRole())
                .storeCount(findUser.getStoreCount())
                .createdAt(findUser.getCreatedAt())
                .updatedAt(findUser.getUpdatedAt())
                .build();
    }

    @Transactional
    public void deleteUser(AuthUser authUser, UserDeleteRequestDto userDeleteRequestDto) {

        User findUser = userRepository.findById(authUser.getId()).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));

        verifyUser(findUser);

        if (!passwordEncoder.matches(userDeleteRequestDto.getPassword(), findUser.getPassword())) {
            throw new UnauthorizedException(ErrorCode.INVALID_PASSWORD);
        }

        findUser.deleteUser(true);
    }

    public void verifyUser(User findUser) {
        if (findUser.isDeleted()) {
            throw new UnauthorizedException(ErrorCode.DELETED_USER);
        }
    }
}