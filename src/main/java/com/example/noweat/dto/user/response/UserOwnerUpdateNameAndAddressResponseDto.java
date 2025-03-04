package com.example.noweat.dto.user.response;

import com.example.noweat.domain.user.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserOwnerUpdateNameAndAddressResponseDto extends UserResponseDto {
    private Long storeCount;

    public UserOwnerUpdateNameAndAddressResponseDto(Long id, String name, String address, UserRole userRole, Long storeCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, name, address, userRole, createdAt, updatedAt);
        this.storeCount = storeCount;
    }
}
