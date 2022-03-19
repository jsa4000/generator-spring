package com.example.<%- artifactId %>.tests.helpers.integration.base;

import com.example.architecture.jpa.impl.JpaDaoImpl;
import com.example.architecture.test.utils.TestContainersUtils;
import org.junit.jupiter.api.AfterAll;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.transaction.Transactional;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = AbstractMSSQLServerContainerTest.Initializer.class)
@EntityScan(basePackages = "com.example.<%- artifactId %>.dao.entities")
@EnableJpaRepositories(
        basePackages = "com.example.<%- artifactId %>.dao.repositories",
        repositoryBaseClass = JpaDaoImpl.class)
@Transactional
@Rollback
@ActiveProfiles(profiles = "test", inheritProfiles = false)
@TestPropertySource(locations = "classpath:application-testcontainers-sqlserver-test.yml")
public abstract class AbstractMSSQLServerContainerTest {

    @Container
    public static MSSQLServerContainer<?> mssqlServerContainer =
            new MSSQLServerContainer<>(TestContainersUtils.MSSQLServer.getDockerImageNameByOS())
                    .acceptLicense();

    @AfterAll
    static void after() {
        mssqlServerContainer.close();
    }

    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(
                final ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                            "spring.datasource.url=" + mssqlServerContainer.getJdbcUrl(),
                            "spring.datasource.username=" + mssqlServerContainer.getUsername(),
                            "spring.datasource.password=" + mssqlServerContainer.getPassword())
                    .applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
