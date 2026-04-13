package com.sagittarius.identity.adapter.web.controller;

import com.sagittarius.identity.application.dto.request.UpdateProfileRequest;
import com.sagittarius.identity.application.dto.response.UserProfileResponse;
import com.sagittarius.identity.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(JwtAuthenticationToken authentication) {
        String userId = authentication.getName();
        return ResponseEntity.ok(userService.getMyProfile(userId));
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            JwtAuthenticationToken authentication,
            @RequestBody UpdateProfileRequest request) {

        String userId = authentication.getName();
        return ResponseEntity.ok(userService.updateMyProfile(userId, request));
    }



}
