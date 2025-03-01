package com.example.noweat.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserSingupRequestDto {
    @NotBlank(message = "이메일은 필수 값 입니다.")
    @Email(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$", message = "올바른 이메일 형식을 입력해 주세요.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 값 입니다.")
    private String password;

    @NotBlank(message = "유저 주소는 필수 값 입니다.")
    private String userAddress;

    @NotBlank(message = "유저이름은 필수 값 입니다.")
    private String username;

    @NotBlank(message = "유저 역할은 필수 값 입니다.")
    private String userRole;
}
