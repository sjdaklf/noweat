package com.example.noweat.dto.menu.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MenuResponseDto {

    private final Long id;

    private final String name;

    private final Long price;

    @Builder
    public MenuResponseDto(Long id, String name, Long price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
}
