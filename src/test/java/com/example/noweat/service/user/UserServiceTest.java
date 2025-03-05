package com.example.noweat.service.user;

import com.example.noweat.domain.user.User;
import com.example.noweat.domain.user.UserRole;
import com.example.noweat.dto.user.request.UserDeleteRequestDto;
import com.example.noweat.dto.user.request.UserUpdateNameAndAddressRequestDto;
import com.example.noweat.dto.user.request.UserUpdatePasswordRequestDto;
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
import static org.mockito.Mockito.verify;
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

    @Test
    @DisplayName("UserId로 Store 찾기 성공")
    void findStoresByUserIdTest() {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("UserId로 Review 찾기 성공")
    void findReviewsByUserIdTest() {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("User의 Name과 Address 수정 성공")
    void updateUserNameAndAddressTest() {
        // given
        user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "email", "a@a.com");
        ReflectionTestUtils.setField(user, "password", "0000");
        ReflectionTestUtils.setField(user, "userAddress", "oldAddress");
        ReflectionTestUtils.setField(user, "username", "oldUsername");
        ReflectionTestUtils.setField(user, "userRole", UserRole.USER);
        ReflectionTestUtils.setField(user, "isDeleted", false);
        ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(user, "updatedAt", LocalDateTime.now());

        UserUpdateNameAndAddressRequestDto userUpdateNameAndAddressRequestDto = new UserUpdateNameAndAddressRequestDto();
        ReflectionTestUtils.setField(userUpdateNameAndAddressRequestDto, "userAddress", "newAddress");
        ReflectionTestUtils.setField(userUpdateNameAndAddressRequestDto, "username", "newUsername");

        authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        when(userRepository.findById(authUser.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        // when
        UserResponseDto result = userService.updateUserNameAndAddress(authUser, userUpdateNameAndAddressRequestDto);

        // then
        assertNotNull(result);
        assertEquals("newAddress", result.getUserAddress());
        assertEquals("newUsername", result.getUsername());
        assertEquals(UserRole.USER, result.getUserRole());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    @DisplayName("Owner의 Name과 Address 수정 성공")
    void updateOwnerNameAndAddressTest() {
        // given
        user = new User();
        ReflectionTestUtils.setField(user, "id", 2L);
        ReflectionTestUtils.setField(user, "email", "b@b.com");
        ReflectionTestUtils.setField(user, "password", "0000");
        ReflectionTestUtils.setField(user, "userAddress", "oldAddress");
        ReflectionTestUtils.setField(user, "username", "oldUsername");
        ReflectionTestUtils.setField(user, "userRole", UserRole.OWNER);
        ReflectionTestUtils.setField(user, "isDeleted", false);
        ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(user, "updatedAt", LocalDateTime.now());

        UserUpdateNameAndAddressRequestDto userUpdateNameAndAddressRequestDto = new UserUpdateNameAndAddressRequestDto();
        ReflectionTestUtils.setField(userUpdateNameAndAddressRequestDto, "userAddress", "newAddress");
        ReflectionTestUtils.setField(userUpdateNameAndAddressRequestDto, "username", "newUsername");

        authUser = new AuthUser(2L, "b@b.com", UserRole.OWNER);
        when(userRepository.findById(authUser.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        // when
        UserResponseDto result = userService.updateUserNameAndAddress(authUser, userUpdateNameAndAddressRequestDto);

        // then
        assertNotNull(result);
        assertEquals("newAddress", result.getUserAddress());
        assertEquals("newUsername", result.getUsername());
        assertEquals(UserRole.OWNER, result.getUserRole());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    @DisplayName("User의 Password 수정 성공")
    void updateUserPasswordTest() {
        // given
        user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "email", "a@a.com");
        ReflectionTestUtils.setField(user, "password", "oldPassword");
        ReflectionTestUtils.setField(user, "userAddress", "대구");
        ReflectionTestUtils.setField(user, "username", "유저");
        ReflectionTestUtils.setField(user, "userRole", UserRole.USER);
        ReflectionTestUtils.setField(user, "isDeleted", false);
        ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(user, "updatedAt", LocalDateTime.now());

        UserUpdatePasswordRequestDto userUpdatePasswordRequestDto = new UserUpdatePasswordRequestDto();
        ReflectionTestUtils.setField(userUpdatePasswordRequestDto, "oldPassword", "oldPassword");
        ReflectionTestUtils.setField(userUpdatePasswordRequestDto, "newPassword", "newPassword1!");

        authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        when(userRepository.findById(authUser.getId())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userUpdatePasswordRequestDto.getOldPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.matches(userUpdatePasswordRequestDto.getNewPassword(), user.getPassword())).thenReturn(false);
        when(passwordEncoder.encode(userUpdatePasswordRequestDto.getNewPassword())).thenReturn("encodedNewPassword");
        when(userRepository.save(user)).thenReturn(user);

        // when
        UserResponseDto result = userService.updateUserPassword(authUser, userUpdatePasswordRequestDto);

        // then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("유저", result.getUsername());
        assertEquals(UserRole.USER, result.getUserRole());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        assertEquals("encodedNewPassword", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Owner의 Password 수정 성공")
    void updateOwnerPasswordTest() {
        // given
        user = new User();
        ReflectionTestUtils.setField(user, "id", 2L);
        ReflectionTestUtils.setField(user, "email", "b@b.com");
        ReflectionTestUtils.setField(user, "password", "oldPassword");
        ReflectionTestUtils.setField(user, "userAddress", "서울");
        ReflectionTestUtils.setField(user, "username", "사장");
        ReflectionTestUtils.setField(user, "storeCount", 2L);
        ReflectionTestUtils.setField(user, "userRole", UserRole.OWNER);
        ReflectionTestUtils.setField(user, "isDeleted", false);
        ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(user, "updatedAt", LocalDateTime.now());

        UserUpdatePasswordRequestDto userUpdatePasswordRequestDto = new UserUpdatePasswordRequestDto();
        ReflectionTestUtils.setField(userUpdatePasswordRequestDto, "oldPassword", "oldPassword");
        ReflectionTestUtils.setField(userUpdatePasswordRequestDto, "newPassword", "newPassword1!");

        authUser = new AuthUser(2L, "b@b.com", UserRole.OWNER);
        when(userRepository.findById(authUser.getId())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userUpdatePasswordRequestDto.getOldPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.matches(userUpdatePasswordRequestDto.getNewPassword(), user.getPassword())).thenReturn(false);
        when(passwordEncoder.encode(userUpdatePasswordRequestDto.getNewPassword())).thenReturn("encodedNewPassword");
        when(userRepository.save(user)).thenReturn(user);

        // when
        UserResponseDto result = userService.updateUserPassword(authUser, userUpdatePasswordRequestDto);

        // then
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("사장", result.getUsername());
        assertEquals(UserRole.OWNER, result.getUserRole());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        assertEquals("encodedNewPassword", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("User 삭제 성공")
    void deleteUserTest() {
        // given
        user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "email", "a@a.com");
        ReflectionTestUtils.setField(user, "password", "0000");
        ReflectionTestUtils.setField(user, "userAddress", "대구");
        ReflectionTestUtils.setField(user, "username", "유저");
        ReflectionTestUtils.setField(user, "userRole", UserRole.USER);
        ReflectionTestUtils.setField(user, "isDeleted", false);
        ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(user, "updatedAt", LocalDateTime.now());

        UserDeleteRequestDto userDeleteRequestDto = new UserDeleteRequestDto();
        ReflectionTestUtils.setField(userDeleteRequestDto, "password", "0000");

        authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        when(userRepository.findById(authUser.getId())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userDeleteRequestDto.getPassword(), user.getPassword())).thenReturn(true);

        // when
        userService.deleteUser(authUser, userDeleteRequestDto);

        // then
        verify(userRepository).findById(authUser.getId());
        assertTrue(user.isDeleted());
    }
}
