package com.example.noweat.dto.user.response;

import com.example.noweat.domain.user.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserOwnerFindResponseDto extends UserResponseDto<UserOwnerFindResponseDto> {
    private Long storeCount;

    @Builder
    public UserOwnerFindResponseDto(Long id, String username, String userAddress, UserRole userRole, Long storeCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, username, userAddress, userRole, createdAt, updatedAt);
        this.storeCount = storeCount;
    }
}