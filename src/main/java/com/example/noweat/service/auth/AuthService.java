package com.example.noweat.service.auth;

import com.example.noweat.domain.user.User;
import com.example.noweat.domain.user.UserRole;
import com.example.noweat.dto.auth.reponse.UserSingupResponseDto;
import com.example.noweat.dto.auth.request.UserSingupRequestDto;
import com.example.noweat.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public UserSingupResponseDto signupUser(UserSingupRequestDto userSingupRequestDto){

        UserRole userRole = UserRole.of(userSingupRequestDto.getUserRole());

        User user = User.builder()
                .email(userSingupRequestDto.getEmail())
                .password(userSingupRequestDto.getPassword())
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
