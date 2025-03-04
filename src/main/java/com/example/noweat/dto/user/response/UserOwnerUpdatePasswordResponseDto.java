package com.example.noweat.dto.user.response;

import com.example.noweat.domain.user.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserOwnerUpdatePasswordResponseDto extends UserResponseDto<UserOwnerUpdatePasswordResponseDto> {
    private Long storeCount;

    @Builder
    public UserOwnerUpdatePasswordResponseDto(Long id, String username, String userAddress, UserRole userRole, Long storeCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, username, userAddress, userRole, createdAt, updatedAt);
        this.storeCount = storeCount;
    }
}
