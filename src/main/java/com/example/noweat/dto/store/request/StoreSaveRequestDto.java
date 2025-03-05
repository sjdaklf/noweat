package com.example.noweat.dto.store.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalTime;

@Getter
public class StoreSaveRequestDto {

    @NotBlank(message = "가게 이름은 필수 값 입니다.")
    private String name;

    @NotBlank(message = "가게 주소는 필수 값 입니다.")
    private String address;

    @NotBlank(message = "가게 카테고리는 필수 값 입니다.")
    private String storeCategory;

    @NotNull(message = "가게 최소 주문 금액은 필수 값 입니다.")
    private Long minOrderPrice;

    @NotNull(message = "가게 오픈 시간은 필수 값 입니다.")
    private LocalTime openTime;

    @NotNull(message = "가게 마감 시간은 필수 값 입니다.")
    private LocalTime closedTime;
}