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
import com.example.noweat.service.exception.*;
import com.example.noweat.service.exception.enums.ErrorCode;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    public UserSignupResponseDto signupUser(UserSignupRequestDto userSignupRequestDto){

        if(userRepository.existsByEmail(userSignupRequestDto.getEmail())){
            throw new ConflictException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        UserRole userRole = UserRole.of(userSignupRequestDto.getUserRole());

        String encodedPassword = passwordEncoder.encode(userSignupRequestDto.getPassword());

        User user = User.builder()
                .email(userSignupRequestDto.getEmail())
                .password(encodedPassword)
                .address(userSignupRequestDto.getAddress())
                .name(userSignupRequestDto.getName())
                .userRole(userRole)
                .storeCount(0L)
                .isDeleted(false)
                .build();

        User saveUser = userRepository.save(user);

        return UserSignupResponseDto.builder()
                .id(saveUser.getId())
                .name(saveUser.getName())
                .userRole(saveUser.getUserRole())
                .createdAt(saveUser.getCreatedAt())
                .build();
    }

    @Transactional
    public UserSigninResponseDto signinUser(UserSigninRequestDto userSigninRequestDto){

        User findUser = userRepository.findByEmail(userSigninRequestDto.getEmail()).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));

        if(findUser.isDeleted()){
            throw new GoneException(ErrorCode.USER_ALREADY_DELETED);
        }

        if(!passwordEncoder.matches(userSigninRequestDto.getPassword(), findUser.getPassword())){
            throw new UnauthorizedException(ErrorCode.INVALID_PASSWORD);
        }

        // 리프레시 토큰을 찾아서 존재한다면 유효한지 검사, 유효하다면 예외를 던지고 유효하지 않으면 db에서 제거함
        RefreshToken findRefreshToken = refreshTokenRepository.findByUser_IdAndDeviceId(findUser.getId(), userSigninRequestDto.getDeviceId()).orElse(null);
        if(findRefreshToken != null){
            try{
                jwtUtil.getRefreshTokenClaims(findRefreshToken.getRefreshToken());
                throw new ConflictException(ErrorCode.USER_ALREADY_LOGGED_IN);
            }catch (UnauthorizedException ex){
                refreshTokenRepository.deleteByUser_IdAndDeviceId(findUser.getId(), userSigninRequestDto.getDeviceId());
            }
        }


        String accessToken = jwtUtil.createAccessToken(findUser.getId(), findUser.getEmail(), findUser.getUserRole());
        String refreshToken = jwtUtil.createRefreshToken(findUser.getId(), findUser.getEmail(), findUser.getUserRole());

        // 리프레시 토큰 저장
        refreshTokenRepository.save(RefreshToken.builder()
                .user(findUser)
                .deviceId(userSigninRequestDto.getDeviceId())
                .refreshToken(refreshToken)
                .build());

        return UserSigninResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public void signout(Long userId, String deviceId){
        refreshTokenRepository.deleteByUser_IdAndDeviceId(userId, deviceId);
    }

    @Transactional
    public RefreshTokenResponseDto refreshToken(RefreshTokenRequestDto refreshTokenRequestDto){

        // 만료된 토큰인지 검사
        Claims claims = jwtUtil.getRefreshTokenClaims(refreshTokenRequestDto.getRefreshToken());

        // 토큰 찾아오기
        RefreshToken findRefreshToken = refreshTokenRepository.findByRefreshToken(refreshTokenRequestDto.getRefreshToken()).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_REFRESH_TOKEN));

        Long userId = Long.parseLong((String)claims.getSubject());
        String email = (String)claims.get("email");
        UserRole userRole = UserRole.of((String)claims.get("userRole"));

        String accessToken = jwtUtil.createAccessToken(userId, email, userRole);
        String refreshToken = jwtUtil.createRefreshToken(userId, email, userRole);

        // 현재 db에 있는 토큰 삭제
        refreshTokenRepository.deleteById(findRefreshToken.getId());

        // 리프레시 토큰 저장
        refreshTokenRepository.save(RefreshToken.builder()
                .user(findRefreshToken.getUser())
                .deviceId(findRefreshToken.getDeviceId())
                .refreshToken(refreshToken)
                .build());

        return RefreshTokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
