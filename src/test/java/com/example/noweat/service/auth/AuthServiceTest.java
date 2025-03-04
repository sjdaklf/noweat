package com.example.noweat.service.auth;

import com.example.noweat.domain.auth.RefreshToken;
import com.example.noweat.domain.user.User;
import com.example.noweat.domain.user.UserRole;
import com.example.noweat.dto.auth.reponse.RefreshTokenResponseDto;
import com.example.noweat.dto.auth.reponse.UserSigninResponseDto;
import com.example.noweat.dto.auth.reponse.UserSignupResponseDto;
import com.example.noweat.dto.auth.request.RefreshTokenRequestDto;
import com.example.noweat.dto.auth.request.UserSigninRequestDto;
import com.example.noweat.dto.auth.request.UserSignupRequestDto;
import com.example.noweat.global.config.PasswordEncoder;
import com.example.noweat.global.jwt.JwtUtil;
import com.example.noweat.repository.auth.RefreshTokenRepository;
import com.example.noweat.repository.user.UserRepository;
import com.example.noweat.service.exception.ConflictException;
import com.example.noweat.service.exception.NotFoundException;
import com.example.noweat.service.exception.UnauthorizedException;
import com.example.noweat.service.exception.enums.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;


import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    UserRepository userRepository;

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @Mock
    JwtUtil jwtUtil;

    @InjectMocks
    AuthService authService;

    @Test
    @DisplayName("정상적인 회원가입 성공")
    void signupUserTest(){
        // given
        UserSignupRequestDto requestDto = new UserSignupRequestDto();
        ReflectionTestUtils.setField(requestDto, "email", "scie429@gmail.com");
        ReflectionTestUtils.setField(requestDto, "password", "1234");
        ReflectionTestUtils.setField(requestDto, "address", "주소");
        ReflectionTestUtils.setField(requestDto, "name", "tgg");
        ReflectionTestUtils.setField(requestDto, "userRole", "USER");

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

        User savedUser = User.builder()
                .email(requestDto.getEmail())
                .password("encodedPassword")
                .name(requestDto.getName())
                .address(requestDto.getAddress())
                .userRole(UserRole.USER)
                .storeCount(0L)
                .isDeleted(false)
                .build();

        LocalDateTime localDateTime = LocalDateTime.now();
        ReflectionTestUtils.setField(savedUser,"id",1L);
        ReflectionTestUtils.setField(savedUser,"createdAt", localDateTime);
        ReflectionTestUtils.setField(savedUser,"updatedAt", localDateTime);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // when
        UserSignupResponseDto responseDto = authService.signupUser(requestDto);

        //then
        assertThat(responseDto.getId()).isEqualTo(savedUser.getId());
        assertThat(responseDto.getName()).isEqualTo(savedUser.getName());
        assertThat(responseDto.getUserRole()).isEqualTo(UserRole.USER);
        assertThat(responseDto.getCreatedAt()).isEqualTo(localDateTime);

        verify(userRepository, times(1)).existsByEmail(any());
        verify(passwordEncoder, times(1)).encode(any());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("회원가입시 중복된 이메일 일때")
    void duplicatedEmailTest(){
        // given
        UserSignupRequestDto requestDto = new UserSignupRequestDto();
        ReflectionTestUtils.setField(requestDto, "email", "scie429@gmail.com");
        ReflectionTestUtils.setField(requestDto, "password", "1234");
        ReflectionTestUtils.setField(requestDto, "address", "주소");
        ReflectionTestUtils.setField(requestDto, "name", "tgg");
        ReflectionTestUtils.setField(requestDto, "userRole", "USER");

        when(userRepository.existsByEmail(any())).thenReturn(true);

        // when, then
        assertThatThrownBy(() -> authService.signupUser(requestDto)).isInstanceOf(ConflictException.class);

        verify(userRepository, times(1)).existsByEmail(any());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("로그인 할때 이메일과 비밀번호가 일치하고 db에 RefreshToken 이 없는 경우")
    void loginTest(){
        // given
        UserSigninRequestDto requestDto = new UserSigninRequestDto();

        User findUser = new User();

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(findUser));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(refreshTokenRepository.findByUser_IdAndDeviceId(any(), any())).thenReturn(Optional.empty());
        when(jwtUtil.createAccessToken(any(), any(), any())).thenReturn("accessToken");
        when(jwtUtil.createRefreshToken(any(), any(), any())).thenReturn("refreshToken");
        when(refreshTokenRepository.save(any())).thenReturn(null);

        // when
        UserSigninResponseDto responseDto = authService.signinUser(requestDto);

        // then
        assertThat(responseDto.getAccessToken()).isEqualTo("accessToken");
        assertThat(responseDto.getRefreshToken()).isEqualTo("refreshToken");

        verify(userRepository, times(1)).findByEmail(any());
        verify(passwordEncoder, times(1)).matches(any(), any());
        verify(refreshTokenRepository, times(1)).findByUser_IdAndDeviceId(any(), any());
        verify(refreshTokenRepository, times(1)).save(any());
        verify(jwtUtil, times(1)).createAccessToken(any(), any(), any());
        verify(jwtUtil, times(1)).createRefreshToken(any(), any(), any());
    }

    @Test
    @DisplayName("로그인시 이메일에 해당하는 유저를 찾지 못했을 때")
    void notFindUserTest(){
        // given
        UserSigninRequestDto requestDto = new UserSigninRequestDto();

        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> authService.signinUser(requestDto)).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("로그인시 비밀번호가 틀렸을때")
    void invalidPassword(){
        // given
        UserSigninRequestDto requestDto = new UserSigninRequestDto();
        User findUser = new User();

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(findUser));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        // when, then
        assertThatThrownBy(() -> authService.signinUser(requestDto)).isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("로그인시 db에 refresh토큰이 존재하고 해당 토큰이 만료되지 않은 경우")
    void validRefreshToken(){
        // given
        UserSigninRequestDto requestDto = new UserSigninRequestDto();
        User findUser = new User();
        RefreshToken refreshToken = new RefreshToken();

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(findUser));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(refreshTokenRepository.findByUser_IdAndDeviceId(any(), any())).thenReturn(Optional.of(refreshToken));
        when(jwtUtil.getRefreshTokenClaims(any())).thenReturn(any());

        // when, then
        assertThatThrownBy(() -> authService.signinUser(requestDto)).isInstanceOf(ConflictException.class);
    }

    @Test
    @DisplayName("로그인시 db에 refresh토큰이 존재하고 해당 토큰이 만료된 경우")
    void invalidRefreshToken(){
        // given
        UserSigninRequestDto requestDto = new UserSigninRequestDto();
        User findUser = new User();
        RefreshToken refreshToken = new RefreshToken();

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(findUser));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(refreshTokenRepository.findByUser_IdAndDeviceId(any(), any())).thenReturn(Optional.of(refreshToken));
        when(jwtUtil.getRefreshTokenClaims(any())).thenThrow(new UnauthorizedException(ErrorCode.REFRESH_TOKEN_EXPIRED));
        doNothing().when(refreshTokenRepository).deleteByUser_IdAndDeviceId(any(), any());
        when(jwtUtil.createAccessToken(any(), any(), any())).thenReturn("accessToken");
        when(jwtUtil.createRefreshToken(any(), any(), any())).thenReturn("refreshToken");
        when(refreshTokenRepository.save(any())).thenReturn(null);

        // when
        UserSigninResponseDto responseDto = authService.signinUser(requestDto);

        // then
        assertThat(responseDto.getAccessToken()).isEqualTo("accessToken");
        assertThat(responseDto.getRefreshToken()).isEqualTo("refreshToken");

        verify(userRepository, times(1)).findByEmail(any());
        verify(passwordEncoder, times(1)).matches(any(), any());
        verify(refreshTokenRepository, times(1)).findByUser_IdAndDeviceId(any(), any());
        verify(jwtUtil, times(1)).getRefreshTokenClaims(any());
        verify(refreshTokenRepository, times(1)).deleteByUser_IdAndDeviceId(any(), any());
        verify(jwtUtil, times(1)).createAccessToken(any(), any(), any());
        verify(jwtUtil, times(1)).createRefreshToken(any(), any(), any());
        verify(refreshTokenRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("로그아웃 테스트 작성")
    void signoutTest(){
        // given
        doNothing().when(refreshTokenRepository).deleteByUser_IdAndDeviceId(any(), any());

        // when
        authService.signout(any(), any());

        // then
        verify(refreshTokenRepository, times(1)).deleteByUser_IdAndDeviceId(any(), any());
    }

    @Test
    @DisplayName("토큰 재발급시 db에 존재하지 않는 토큰이면 예외처리")
    void notFoundRefreshTokenTest(){
        // given
        RefreshTokenRequestDto refreshTokenRequestDto = new RefreshTokenRequestDto();

        Claims claims = Jwts.claims();

        when(jwtUtil.getRefreshTokenClaims(any())).thenReturn(claims);
        when(refreshTokenRepository.findByRefreshToken(any())).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> authService.refreshToken(refreshTokenRequestDto)).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("토큰 재발급시 db에 존재하는 토큰이면 정상 진행")
    void foundRefreshTokenTest(){
        // given
        RefreshTokenRequestDto refreshTokenRequestDto = new RefreshTokenRequestDto();
        RefreshToken refreshToken = new RefreshToken();

        Claims claims = Jwts.claims();
        claims.setSubject("1");
        claims.put("email", "이메일");
        claims.put("userRole", UserRole.USER.name());

        when(jwtUtil.getRefreshTokenClaims(any())).thenReturn(claims);
        when(refreshTokenRepository.findByRefreshToken(any())).thenReturn(Optional.of(refreshToken));
        when(jwtUtil.createAccessToken(any(), any(), any())).thenReturn("accessToken");
        when(jwtUtil.createRefreshToken(any(), any(), any())).thenReturn("refreshToken");
        doNothing().when(refreshTokenRepository).deleteById(any());
        when(refreshTokenRepository.save(any())).thenReturn(any());

        // when
        RefreshTokenResponseDto responseDto = authService.refreshToken(refreshTokenRequestDto);

        // then
        assertThat(responseDto.getAccessToken()).isEqualTo("accessToken");
        assertThat(responseDto.getRefreshToken()).isEqualTo("refreshToken");

        verify(jwtUtil, times(1)).getRefreshTokenClaims(any());
        verify(refreshTokenRepository, times(1)).findByRefreshToken(any());
        verify(jwtUtil, times(1)).createAccessToken(any(), any(), any());
        verify(jwtUtil, times(1)).createRefreshToken(any(), any(), any());
        verify(refreshTokenRepository, times(1)).deleteById(any());
        verify(refreshTokenRepository, times(1)).save(any());
    }
}