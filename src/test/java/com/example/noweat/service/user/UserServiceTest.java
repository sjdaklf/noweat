package com.example.noweat.service.user;

import com.example.noweat.domain.user.User;
import com.example.noweat.domain.user.UserRole;
import com.example.noweat.dto.user.response.UserFindResponseDto;
import com.example.noweat.dto.user.response.UserOwnerFindResponseDto;
import com.example.noweat.dto.user.response.UserResponseDto;
import com.example.noweat.global.argumentResolver.AuthUser;
import com.example.noweat.global.config.PasswordEncoder;
import com.example.noweat.repository.review.ReviewRepository;
import com.example.noweat.repository.store.StoreRepository;
import com.example.noweat.repository.user.UserRepository;
import com.example.noweat.service.exception.NotFoundException;
import com.example.noweat.service.exception.UnauthorizedException;
import com.example.noweat.service.exception.enums.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private AuthUser authUser;

    @InjectMocks
    private UserService userService;

    private User user;

    @Test
    @DisplayName("정상적인 유저 조회 성공")
    void findUserTest() {
        // given
        user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "email", "a@a.com");
        ReflectionTestUtils.setField(user, "password", "0000");
        ReflectionTestUtils.setField(user, "userAddress", "서울");
        ReflectionTestUtils.setField(user, "username", "유저");
        ReflectionTestUtils.setField(user, "userRole", UserRole.USER);
        ReflectionTestUtils.setField(user, "isDeleted", false);
        ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(user, "updatedAt", LocalDateTime.now());

        when(authUser.getId()).thenReturn(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // when
        UserResponseDto result = userService.findUser(authUser);

        // then
        assertTrue(result instanceof UserFindResponseDto);
        UserFindResponseDto responseDto = (UserFindResponseDto) result;
        assertEquals(user.getId(), responseDto.getId());
        assertEquals(user.getUsername(), responseDto.getUsername());
        assertEquals(user.getUserRole(), responseDto.getUserRole());
    }

    @Test
    @DisplayName("정상적인 사장 조회 성공")
    void findOwnerTest() {
        // given
        user = new User();
        ReflectionTestUtils.setField(user, "id", 2L);
        ReflectionTestUtils.setField(user, "email", "b@b.com");
        ReflectionTestUtils.setField(user, "password", "0000");
        ReflectionTestUtils.setField(user, "userAddress", "서울");
        ReflectionTestUtils.setField(user, "username", "사장");
        ReflectionTestUtils.setField(user, "storeCount", 2L);
        ReflectionTestUtils.setField(user, "userRole", UserRole.OWNER);
        ReflectionTestUtils.setField(user, "isDeleted", false);
        ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(user, "updatedAt", LocalDateTime.now());

        when(authUser.getId()).thenReturn(2L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // when
        UserResponseDto result = userService.findUser(authUser);

        // then
        assertTrue(result instanceof UserOwnerFindResponseDto);
        UserOwnerFindResponseDto responseDto = (UserOwnerFindResponseDto) result;
        assertEquals(user.getId(), responseDto.getId());
        assertEquals(user.getUsername(), responseDto.getUsername());
        assertEquals(user.getUserRole(), responseDto.getUserRole());
        assertEquals(user.getStoreCount(), responseDto.getStoreCount());
    }

    @Test
    @DisplayName("유저가 isDeleted 된 경우")
    void deletedUserTest() {
        // given
        user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "email", "a@a.com");
        ReflectionTestUtils.setField(user, "password", "0000");
        ReflectionTestUtils.setField(user, "userAddress", "서울");
        ReflectionTestUtils.setField(user, "username", "유저");
        ReflectionTestUtils.setField(user, "userRole", UserRole.USER);
        ReflectionTestUtils.setField(user, "isDeleted", true);
        ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(user, "updatedAt", LocalDateTime.now());

        when(authUser.getId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When & Then
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            userService.findUser(authUser);
        });

        assertEquals(ErrorCode.DELETED_USER, exception.getErrorCode());
    }

    @Test
    @DisplayName("유저를 찾을 수 없는 경우")
    void notFoundUserTest() {
        // given
        user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "email", "a@a.com");
        ReflectionTestUtils.setField(user, "password", "0000");
        ReflectionTestUtils.setField(user, "userAddress", "서울");
        ReflectionTestUtils.setField(user, "username", "유저");
        ReflectionTestUtils.setField(user, "userRole", UserRole.USER);
        ReflectionTestUtils.setField(user, "isDeleted", false);
        ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(user, "updatedAt", LocalDateTime.now());

        when(authUser.getId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.findUser(authUser);
        });

        assertEquals(ErrorCode.NOT_FOUND_USER, exception.getErrorCode());
    }
}
