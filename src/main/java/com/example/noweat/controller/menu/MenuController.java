package com.example.noweat.controller.menu;

import com.example.noweat.dto.menu.request.MenuSaveRequest;
import com.example.noweat.dto.menu.request.MenuUpdateRequest;
import com.example.noweat.dto.menu.response.MenuResponse;
import com.example.noweat.dto.menu.response.MenuSaveResponse;
import com.example.noweat.dto.menu.response.MenuUpdateResponse;
import com.example.noweat.service.menu.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @PostMapping("/api/stores/{storeId}/menus")
    public ResponseEntity<MenuSaveResponse> saveMenu(
            @PathVariable Long storeId,
            @RequestBody MenuSaveRequest request
    ) {
        return ResponseEntity.ok(menuService.saveMenu(storeId, request));
    }

    @GetMapping("/api/stores/{storeId}/menus")
    public ResponseEntity<List<MenuResponse>> findAllMenu(
            @PathVariable Long storeId
    ) {
        return ResponseEntity.ok(menuService.findAllMenu(storeId));
    }

    @PatchMapping("/api/menus/{menuId}")
    public ResponseEntity<MenuUpdateResponse> updateMenu(
            @PathVariable Long menuId,
            @RequestBody MenuUpdateRequest request
    ) {
        return ResponseEntity.ok(menuService.updateMenu(menuId, request));
    }

    @DeleteMapping("/api/menus/{menuId}")
    public void deleteMenu(@PathVariable Long menuId) {
        menuService.deleteMenu(menuId);
    }
}
