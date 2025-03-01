package com.example.noweat.dto.menu.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MenuSaveRequest {

    private String menuName;
    private Long menuPrice;
}