package com.wisetect.architectureservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import jakarta.validation.Validator;

/**
 * Configuration class for setting up validation in the Architecture Service.
 * This class defines a Validator bean and integrates it with Spring WebFlux.
 */
@Configuration
public class ValidationConfig implements WebFluxConfigurer {

    /**
     * Creates a Validator bean for validating request payloads and other objects.
     *
     * @return a LocalValidatorFactoryBean instance.
     */
    @Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }
}
