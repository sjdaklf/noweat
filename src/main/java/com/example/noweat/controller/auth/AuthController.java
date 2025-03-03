package com.example.noweat.controller.auth;

import com.example.noweat.dto.auth.reponse.RefreshTokenResponseDto;
import com.example.noweat.dto.auth.reponse.UserSigninResponseDto;
import com.example.noweat.dto.auth.reponse.UserSignupResponseDto;
import com.example.noweat.dto.auth.request.RefreshTokenRequestDto;
import com.example.noweat.dto.auth.request.UserSigninRequestDto;
import com.example.noweat.dto.auth.request.UserSignupRequestDto;
import com.example.noweat.dto.auth.request.UserSignoutRequestDto;
import com.example.noweat.global.argumentResolver.AuthUser;
import com.example.noweat.service.auth.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.ValidatorFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {
    private final AuthService authService;
    private final ValidatorFactory validatorFactory;

    @PostMapping("/signup")
    public ResponseEntity<UserSignupResponseDto> signupUser(@Valid @RequestBody UserSignupRequestDto userSignupRequestDto){
        UserSignupResponseDto userSignupResponseDto = authService.signupUser(userSignupRequestDto);
        return new ResponseEntity<>(userSignupResponseDto, HttpStatus.OK);
    }

    @PostMapping("/signin")
    public ResponseEntity<UserSigninResponseDto> signinUser(@Valid @RequestBody UserSigninRequestDto userSigninRequestDto){
        UserSigninResponseDto userSigninResponseDto = authService.signinUser(userSigninRequestDto);
        return new ResponseEntity<>(userSigninResponseDto, HttpStatus.OK);
    }

    @PostMapping("/signout")
    public void signoutUser(AuthUser authUser, @Valid @RequestBody UserSignoutRequestDto userSignoutRequestDto){
        authService.signout(authUser.getId(), userSignoutRequestDto.getDeviceId());
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponseDto> refreshToken(@Valid @RequestBody RefreshTokenRequestDto refreshTokenRequestDto){
        RefreshTokenResponseDto refreshTokenResponseDto = authService.refreshToken(refreshTokenRequestDto);
        return new ResponseEntity<>(refreshTokenResponseDto, HttpStatus.OK);
    }

}
