package com.sagittarius.inventory;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@OpenAPIDefinition(servers = {@Server(url = "/", description = "API Gateway Route")})
public class InventoryServiceApplication {
    public static void main(String[] args)
    {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }
}
