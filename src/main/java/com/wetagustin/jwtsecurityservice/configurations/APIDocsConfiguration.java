package com.wetagustin.jwtsecurityservice.configurations;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class APIDocsConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Spring-OAuth2-Security-Service")
                        .version("1.0.0")
                        .description("API documentation for my Spring Boot application")
                        .contact(new Contact()
                                .name("Agustin Wet")
                                .email("wet.4gustin@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Development server")
                        // new Server().url("https://api.myapp.com").description("Production server")
                ));
    }
}
