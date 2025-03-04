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

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<UserResponseDto> findUser(AuthUser authUser) {
        UserResponseDto userResponseDto = userService.findUser(authUser);
        return ResponseEntity.ok(userResponseDto);
    }

    @GetMapping("/users/stores")
    public ResponseEntity<List<UserStoreResponseDto>> findStoresByUserId(AuthUser authUser) {
        List<UserStoreResponseDto> userStoreResponseDto = userService.findStoresByUserId(authUser);
        return ResponseEntity.ok(userStoreResponseDto);
    }

    @GetMapping("/users/reviews")
    public ResponseEntity<List<UserReviewResponseDto>> findReviewsByUserId(AuthUser authUser) {
        List<UserReviewResponseDto> userReviewResponseDto = userService.findReviewsByUserId(authUser);
        return ResponseEntity.ok(userReviewResponseDto);
    }

    @PatchMapping("/users")
    public ResponseEntity<UserResponseDto> updateUserNameAndAddress(
            AuthUser authUser,
            @Valid @RequestBody UserUpdateNameAndAddressRequestDto userUpdateNameAndAddressRequestDto
    ) {
        UserResponseDto userResponseDto = userService.updateUserNameAndAddress(authUser, userUpdateNameAndAddressRequestDto);
        return ResponseEntity.ok(userResponseDto);
    }

    @PatchMapping("/users/password")
    public ResponseEntity<UserResponseDto> updateUserPassword(
            AuthUser authUser,
            @Valid @RequestBody UserUpdatePasswordRequestDto userUpdatePasswordRequestDto
    ) {
        UserResponseDto userResponseDto = userService.updateUserPassword(authUser, userUpdatePasswordRequestDto);
        return ResponseEntity.ok(userResponseDto);
    }

    @PostMapping("/users")
    public void deleteUser(
            AuthUser authUser,
            @RequestBody UserDeleteRequestDto userDeleteRequestDto
    ) {
        userService.deleteUser(authUser, userDeleteRequestDto);
    }
}
