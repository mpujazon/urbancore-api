package com.urbancore.urbancore_api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI urbanCoreOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("UrbanCore API")
                        .version("v1")
                        .description("""
                                REST API for urban incident reporting, public discovery, planned actions, \
                                statistics and municipal administration.
                                """)
                        .contact(new Contact()
                                .name("UrbanCore Team")))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("""
                                        Firebase ID Token obtained from the Firebase Auth client SDK. \
                                        Prefix the token value with no additional scheme — \
                                        only the raw JWT token string is required. \
                                        Example: `eyJhbGciOiJSUzI1NiIsImtpZCI6...`
                                        """)))
                .addSecurityItem(new SecurityRequirement()
                        .addList("BearerAuth"));
    }
}
