package com.example.noweat.controller.auth;

import com.example.noweat.dto.auth.reponse.UserSingupResponseDto;
import com.example.noweat.dto.auth.request.UserSingupRequestDto;
import com.example.noweat.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserSingupResponseDto> signupUser(@Valid @RequestBody UserSingupRequestDto userSingupRequestDto){
        UserSingupResponseDto userSingupResponseDto = authService.signupUser(userSingupRequestDto);
        return new ResponseEntity<>(userSingupResponseDto, HttpStatus.OK);
    }
}
