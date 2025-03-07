package com.wisetect.userservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("WiseTect Service API")
                .version("1.0.0")
                .description("API documentation for User Service in WiseTect platform")
                .contact(new Contact()
                    .name("WiseTect Development Team")
                    .email("dev@wisetect.com")
                    .url("https://wisetect.com")
                )
            );
    }
}
