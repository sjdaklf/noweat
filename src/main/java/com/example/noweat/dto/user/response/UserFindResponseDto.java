package com.example.noweat.dto.user.response;

import com.example.noweat.domain.user.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserFindResponseDto extends UserResponseDto {

    public UserFindResponseDto(Long id, String name, String address, UserRole userRole, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, name, address, userRole, createdAt, updatedAt);
    }
}
