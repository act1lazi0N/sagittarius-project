package com.sagittarius.identity.application.service;

import com.sagittarius.identity.adapter.persistence.entity.UserEntity;
import com.sagittarius.identity.adapter.persistence.repository.UserRepository;
import com.sagittarius.identity.application.dto.AuthResponse;
import com.sagittarius.identity.application.dto.LoginRequest;
import com.sagittarius.identity.application.dto.RegisterRequest;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final Keycloak keycloak;
    private final String REALM = "sagittarius-realm";

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username has been used!");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email has been used!");
        }

        // Create a user in Keycloak
        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(request.getUsername());
        kcUser.setEmail(request.getEmail());
        kcUser.setFirstName(request.getFullName());
        kcUser.setEnabled(true);
        kcUser.setEmailVerified(true);

        // Set password
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getPassword());
        credential.setTemporary(false);
        kcUser.setCredentials(Collections.singletonList(credential));

        Response response = keycloak.realm(REALM).users().create(kcUser);

        if (response.getStatus() != 201) {
            log.error("Failed to register user. HTTP status: {}", response.getStatus());
            throw new RuntimeException("Failed to register user");
        }

        // Set rules
        String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
        RoleRepresentation customerRole = keycloak.realm(REALM).roles().get("CUSTOMER").toRepresentation();
        keycloak.realm(REALM).users().get(userId).roles().realmLevel().add(Collections.singletonList(customerRole));

        // Save user to a database
        UserEntity userEntity = UserEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .role("CUSTOMER")
                .password("MANAGED_BY_KEYCLOAK")
                .isActive(true)
                .isEmailVerified(true)
                .build();
        userRepository.save(userEntity);

        log.info("User {} has been successfully synchronized among Keycloak and DB", request.getUsername());
        return AuthResponse.builder().message("Account has been created and synchronized successfully!").build();
    }
}
