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
import com.example.noweat.service.exception.*;
import com.example.noweat.service.exception.ConflictException;
import com.example.noweat.service.exception.GoneException;
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
    public UserResponseDto findUser(AuthUser authUser, Long userId) {

        User findUser = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));

        verifyUser(findUser);

        // USER 는 다른 사용자(USER, OWNER) 의 정보에 접근 불가, 자신의 정보만 접근 가능
        if(authUser.getUserRole() == UserRole.USER && !authUser.getId().equals(userId)){
            throw new ForbiddenException(ErrorCode.NOT_OWNER);
        }

        // 유저는 자신의 정보만 조회 가능
        if(authUser.getUserRole() == UserRole.USER){
            return new UserFindResponseDto(
                    findUser.getId(),
                    findUser.getName(),
                    findUser.getAddress(),
                    findUser.getUserRole(),
                    findUser.getCreatedAt(),
                    findUser.getUpdatedAt()
            );
        }

        // OWNER 는 자신의 정보 또는 USER 의 정보에만 접근이 가능
        if(findUser.getUserRole() == UserRole.OWNER && authUser.getId() != findUser.getId()){
            throw new ForbiddenException(ErrorCode.NO_ACCESS_TO_OWNER_INFO);
        }

        if (findUser.getUserRole() != UserRole.OWNER) {

            return new UserFindResponseDto(
                    findUser.getId(),
                    findUser.getName(),
                    findUser.getAddress(),
                    findUser.getUserRole(),
                    findUser.getCreatedAt(),
                    findUser.getUpdatedAt()
            );
        }

        return new UserOwnerFindResponseDto(
                findUser.getId(),
                findUser.getName(),
                findUser.getAddress(),
                findUser.getUserRole(),
                findUser.getStoreCount(),
                findUser.getCreatedAt(),
                findUser.getUpdatedAt()
        );

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
                    store.getName(),
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

        findUser.updateUserNameAndAddress(userUpdateNameAndAddressRequestDto.getName(), userUpdateNameAndAddressRequestDto.getAddress());

        User savedUser = userRepository.save(findUser);

        if (savedUser.getUserRole() != UserRole.OWNER) {

            return new UserUpdateNameAndAddressResponseDto(
                    findUser.getId(),
                    findUser.getName(),
                    findUser.getAddress(),
                    findUser.getUserRole(),
                    findUser.getCreatedAt(),
                    findUser.getUpdatedAt()
            );

        }

        return new UserOwnerUpdateNameAndAddressResponseDto(
                findUser.getId(),
                findUser.getName(),
                findUser.getAddress(),
                findUser.getUserRole(),
                savedUser.getStoreCount(),
                findUser.getCreatedAt(),
                findUser.getUpdatedAt()
        );

    }

    public UserResponseDto updateUserPassword(AuthUser authUser, UserUpdatePasswordRequestDto userUpdatePasswordRequestDto) {

        User findUser = userRepository.findById(authUser.getId()).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));

        verifyUser(findUser);

        if (passwordEncoder.matches(userUpdatePasswordRequestDto.getNewPassword(), findUser.getPassword())) {
            throw new ConflictException(ErrorCode.SAME_AS_PREVIOUS_PASSWORD);
        }

        if (!passwordEncoder.matches(userUpdatePasswordRequestDto.getOldPassword(), findUser.getPassword())) {
            throw new BadRequestException(ErrorCode.INVALID_PASSWORD);
        }

        findUser.updatePassword(passwordEncoder.encode(userUpdatePasswordRequestDto.getNewPassword()));

        User savedUser = userRepository.save(findUser);

        if (savedUser.getUserRole() != UserRole.OWNER) {

            return new UserUpdatePasswordResponseDto(
                    findUser.getId(),
                    findUser.getName(),
                    findUser.getAddress(),
                    findUser.getUserRole(),
                    findUser.getCreatedAt(),
                    findUser.getUpdatedAt()
            );

        }

        return new UserOwnerUpdatePasswordResponseDto(
                findUser.getId(),
                findUser.getName(),
                findUser.getAddress(),
                findUser.getUserRole(),
                savedUser.getStoreCount(),
                findUser.getCreatedAt(),
                findUser.getUpdatedAt()
        );

    }

    @Transactional
    public void deleteUser(AuthUser authUser, UserDeleteRequestDto userDeleteRequestDto) {

        User findUser = userRepository.findById(authUser.getId()).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));

        verifyUser(findUser);

        if (!passwordEncoder.matches(userDeleteRequestDto.getPassword(), findUser.getPassword())) {
            throw new BadRequestException(ErrorCode.INVALID_PASSWORD);
        }

        findUser.deleteUser(true);
    }

    public void verifyUser(User findUser) {
        if (findUser.isDeleted()) {
            throw new ForbiddenException(ErrorCode.USER_ALREADY_DELETED);
        }
    }
}