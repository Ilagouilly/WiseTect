package com.wisetect.architectureservice;

import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
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

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();

        // Get all SQL files from the migration directory
        Resource[] resources;
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            resources = resolver.getResources("classpath:db/migration/*_h2.sql");

            // Sort resources to ensure ordered execution
            Arrays.sort(resources, (r1, r2) -> {
                try {
                    return r1.getFilename().compareTo(r2.getFilename());
                } catch (Exception e) {
                    return 0;
                }
            });

            // Add all SQL files to the populator
            populator.addScripts(resources);

            // Configure the populator
            populator.setSeparator(";");
            populator.setContinueOnError(false);

            initializer.setDatabasePopulator(populator);

            LOGGER.info("Database initialization configured with {} migration files", resources.length);
        } catch (IOException e) {
            LOGGER.error("Failed to load database migration files", e);
            throw new RuntimeException("Database initialization failed", e);
        }

        return initializer;
    }

}
