package com.example.noweat.domain.user;

import com.example.noweat.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String userAddress;

    private String username;

    private Long storeCount;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    private boolean isDeleted;

    @Builder
    public User(String email, String password, String userAddress, String username, Long storeCount, UserRole userRole, boolean isDeleted) {
        this.email = email;
        this.password = password;
        this.userAddress = userAddress;
        this.username = username;
        this.storeCount = storeCount;
        this.userRole = userRole;
        this.isDeleted = isDeleted;
    }
}
