package com.sagittarius.identity.application.service;

import com.sagittarius.common.exception.BusinessException;
import com.sagittarius.identity.adapter.persistence.entity.UserEntity;
import com.sagittarius.identity.adapter.persistence.repository.UserRepository;
import com.sagittarius.identity.application.dto.response.AuthResponse;
import com.sagittarius.identity.application.dto.request.RegisterRequest;
import com.sagittarius.identity.application.exception.IdentityErrorCode;
import com.sagittarius.identity.infrastucture.client.PaymentClient;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final Keycloak keycloak;
    private final PaymentClient paymentClient;
    private final String REALM = "sagittarius-realm";

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException(IdentityErrorCode.USERNAME_ALREADY_EXISTS);
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(IdentityErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // Create a user in Keycloak
        UserRepresentation kcUser = getUserRepresentation(request);

        Response response = keycloak.realm(REALM).users().create(kcUser);

        if (response.getStatus() != 201) {
            log.error("Failed to register user. HTTP status: {}", response.getStatus());
            throw new RuntimeException("Failed to register user");
        }

        // Set rules
        String keycloakUserId = CreatedResponseUtil.getCreatedId(response);
        String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
        RoleRepresentation customerRole = keycloak.realm(REALM).roles().get("CUSTOMER").toRepresentation();
        keycloak
                .realm(REALM)
                .users()
                .get(userId)
                .roles()
                .realmLevel()
                .add(Collections.singletonList(customerRole));

        // Save user to a database
        UserEntity userEntity = UserEntity.builder()
                .id(keycloakUserId)
                .username(request.username())
                .email(request.email())
                .fullName(request.fullName())
                .phoneNumber(request.phoneNumber())
                .role("CUSTOMER")
                .password("MANAGED_BY_KEYCLOAK")
                .isActive(true)
                .isEmailVerified(true)
                .build();
        userRepository.save(userEntity);

        try {
            paymentClient.openWallet(userEntity.getId());
            log.info("Successfully opening wallet for user: {}", userEntity.getUsername());
        } catch (Exception e) {
            log.error("Error in opening wallet {}: {}", userEntity.getUsername(), e.getMessage());
        }

        log.info("User {} has been successfully synchronized among Keycloak and DB", request.username());
        return AuthResponse.builder().message("Account has been created and synchronized successfully!").build();
    }

    private static UserRepresentation getUserRepresentation(RegisterRequest request) {
        UserRepresentation kcUser = new UserRepresentation();

        // Set user
        kcUser.setUsername(request.username());
        kcUser.setEmail(request.email());
        kcUser.setFirstName(request.getFirstName());
        kcUser.setLastName(request.getLastName());
        kcUser.setEnabled(true);
        kcUser.setEmailVerified(true);

        // Set password
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.password());
        credential.setTemporary(false);
        kcUser.setCredentials(Collections.singletonList(credential));
        return kcUser;
    }
}
