package com.example.noweat.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserSignupRequestDto {
    @NotBlank(message = "이메일은 필수 값 입니다.")
    @Email(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$", message = "올바른 이메일 형식을 입력해 주세요.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 값 입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8글자 이상입니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).*$", message = "대소문자 포함 영문+숫자+특수문자를 최소 1글자씩 포함해야 됩니다.")
    private String password;

    @NotBlank(message = "유저 주소는 필수 값 입니다.")
    private String userAddress;

    @NotBlank(message = "유저이름은 필수 값 입니다.")
    private String username;

    @NotBlank(message = "유저 역할은 필수 값 입니다.")
    private String userRole;
}
