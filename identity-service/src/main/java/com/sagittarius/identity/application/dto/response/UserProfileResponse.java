package com.sagittarius.identity.application.dto.response;

import lombok.Builder;

@Builder
public record UserProfileResponse(String id, String username, String email, String fullName, String firstName, String lastName, String phoneNumber) {
    public static UserProfileResponse fromEntity(com.sagittarius.identity.adapter.persistence.entity.UserEntity user) {
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
}
