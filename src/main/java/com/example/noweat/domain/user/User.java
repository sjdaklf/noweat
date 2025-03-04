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

    private String address;

    private String name;

    private Long storeCount;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    private boolean isDeleted;

    @Builder
    public User(String email, String password, String address, String name, Long storeCount, UserRole userRole, boolean isDeleted) {
        this.email = email;
        this.password = password;
        this.address = address;
        this.name = name;
        this.storeCount = storeCount;
        this.userRole = userRole;
        this.isDeleted = isDeleted;
    }

    public void updateUserNameAndAddress(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void deleteUser(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void increaseStoreCount() {
        this.storeCount += 1;
    }

    public void decreaseStoreCount() {
            this.storeCount -= 1;
    }

}
