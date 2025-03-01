package com.example.noweat.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserSingoutRequestDto {
    @NotBlank(message = "DeviceId는 필수 값 입니다.")
    private String deviceId;
}
