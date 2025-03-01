package com.example.noweat.dto.menu.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MenuUpdateResponse {

    private final String menuName;
    private final Long menuPrice;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public MenuUpdateResponse(String menuName, Long menuPrice, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.menuName = menuName;
        this.menuPrice = menuPrice;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
