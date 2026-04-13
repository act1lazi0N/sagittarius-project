package com.sagittarius.identity.application.service;

import com.sagittarius.common.exception.BusinessException;
import com.sagittarius.identity.adapter.persistence.entity.UserEntity;
import com.sagittarius.identity.adapter.persistence.repository.UserRepository;
import com.sagittarius.identity.application.dto.request.UpdateProfileRequest;
import com.sagittarius.identity.application.dto.response.UserProfileResponse;
import com.sagittarius.identity.application.exception.IdentityErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public UserProfileResponse getMyProfile(String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(IdentityErrorCode.USER_NOT_FOUND));
        log.info("User has been found: {}", user.getUsername());

        String full = user.getFullName() != null ? user.getFullName().trim() : "";
        int lastSpace = full.lastIndexOf(" ");
        String first = lastSpace != -1 ? full.substring(0, lastSpace) : full;
        String last = lastSpace != -1 ? full.substring(lastSpace + 1) : "";

        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(full)
                .firstName(first)
                .lastName(last)
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    @Transactional
    public UserProfileResponse updateMyProfile(String userId, UpdateProfileRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        String newFullName = request.getFullName();
        if (newFullName != null && !newFullName.isBlank()) {
            user.setFullName(newFullName);
        }

        if (request.phoneNumber() != null) {
            user.setPhoneNumber(request.phoneNumber());
        }

        userRepository.save(user);
        return getMyProfile(userId);
    }

}
