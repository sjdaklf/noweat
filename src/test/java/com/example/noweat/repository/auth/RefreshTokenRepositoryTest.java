package com.example.noweat.repository.auth;

import com.example.noweat.domain.auth.RefreshToken;
import com.example.noweat.domain.user.User;
import com.example.noweat.domain.user.UserRole;
import com.example.noweat.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RefreshTokenRepositoryTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Test
    @Transactional
    void userId와_deviceId로_토큰을_조회할_수_있다(){

        // givean
        User user = User.builder()
                .email("scie429@gmail.com")
                .password("1234")
                .userAddress("전남 신안군")
                .username("김민재")
                .userRole(UserRole.USER)
                .storeCount(0L)
                .isDeleted(false)
                .build();

        userRepository.save(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .refreshToken("1234")
                .deviceId("1")
                .build();

        refreshTokenRepository.save(refreshToken);

        // when
        RefreshToken findRefreshToken = refreshTokenRepository.findByUser_IdAndDeviceId(user.getId(), refreshToken.getDeviceId()).orElse(null);

        // then
        assertThat(findRefreshToken.getRefreshToken()).isEqualTo("1234");
        assertThat(findRefreshToken.getUser()).isEqualTo(user);
        assertThat(findRefreshToken.getDeviceId()).isEqualTo("1");
    }

    @Test
    @Transactional
    void userId와_deviceId로_토큰을_삭제할_수_있다(){
        // givean
        User user = User.builder()
                .email("scie429@gmail.com")
                .password("1234")
                .userAddress("전남 신안군")
                .username("김민재")
                .userRole(UserRole.USER)
                .storeCount(0L)
                .isDeleted(false)
                .build();

        userRepository.save(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .refreshToken("1234")
                .deviceId("1")
                .build();

        refreshTokenRepository.save(refreshToken);

        // when
        refreshTokenRepository.deleteByUser_IdAndDeviceId(user.getId(), refreshToken.getDeviceId());

        // then
        assertThat(refreshTokenRepository.findByUser_IdAndDeviceId(user.getId(), refreshToken.getDeviceId()).isEmpty()).isTrue();
    }

    @Test
    @Transactional
    void refreshToken_으로_토큰을_조회할_수_있다(){

        // givean
        User user = User.builder()
                .email("scie429@gmail.com")
                .password("1234")
                .userAddress("전남 신안군")
                .username("김민재")
                .userRole(UserRole.USER)
                .storeCount(0L)
                .isDeleted(false)
                .build();

        userRepository.save(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .refreshToken("1234")
                .deviceId("1")
                .build();

        refreshTokenRepository.save(refreshToken);

        // when
        RefreshToken findRefreshToken = refreshTokenRepository.findByRefreshToken("1234").orElse(null);

        // then
        assertThat(findRefreshToken.getRefreshToken()).isEqualTo("1234");
        assertThat(findRefreshToken.getUser()).isEqualTo(user);
        assertThat(findRefreshToken.getDeviceId()).isEqualTo("1");
    }
}