package com.example.noweat.repository.auth;

import com.example.noweat.domain.auth.RefreshToken;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    @EntityGraph(value = "user")
    Optional<RefreshToken> findByUser_IdAndDeviceId(Long userId, String deviceId);

    void deleteByUser_IdAndDeviceId(Long userId, String deviceId);
}
