package com.example.noweat.dto.menu.response;

import lombok.Getter;

@Getter
public class MenuResponse {

    private final Long id;
    private final String menuName;
    private final Long menuPrice;

    public MenuResponse(Long id, String menuName, Long menuPrice) {
        this.id = id;
        this.menuName = menuName;
        this.menuPrice = menuPrice;
    }
}
