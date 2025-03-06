package com.example.noweat.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserUpdateNameAndAddressRequestDto {
    @NotBlank(message = "사용자 이름은 필수 값 입니다.")
    private String name;

    @NotBlank(message = "사용자 주소는 필수 값 입니다.")
    private String address;
}
