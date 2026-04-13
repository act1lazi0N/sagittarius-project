package com.sagittarius.identity.infrastucture.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Value("${app.keycloak.server-url:http://localhost:8181}")
    private String keycloakUrl;

    @Value("${app.keycloak.username}")
    private String keycloakUsername;

    @Value("${app.keycloak.password}")
    private String keycloakPassword;

    @Bean
    public Keycloak keycloakAdminClient() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakUrl)
                .realm("master")
                .grantType(OAuth2Constants.PASSWORD)
                .clientId("admin-cli")
                .username(keycloakUsername)
                .password(keycloakPassword)
                .build();
    }
}
