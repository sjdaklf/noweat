package com.example.noweat.dto.auth.reponse;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RefreshTokenResponseDto {
    private final String accessToken;
    private final String refreshToken;

    @Builder
    public RefreshTokenResponseDto(String accessToken, String refreshToken){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
