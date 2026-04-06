package com.sagittarius.gateway.utils;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Extracting JWT Token from Security Context
        return ReactiveSecurityContextHolder.getContext()
                // Executing JWT Token Only
                .filter(c -> c.getAuthentication() instanceof JwtAuthenticationToken)
                .map(c -> (JwtAuthenticationToken) c.getAuthentication())
                .flatMap(jwtAuth -> {

                    // Extracting userId directly
                    String username = jwtAuth.getToken().getClaimAsString("preferred_username");
                    if (username == null) {
                        username = jwtAuth.getToken().getSubject();
                    }
                    String email = jwtAuth.getToken().getClaimAsString("email");

                    // Adding to the request header
                    ServerHttpRequest request = exchange.getRequest().mutate()
                            .header("X-User-Id", username)
                            .header("X-User-Email", email)
                            .build();

                    // Returning the exchange to the next filter
                    return chain.filter(exchange.mutate().request(request).build());
                })
                // Proceeding to the next filter if JWT Token is not present
                .switchIfEmpty(chain.filter(exchange));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
