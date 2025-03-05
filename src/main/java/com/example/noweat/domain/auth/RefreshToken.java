package com.example.noweat.domain.auth;

import com.example.noweat.domain.common.entity.BaseEntity;
import com.example.noweat.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class RefreshToken extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String refreshToken;

    private String deviceId;

    @Builder
    public RefreshToken(User user, String refreshToken, String deviceId) {
        this.user = user;
        this.refreshToken = refreshToken;
        this.deviceId = deviceId;
    }
}