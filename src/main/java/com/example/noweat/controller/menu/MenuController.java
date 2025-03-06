package com.example.noweat.controller.menu;

import com.example.noweat.dto.menu.request.MenuSaveRequestDto;
import com.example.noweat.dto.menu.request.MenuUpdateRequestDto;
import com.example.noweat.dto.menu.response.MenuResponseDto;
import com.example.noweat.dto.menu.response.MenuSaveResponseDto;
import com.example.noweat.dto.menu.response.MenuUpdateResponseDto;
import com.example.noweat.global.argumentResolver.AuthUser;
import com.example.noweat.service.menu.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @PostMapping("stores/{storeId}/menus")
    public ResponseEntity<MenuSaveResponseDto> saveMenu(
            AuthUser authUser,
            @PathVariable("storeId") Long storeId,
            @Valid @RequestBody MenuSaveRequestDto request
    ) {
        return ResponseEntity.ok(menuService.saveMenu(authUser, storeId, request));
    }

    @GetMapping("stores/{storeId}/menus")
    public ResponseEntity<List<MenuResponseDto>> findAllMenu(
            @PathVariable("storeId") Long storeId
    ) {
        return ResponseEntity.ok(menuService.findAllMenu(storeId));
    }

    @PutMapping("menus/{menuId}")
    public ResponseEntity<MenuUpdateResponseDto> updateMenu(
            AuthUser authUser,
            @PathVariable("menuId") Long menuId,
            @Valid @RequestBody MenuUpdateRequestDto request
    ) {
        return ResponseEntity.ok(menuService.updateMenu(authUser, menuId, request));
    }

    @DeleteMapping("menus/{menuId}")
    public void deleteMenu(
            AuthUser authUser,
            @PathVariable("menuId") Long menuId
    ) {
        menuService.deleteMenu(authUser, menuId);
    }
}
