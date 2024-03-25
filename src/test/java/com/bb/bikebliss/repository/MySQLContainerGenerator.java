package com.bb.bikebliss.repository;

import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

public class MySQLContainerGenerator {
    @Container
    public static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.3"))
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
}