package com.example.noweat.dto.auth.reponse;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserSigninResponseDto {
    private final String accessToken;
    private final String refreshToken;

    @Builder
    public UserSigninResponseDto(String accessToken, String refreshToken){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
