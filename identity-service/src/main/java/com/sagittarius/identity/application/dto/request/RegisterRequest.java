package com.sagittarius.identity.application.dto.request;

import lombok.Data;

public record RegisterRequest(String username,
                              String email,
                              String password,
                              String fullName,
                              String phoneNumber) {

    public String getFirstName() {
        if (fullName == null || fullName.trim().isEmpty()) return "";
        String trimmed = fullName.trim();
        int lastSpaceIndex = trimmed.lastIndexOf(" ");
        if (lastSpaceIndex == -1) return trimmed;

        return trimmed.substring(0, lastSpaceIndex);
    }
    public String getLastName() {
        if (fullName == null || fullName.trim().isEmpty()) return "";
        String trimmed = fullName.trim();
        int lastSpaceIndex = trimmed.lastIndexOf(" ");

        if (lastSpaceIndex == -1) return "";

        return trimmed.substring(lastSpaceIndex + 1);
    }
}
