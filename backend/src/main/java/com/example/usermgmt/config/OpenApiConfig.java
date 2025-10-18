package com.example.usermgmt.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi userManagementApi() {
        return GroupedOpenApi.builder()
                .group("User Management")
                .pathsToMatch("/api/v1/users/**")
                .addOpenApiCustomizer(openApi ->
                        openApi.info(new Info()
                                .title("User Management API")
                                .description("CRUD operations for user management suite")
                                .version("1.0.0")
                                .contact(new Contact()
                                        .name("User Management Suite")
                                        .email("support@example.com"))))
                .build();
    }
}

