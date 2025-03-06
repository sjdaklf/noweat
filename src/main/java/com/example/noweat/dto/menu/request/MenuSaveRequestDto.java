package com.example.noweat.dto.menu.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MenuSaveRequestDto {

    @NotBlank(message = "메뉴 이름은 필수 값 입니다.")
    private String name;

    @NotNull(message = "메뉴 가격은 필수 값 입니다.")
    private Long price;
}