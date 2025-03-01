package com.example.noweat.dto.menu.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MenuSaveResponse {

    private final Long id;
    private final String menuName;
    private final Long menuPrice;
    private final LocalDateTime createdAt;

    public MenuSaveResponse(Long id, String menuName, Long menuPrice, LocalDateTime createdAt) {
        this.id = id;
        this.menuName = menuName;
        this.menuPrice = menuPrice;
        this.createdAt = createdAt;
    }
}
