package com.example.noweat.dto.user.response;

import com.example.noweat.domain.user.UserRole;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class UserResponseDto {
    private final Long id;
    private final String username;
    private final String userAddress;
    private final UserRole userRole;
    private final Long storeCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @Builder
    public UserResponseDto(Long id, String username, String userAddress, UserRole userRole, Long storeCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.userAddress = userAddress;
        this.userRole = userRole;
        this.storeCount = storeCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
