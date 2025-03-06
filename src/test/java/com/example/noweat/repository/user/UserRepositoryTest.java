package com.example.noweat.repository.user;

import com.example.noweat.domain.user.User;
import com.example.noweat.domain.user.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 실제 DB 사용
@TestPropertySource(locations = "classpath:application-test.properties")  // 테스트용 DB 설정 파일
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @Test
    void testExistsByEmail() {
        // given
        user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "email", "a@a.com");
        ReflectionTestUtils.setField(user, "password", "0000");
        ReflectionTestUtils.setField(user, "address", "서울");
        ReflectionTestUtils.setField(user, "name", "유저");
        ReflectionTestUtils.setField(user, "userRole", UserRole.USER);
        ReflectionTestUtils.setField(user, "isDeleted", false);
        ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(user, "updatedAt", LocalDateTime.now());

        userRepository.save(user);

        // when
        boolean result = userRepository.existsByEmail(user.getEmail());

        // then
        assertTrue(result);
    }

    @Test
    void testFindByEmail() {
        // given
        user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "email", "a@a.com");
        ReflectionTestUtils.setField(user, "password", "0000");
        ReflectionTestUtils.setField(user, "address", "서울");
        ReflectionTestUtils.setField(user, "name", "유저");
        ReflectionTestUtils.setField(user, "userRole", UserRole.USER);
        ReflectionTestUtils.setField(user, "isDeleted", false);
        ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(user, "updatedAt", LocalDateTime.now());

        userRepository.save(user);

        // when
        Optional<User> result = userRepository.findByEmail(user.getEmail());

        // then
        assertTrue(result.isPresent());
        assertEquals(user.getEmail(), result.get().getEmail());
    }

    @Test
    void testFindByEmailNotFound() {
        // given
        String email = "c@c.com";

        // when
        Optional<User> result = userRepository.findByEmail(email);

        // then
        assertFalse(result.isPresent());
    }
}