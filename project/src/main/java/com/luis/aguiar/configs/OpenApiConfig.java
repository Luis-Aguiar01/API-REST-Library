package com.luis.aguiar.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(
                new Info()
                        .title("REST API - Library")
                        .description("API para simular um serviço simples de biblioteca.")
                        .version("v1")
                        .contact(new Contact().name("Luis Henrique Aguiar").email("luishenrique.aguiar@outlook.com"))
        );
    }
}