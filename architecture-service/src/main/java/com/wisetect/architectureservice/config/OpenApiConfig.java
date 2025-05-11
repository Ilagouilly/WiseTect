package com.wisetect.architectureservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

/**
 * Configuration class for setting up OpenAPI documentation for the Architecture
 * Service.
 * This class defines the metadata for the API documentation, such as title,
 * version, description, and contact details.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Creates a custom OpenAPI bean to configure the API documentation.
     *
     * @return an OpenAPI instance with metadata for the Architecture Service API.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("WiseTect Service API")
                        .version("1.0.0")
                        .description("API documentation for the Architecture Service in the WiseTect platform.")
                        .contact(new Contact()
                                .name("WiseTect Development Team")
                                .email("dev@wisetect.com")
                                .url("https://wisetect.com")));
    }
}
