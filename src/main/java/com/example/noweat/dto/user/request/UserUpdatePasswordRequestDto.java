package com.example.noweat.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserUpdatePasswordRequestDto {

    @NotBlank(message = "기존 비밀번호는 필수 값 입니다.")
    private String oldPassword;

    @NotBlank(message = "새로운 비밀번호는 필수 값 입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8글자 이상입니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).*$", message = "대소문자 포함 영문+숫자+특수문자를 최소 1글자씩 포함해야 됩니다.")
    private String newPassword;
}
