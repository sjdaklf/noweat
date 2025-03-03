package com.example.noweat.controller.user;

import com.example.noweat.dto.user.request.UserUpdatePasswordRequestDto;
import com.example.noweat.dto.user.request.UserDeleteRequestDto;
import com.example.noweat.dto.user.request.UserUpdateNameAndAddressRequestDto;
import com.example.noweat.dto.user.response.*;
import com.example.noweat.global.argumentResolver.AuthUser;
import com.example.noweat.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<UserResponseDto> getUser(AuthUser authUser) {
        UserResponseDto userResponseDto = userService.getUser(authUser);
        return ResponseEntity.ok(userResponseDto);
    }

    @GetMapping("/users/stores")
    public ResponseEntity<UserStoreResponseDto> getStoreByUser(AuthUser authUser) {
        UserStoreResponseDto userStoreResponseDto = userService.getStoreByUser(authUser);
        return ResponseEntity.ok(userStoreResponseDto);
    }

    @GetMapping("/users/reviews")
    public ResponseEntity<UserReviewResponseDto> getReviewByUser(AuthUser authUser) {
        UserReviewResponseDto userReviewResponseDto = userService.getReviewByUser(authUser);
        return ResponseEntity.ok(userReviewResponseDto);
    }

    @PatchMapping("/users")
    public ResponseEntity<UserUpdateNameAndAddressResponseDto> updateUserNameAndAddress(
            AuthUser authUser,
            @Valid @RequestBody UserUpdateNameAndAddressRequestDto userUpdateNameAndAddressRequestDto
    ) {
        UserUpdateNameAndAddressResponseDto userUpdateNameAndAddressResponseDto = userService.updateUserNameAndAddress(authUser, userUpdateNameAndAddressRequestDto);
        return ResponseEntity.ok(userUpdateNameAndAddressResponseDto);
    }

    @PatchMapping("/users/password")
    public ResponseEntity<UserUpdatePasswordResponseDto> updateUserPassword(
            AuthUser authUser,
            @Valid @RequestBody UserUpdatePasswordRequestDto userUpdatePasswordRequestDto
    ) {
        UserUpdatePasswordResponseDto userUpdatePasswordResponseDto = userService.updateUserPassword(authUser, userUpdatePasswordRequestDto);
        return ResponseEntity.ok(userUpdatePasswordResponseDto);
    }

    @PostMapping("/users")
    public void deleteUser(
            AuthUser authUser,
            @RequestBody UserDeleteRequestDto userDeleteRequestDto
    ) {
        userService.deleteUser(authUser, userDeleteRequestDto);
    }
}
