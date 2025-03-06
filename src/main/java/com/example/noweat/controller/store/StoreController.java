package com.example.noweat.controller.store;

import com.example.noweat.dto.store.request.StoreSaveRequestDto;
import com.example.noweat.dto.store.request.StoreUpdateRequestDto;
import com.example.noweat.dto.store.response.StoreFindAllResponseDto;
import com.example.noweat.dto.store.response.StoreFindOneResponseDto;
import com.example.noweat.dto.store.response.StoreSaveResponseDto;
import com.example.noweat.dto.store.response.StoreUpdateResponseDto;
import com.example.noweat.global.argumentResolver.AuthUser;
import com.example.noweat.service.store.StoreService;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StoreController {

    private final StoreService storeService;

    @PostMapping("/stores")
    public ResponseEntity<StoreSaveResponseDto> saveStore(
            AuthUser authUser,
            @Valid @RequestBody StoreSaveRequestDto storeSaveRequestDto
    ) {
        StoreSaveResponseDto storeSaveResponseDto = storeService.saveStore(authUser, storeSaveRequestDto);
        return new ResponseEntity<>(storeSaveResponseDto, HttpStatus.OK);
    }

    @GetMapping("/stores")
    public ResponseEntity<List<StoreFindAllResponseDto>> findAllStore(
            @RequestParam(required = false) String name
    ) {
        List<StoreFindAllResponseDto> StoreFindAllResponseDto = storeService.findAllStore(name);
        return new ResponseEntity<>(StoreFindAllResponseDto, HttpStatus.OK);
    }

    @GetMapping("/stores/{storeId}")
    public ResponseEntity<StoreFindOneResponseDto> findOneStore(@PathVariable("storeId") Long storeId) {
        StoreFindOneResponseDto StoreFindOneResponseDto = storeService.findOneStore(storeId);
        return new ResponseEntity<>(StoreFindOneResponseDto, HttpStatus.OK);
    }

    @PatchMapping("/stores/{storeId}")
    public ResponseEntity<StoreUpdateResponseDto> updateStore(
            AuthUser authUser,
            @PathVariable("storeId") Long storeId,
            @Valid @RequestBody StoreUpdateRequestDto storeUpdateRequestDto
    ) {
        StoreUpdateResponseDto storeUpdateResponseDto = storeService.updateStore(authUser, storeId, storeUpdateRequestDto);
        return new ResponseEntity<>(storeUpdateResponseDto, HttpStatus.OK);
    }

    @DeleteMapping("/stores/{storeId}")
    public void deleteStore(AuthUser authUser, @PathVariable("storeId") Long storeId) {
        storeService.deleteStore(authUser, storeId);
    }
}