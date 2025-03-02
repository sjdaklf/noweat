package com.example.noweat.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RefreshTokenRequestDto {
    @NotBlank(message = "RefreshToken 은 필수 값 입니다.")
    private String refreshToken;
}
