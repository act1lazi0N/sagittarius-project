package com.sagittarius.identity.application.dto.request;

public record UpdateProfileRequest(
        String firstName,
        String lastName,
        String phoneNumber
) {
    public String getFullName() {
        if (firstName == null && lastName == null) return null;

        String first = firstName != null ? firstName.trim() : "";
        String last = lastName != null ? lastName.trim() : "";

        // Nối lại và cắt khoảng trắng thừa ở 2 đầu
        return (first + " " + last).trim();
    }
}