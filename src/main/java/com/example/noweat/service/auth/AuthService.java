package com.example.noweat.service.auth;

import com.example.noweat.domain.user.User;
import com.example.noweat.domain.user.UserRole;
import com.example.noweat.dto.auth.reponse.UserSingupResponseDto;
import com.example.noweat.dto.auth.request.UserSingupRequestDto;
import com.example.noweat.global.config.PasswordEncoder;
import com.example.noweat.repository.user.UserRepository;
import com.example.noweat.service.exception.BadRequestException;
import com.example.noweat.service.exception.ConflictException;
import com.example.noweat.service.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserSingupResponseDto signupUser(UserSingupRequestDto userSingupRequestDto){

        if(userRepository.existsByEmail(userSingupRequestDto.getEmail())){
            throw new ConflictException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        UserRole userRole = UserRole.of(userSingupRequestDto.getUserRole()).orElseThrow(() -> new BadRequestException(ErrorCode.INVALID_USER_ROLE));

        String encodedPassword = passwordEncoder.encode(userSingupRequestDto.getPassword());

        User user = User.builder()
                .email(userSingupRequestDto.getEmail())
                .password(encodedPassword)
                .userAddress(userSingupRequestDto.getUserAddress())
                .username(userSingupRequestDto.getUsername())
                .userRole(userRole)
                .storeCount(0L)
                .isDeleted(false)
                .build();

        User saveUser = userRepository.save(user);

        return UserSingupResponseDto.builder()
                .id(saveUser.getId())
                .username(saveUser.getUsername())
                .userRole(saveUser.getUserRole())
                .createdAt(saveUser.getCreatedAt())
                .build();
    }

}
