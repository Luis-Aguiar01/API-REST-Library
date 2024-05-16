package com.luis.aguiar.configs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("security", securityScheme()))
                .info(
                new Info()
                        .title("REST API - Library")
                        .description("API para simular um serviço simples de biblioteca.")
                        .version("v1")
                        .contact(new Contact().name("Luis Henrique Aguiar").email("luishenrique.aguiar@outlook.com"))
                );
    }

    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .description("Insira um bearer token válido para acessar o recurso")
                .type(SecurityScheme.Type.HTTP)
                .in(SecurityScheme.In.HEADER)
                .scheme("bearer")
                .bearerFormat("JWT")
                .name("security");
    }
}