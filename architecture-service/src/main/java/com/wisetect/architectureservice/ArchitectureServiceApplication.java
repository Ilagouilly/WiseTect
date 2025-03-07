package com.wisetect.architectureservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

import io.r2dbc.spi.ConnectionFactory;

/**
 * Entry point for the Architecture Service Application. Includes R2DBC
 * configuration
 * and database initialization.
 */
@SpringBootApplication
@EnableR2dbcRepositories
public class ArchitectureServiceApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArchitectureServiceApplication.class);

    public static void main(String[] args) {
        LOGGER.info("Starting Architecture Service Application...");
        SpringApplication.run(ArchitectureServiceApplication.class, args);
        LOGGER.info("Architecture Service Application started successfully");
    }

    /**
     * Initializes the database with schema if needed.
     */
    @Bean
    ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
                new ClassPathResource("db/migration/V1__create_users_table_postgres.sql"));
        initializer.setDatabasePopulator(populator);

        LOGGER.info("Database initialization configured");
        return initializer;
    }
}
