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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public UserResponseDto findUser(AuthUser authUser) {

        User findUser = userRepository.findById(authUser.getId()).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));

        verifyUser(findUser);

        if (findUser.getUserRole() != UserRole.OWNER) {
            return UserFindResponseDto.builder()
                    .id(findUser.getId())
                    .username(findUser.getUsername())
                    .userAddress(findUser.getUserAddress())
                    .userRole(findUser.getUserRole())
                    .createdAt(findUser.getCreatedAt())
                    .updatedAt(findUser.getUpdatedAt())
                    .build();
        }

        return UserOwnerFindResponseDto.builder()
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
    public List<UserStoreResponseDto> findStoresByUserId(AuthUser authUser) {

        if (authUser.getUserRole() != UserRole.OWNER) {
            throw new UnauthorizedException(ErrorCode.NOT_OWNER);
        }

        User findUser = userRepository.findById(authUser.getId()).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));

        verifyUser(findUser);

        List<Store> findStores = storeRepository.findStoresByUserId(findUser.getId());

        List<UserStoreResponseDto> storeList = new ArrayList<>();
        for (Store store : findStores) {
            UserStoreResponseDto userStoreResponseDto = new UserStoreResponseDto(
                    store.getId(),
                    store.getStoreName(),
                    store.getStoreCategory(),
                    store.getMinOrderPrice(),
                    store.getAverageRating()
                    );

            storeList.add(userStoreResponseDto);
        }

        return storeList;
    }

    @Transactional(readOnly = true)
    public List<UserReviewResponseDto> findReviewsByUserId(AuthUser authUser) {

        if (authUser.getUserRole() != UserRole.USER) {
            throw new UnauthorizedException(ErrorCode.NOT_USER);
        }

        User findUser = userRepository.findById(authUser.getId()).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));

        verifyUser(findUser);

        List<Review> findReviews = reviewRepository.findReviewsByUserId(findUser.getId());

        List<UserReviewResponseDto> reviewList = new ArrayList<>();
        for (Review review : findReviews) {
            UserReviewResponseDto userReviewResponseDto = new UserReviewResponseDto(
                    review.getId(),
                    review.getTitle(),
                    review.getContent(),
                    review.getStarRating(),
                    review.getCreatedAt(),
                    review.getUpdatedAt()
            );

            reviewList.add(userReviewResponseDto);
        }

        return reviewList;
    }

    public UserResponseDto updateUserNameAndAddress(AuthUser authUser, UserUpdateNameAndAddressRequestDto userUpdateNameAndAddressRequestDto) {

        User findUser = userRepository.findById(authUser.getId()).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));

        verifyUser(findUser);

        findUser.updateUserNameAndAddress(userUpdateNameAndAddressRequestDto.getUsername(), userUpdateNameAndAddressRequestDto.getUserAddress());

        User savedUser = userRepository.save(findUser);

        if (savedUser.getUserRole() != UserRole.OWNER) {
            return UserUpdateNameAndAddressResponseDto.builder()
                    .id(savedUser.getId())
                    .username(savedUser.getUsername())
                    .userAddress(savedUser.getUserAddress())
                    .userRole(savedUser.getUserRole())
                    .createdAt(savedUser.getCreatedAt())
                    .updatedAt(savedUser.getUpdatedAt())
                    .build();
        }

        return UserOwnerUpdateNameAndAddressResponseDto.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .userAddress(savedUser.getUserAddress())
                .userRole(savedUser.getUserRole())
                .storeCount(savedUser.getStoreCount())
                .createdAt(savedUser.getCreatedAt())
                .updatedAt(savedUser.getUpdatedAt())
                .build();
    }

    public UserResponseDto updateUserPassword(AuthUser authUser, UserUpdatePasswordRequestDto userUpdatePasswordRequestDto) {

        User findUser = userRepository.findById(authUser.getId()).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));

        verifyUser(findUser);

        if (passwordEncoder.matches(userUpdatePasswordRequestDto.getNewPassword(), findUser.getPassword())) {
            throw new ConflictException(ErrorCode.SAME_AS_PREVIOUS_PASSWORD);
        }

        if (!passwordEncoder.matches(userUpdatePasswordRequestDto.getOldPassword(), findUser.getPassword())) {
            throw new UnauthorizedException(ErrorCode.INVALID_PASSWORD);
        }

        findUser.updatePassword(passwordEncoder.encode(userUpdatePasswordRequestDto.getNewPassword()));

        User savedUser = userRepository.save(findUser);

        if (savedUser.getUserRole() != UserRole.OWNER) {
            return UserUpdatePasswordResponseDto.builder()
                    .id(savedUser.getId())
                    .username(savedUser.getUsername())
                    .userAddress(savedUser.getUserAddress())
                    .userRole(savedUser.getUserRole())
                    .createdAt(savedUser.getCreatedAt())
                    .updatedAt(savedUser.getUpdatedAt())
                    .build();
        }

        return UserOwnerUpdatePasswordResponseDto.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .userAddress(savedUser.getUserAddress())
                .userRole(savedUser.getUserRole())
                .storeCount(savedUser.getStoreCount())
                .createdAt(savedUser.getCreatedAt())
                .updatedAt(savedUser.getUpdatedAt())
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