package com.example.noweat.dto.auth.reponse;

import com.example.noweat.domain.user.UserRole;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserSignupResponseDto {
    private final Long id;

    private final String username;

    private final UserRole userRole;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;

    @Builder
    public UserSignupResponseDto(Long id, String username, UserRole userRole, LocalDateTime createdAt){
        this.id = id;
        this.username = username;
        this.userRole = userRole;
        this.createdAt = createdAt;
    }
}
