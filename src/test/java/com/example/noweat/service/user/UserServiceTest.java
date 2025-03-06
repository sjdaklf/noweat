package com.example.noweat.service.user;

import com.example.noweat.domain.order.Order;
import com.example.noweat.domain.review.Review;
import com.example.noweat.domain.review.StarRating;
import com.example.noweat.domain.store.Store;
import com.example.noweat.domain.store.StoreCategory;
import com.example.noweat.domain.user.User;
import com.example.noweat.domain.user.UserRole;
import com.example.noweat.dto.user.request.UserDeleteRequestDto;
import com.example.noweat.dto.user.request.UserUpdateNameAndAddressRequestDto;
import com.example.noweat.dto.user.request.UserUpdatePasswordRequestDto;
import com.example.noweat.dto.user.response.*;
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
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
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

    private Store store1;
    private Store store2;

    private Order order1;
    private Order order2;
    private Order order3;

    private Review review1;
    private Review review2;
    private Review review3;

    @Test
    @DisplayName("유저가 자기 자신의 정보 조회 성공")
    void findUserTest() {
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

        authUser = new AuthUser(1L, "a@a.com", UserRole.USER);

        when(userRepository.findById(authUser.getId())).thenReturn(Optional.of(user));

        // when
        UserResponseDto result = userService.findUser(authUser, authUser.getId());

        // then
        assertTrue(result instanceof UserFindResponseDto);
        UserFindResponseDto responseDto = (UserFindResponseDto) result;
        assertEquals(user.getId(), responseDto.getId());
        assertEquals(user.getName(), responseDto.getName());
        assertEquals(user.getUserRole(), responseDto.getUserRole());
    }

    @Test
    @DisplayName("사장이 자기 자신의 정보 조회 성공")
    void findOwnerTest() {
        // given
        user = new User();
        ReflectionTestUtils.setField(user, "id", 2L);
        ReflectionTestUtils.setField(user, "email", "b@b.com");
        ReflectionTestUtils.setField(user, "password", "0000");
        ReflectionTestUtils.setField(user, "address", "서울");
        ReflectionTestUtils.setField(user, "name", "사장");
        ReflectionTestUtils.setField(user, "storeCount", 2L);
        ReflectionTestUtils.setField(user, "userRole", UserRole.OWNER);
        ReflectionTestUtils.setField(user, "isDeleted", false);
        ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(user, "updatedAt", LocalDateTime.now());

        authUser = new AuthUser(2L, "a@a.com", UserRole.OWNER);

        when(userRepository.findById(authUser.getId())).thenReturn(Optional.of(user));

        // when
        UserResponseDto result = userService.findUser(authUser, authUser.getId());

        // then
        assertTrue(result instanceof UserOwnerFindResponseDto);
        UserOwnerFindResponseDto responseDto = (UserOwnerFindResponseDto) result;
        assertEquals(user.getId(), responseDto.getId());
        assertEquals(user.getName(), responseDto.getName());
        assertEquals(user.getUserRole(), responseDto.getUserRole());
        assertEquals(user.getStoreCount(), responseDto.getStoreCount());
    }

    @Test
    @DisplayName("사장이 유저의 정보 조회 성공")
    void findUserTest2() {
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

        authUser = new AuthUser(2L, "a@a.com", UserRole.OWNER);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // when
        UserResponseDto result = userService.findUser(authUser, user.getId());

        // then
        assertTrue(result instanceof UserFindResponseDto);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getUserRole(), result.getUserRole());
    }

    @Test
    @DisplayName("유저가 isDeleted 된 경우")
    void deletedUserTest() {
        // given
        user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "email", "a@a.com");
        ReflectionTestUtils.setField(user, "password", "0000");
        ReflectionTestUtils.setField(user, "address", "서울");
        ReflectionTestUtils.setField(user, "name", "유저");
        ReflectionTestUtils.setField(user, "userRole", UserRole.USER);
        ReflectionTestUtils.setField(user, "isDeleted", true);
        ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(user, "updatedAt", LocalDateTime.now());

        when(authUser.getId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When & Then
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            userService.findUser(authUser, authUser.getId());
        });

        assertEquals(ErrorCode.USER_ALREADY_DELETED, exception.getErrorCode());
    }

    @Test
    @DisplayName("유저를 찾을 수 없는 경우")
    void notFoundUserTest() {
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

        when(authUser.getId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.findUser(authUser, authUser.getId());
        });

        assertEquals(ErrorCode.NOT_FOUND_USER, exception.getErrorCode());
    }

    @Test
    @DisplayName("UserId로 Store 찾기 성공")
    void findStoresByUserIdTest() {
        // given
        authUser = new AuthUser(2L, "b@b.com", UserRole.OWNER);

        user = new User();
        ReflectionTestUtils.setField(user, "id", 2L);
        ReflectionTestUtils.setField(user, "email", "b@b.com");
        ReflectionTestUtils.setField(user, "password", "0000");
        ReflectionTestUtils.setField(user, "address", "서울");
        ReflectionTestUtils.setField(user, "name", "사장");
        ReflectionTestUtils.setField(user, "storeCount", 2L);
        ReflectionTestUtils.setField(user, "userRole", UserRole.OWNER);
        ReflectionTestUtils.setField(user, "isDeleted", false);
        ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(user, "updatedAt", LocalDateTime.now());

        store1 = new Store();
        ReflectionTestUtils.setField(store1, "id", 1L);
        ReflectionTestUtils.setField(store1, "user", user);
        ReflectionTestUtils.setField(store1, "name", "test1");
        ReflectionTestUtils.setField(store1, "address", "서울");
        ReflectionTestUtils.setField(store1, "storeCategory", StoreCategory.KOREAN);
        ReflectionTestUtils.setField(store1, "minOrderPrice", 13000L);
        ReflectionTestUtils.setField(store1, "averageRating", 4.8);
        ReflectionTestUtils.setField(store1, "openTime", LocalTime.parse("10:00:00"));
        ReflectionTestUtils.setField(store1, "closedTime", LocalTime.parse("20:00:00"));
        ReflectionTestUtils.setField(store1, "isClosed", false);

        store2 = new Store();
        ReflectionTestUtils.setField(store2, "id", 2L);
        ReflectionTestUtils.setField(store2, "user", user);
        ReflectionTestUtils.setField(store2, "name", "test2");
        ReflectionTestUtils.setField(store2, "address", "서울");
        ReflectionTestUtils.setField(store2, "storeCategory", StoreCategory.KOREAN);
        ReflectionTestUtils.setField(store2, "minOrderPrice", 12000L);
        ReflectionTestUtils.setField(store2, "averageRating", 4.3);
        ReflectionTestUtils.setField(store2, "openTime", LocalTime.parse("10:00:00"));
        ReflectionTestUtils.setField(store2, "closedTime", LocalTime.parse("20:00:00"));
        ReflectionTestUtils.setField(store2, "isClosed", false);

        when(userRepository.findById(authUser.getId())).thenReturn(Optional.of(user));
        when(storeRepository.findStoresByUserId(user.getId())).thenReturn(Arrays.asList(store1, store2));

        // when
        List<UserStoreResponseDto> result = userService.findStoresByUserId(authUser);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("test1", result.get(0).getName());
        assertEquals(StoreCategory.KOREAN, result.get(0).getStoreCategory());
        assertEquals(13000L, result.get(0).getMinOrderPrice());
        assertEquals(4.8, result.get(0).getAverageRating());

        assertEquals("test2", result.get(1).getName());
        assertEquals(StoreCategory.KOREAN, result.get(1).getStoreCategory());
        assertEquals(12000L, result.get(1).getMinOrderPrice());
        assertEquals(4.3, result.get(1).getAverageRating());
    }

    @Test
    @DisplayName("UserId로 Review 찾기 성공")
    void findReviewsByUserIdTest() {
        // given
        authUser = new AuthUser(1L, "a@a.com", UserRole.USER);

        user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "email", "a@a.com");
        ReflectionTestUtils.setField(user, "password", "0000");
        ReflectionTestUtils.setField(user, "address", "서울");
        ReflectionTestUtils.setField(user, "name", "유저");
        ReflectionTestUtils.setField(user, "storeCount", 3L);
        ReflectionTestUtils.setField(user, "userRole", UserRole.USER);
        ReflectionTestUtils.setField(user, "isDeleted", false);
        ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(user, "updatedAt", LocalDateTime.now());

        review1 = new Review();
        ReflectionTestUtils.setField(review1, "id", 1L);
        ReflectionTestUtils.setField(review1, "user", user);
        ReflectionTestUtils.setField(review1, "store", store1);
        ReflectionTestUtils.setField(review1, "order", order1);
        ReflectionTestUtils.setField(review1, "title", "test title1");
        ReflectionTestUtils.setField(review1, "content", "test contents1");
        ReflectionTestUtils.setField(review1, "starRating", StarRating.FOUR);

        review2 = new Review();
        ReflectionTestUtils.setField(review2, "id", 1L);
        ReflectionTestUtils.setField(review2, "user", user);
        ReflectionTestUtils.setField(review2, "store", store2);
        ReflectionTestUtils.setField(review2, "order", order2);
        ReflectionTestUtils.setField(review2, "title", "test title2");
        ReflectionTestUtils.setField(review2, "content", "test contents2");
        ReflectionTestUtils.setField(review2, "starRating", StarRating.FIVE);

        review3 = new Review();
        ReflectionTestUtils.setField(review3, "id", 1L);
        ReflectionTestUtils.setField(review3, "user", user);
        ReflectionTestUtils.setField(review3, "store", store1);
        ReflectionTestUtils.setField(review3, "order", order3);
        ReflectionTestUtils.setField(review3, "title", "test title3");
        ReflectionTestUtils.setField(review3, "content", "test contents3");
        ReflectionTestUtils.setField(review3, "starRating", StarRating.THREE);

        when(userRepository.findById(authUser.getId())).thenReturn(Optional.of(user));
        when(reviewRepository.findReviewsByUserId(user.getId())).thenReturn(Arrays.asList(review1, review2, review3));

        // when
        List<UserReviewResponseDto> result = userService.findReviewsByUserId(authUser);

        // then
        assertNotNull(result);
        assertEquals(3, result.size());

        assertEquals("test title1", result.get(0).getTitle());
        assertEquals("test contents1", result.get(0).getContent());
        assertEquals(StarRating.FOUR, result.get(0).getStarRating());

        assertEquals("test title2", result.get(1).getTitle());
        assertEquals("test contents2", result.get(1).getContent());
        assertEquals(StarRating.FIVE, result.get(1).getStarRating());

        assertEquals("test title3", result.get(2).getTitle());
        assertEquals("test contents3", result.get(2).getContent());
        assertEquals(StarRating.THREE, result.get(2).getStarRating());
    }

    @Test
    @DisplayName("User의 Name과 Address 수정 성공")
    void updateNameAndAddressTest() {
        // given
        user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "email", "a@a.com");
        ReflectionTestUtils.setField(user, "password", "0000");
        ReflectionTestUtils.setField(user, "address", "oldAddress");
        ReflectionTestUtils.setField(user, "name", "oldname");
        ReflectionTestUtils.setField(user, "userRole", UserRole.USER);
        ReflectionTestUtils.setField(user, "isDeleted", false);
        ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(user, "updatedAt", LocalDateTime.now());

        UserUpdateNameAndAddressRequestDto userUpdateNameAndAddressRequestDto = new UserUpdateNameAndAddressRequestDto();
        ReflectionTestUtils.setField(userUpdateNameAndAddressRequestDto, "address", "newAddress");
        ReflectionTestUtils.setField(userUpdateNameAndAddressRequestDto, "name", "newname");

        authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        when(userRepository.findById(authUser.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        // when
        UserResponseDto result = userService.updateUserNameAndAddress(authUser, userUpdateNameAndAddressRequestDto);

        // then
        assertNotNull(result);
        assertEquals("newAddress", result.getAddress());
        assertEquals("newname", result.getName());
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
        ReflectionTestUtils.setField(user, "address", "oldAddress");
        ReflectionTestUtils.setField(user, "name", "oldname");
        ReflectionTestUtils.setField(user, "storeCount", 2L);
        ReflectionTestUtils.setField(user, "userRole", UserRole.OWNER);
        ReflectionTestUtils.setField(user, "isDeleted", false);
        ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(user, "updatedAt", LocalDateTime.now());

        UserUpdateNameAndAddressRequestDto userUpdateNameAndAddressRequestDto = new UserUpdateNameAndAddressRequestDto();
        ReflectionTestUtils.setField(userUpdateNameAndAddressRequestDto, "address", "newAddress");
        ReflectionTestUtils.setField(userUpdateNameAndAddressRequestDto, "name", "newname");

        authUser = new AuthUser(2L, "b@b.com", UserRole.OWNER);
        when(userRepository.findById(authUser.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        // when
        UserResponseDto result = userService.updateUserNameAndAddress(authUser, userUpdateNameAndAddressRequestDto);

        // then
        assertNotNull(result);
        assertEquals("newAddress", result.getAddress());
        assertEquals("newname", result.getName());
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
        ReflectionTestUtils.setField(user, "address", "대구");
        ReflectionTestUtils.setField(user, "name", "유저");
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
        assertEquals("유저", result.getName());
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
        ReflectionTestUtils.setField(user, "address", "서울");
        ReflectionTestUtils.setField(user, "name", "사장");
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
        assertEquals("사장", result.getName());
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
        ReflectionTestUtils.setField(user, "address", "대구");
        ReflectionTestUtils.setField(user, "name", "유저");
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
