package com.example.<%- artifactId %>.services.impl;

import com.github.javafaker.Faker;
import com.example.architecture.core.autoconfigure.CoreAutoConfiguration;
import com.example.architecture.jpa.autoconfigure.JpaAutoConfiguration;
import com.example.architecture.services.autoconfigure.ServicesAutoConfiguration;
import com.example.<%- artifactId %>.dao.entities.<%- preffixClassName %>;
import com.example.<%- artifactId %>.dao.repositories.<%- preffixClassName %>Repository;
import com.example.<%- artifactId %>.tests.helpers.integration.base.AbstractMSSQLServerContainerTest;
import com.example.<%- artifactId %>.tests.helpers.motherobjects.<%- preffixClassName %>MotherObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Example;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(classes = {CoreAutoConfiguration.class, JpaAutoConfiguration.class,
        ServicesAutoConfiguration.class, <%- preffixClassName %>CronServiceITTest.TestConfig.class}, initializers = { <%- preffixClassName %>CronServiceITTest.Initializer.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class <%- preffixClassName %>CronServiceITTest extends AbstractMSSQLServerContainerTest {

    private static final Faker faker = new Faker();

    @Autowired
    private <%- preffixClassName %>CronService <%- preffixVariableName %>CronService;
    @Autowired
    private <%- preffixClassName %>Repository <%- preffixVariableName %>Repository;

    @BeforeEach
    public void beforeEach() {
        <%- preffixVariableName %>Repository.deleteAll();
    }

    @Test
    void runTest() {
        final var passportGiven = <%- preffixClassName %>MotherObject.list(faker.random().nextInt(5, 10));
        <%- preffixVariableName %>Repository.saveAllAndFlush(passportGiven);
        assertEquals(passportGiven.size(), <%- preffixVariableName %>Repository.count());

        <%- preffixVariableName %>CronService.run();

        assertEquals(0, <%- preffixVariableName %>Repository.count(Example.of(<%- preffixClassName %>.builder().reviewed(false).build())));
    }

    @Configuration
    static class TestConfig {
        @Bean
        public <%- preffixClassName %>CronService <%- preffixVariableName %>CronService(<%- preffixClassName %>Service <%- preffixVariableName %>Service) {
            return new <%- preffixClassName %>CronService(<%- preffixVariableName %>Service);
        }

        @Bean
        public <%- preffixClassName %>Service <%- preffixVariableName %>Service(<%- preffixClassName %>Repository <%- preffixVariableName %>Repository) {
            return new <%- preffixClassName %>Service(<%- preffixVariableName %>Repository);
        }
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(
                final ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                            /*
                            XXX: Necessary because @Transactional.REQUIRES_NEW not working in JUnit tests
                                 (test context only is allowed one transaction in runtime)
                            */
                            "spring.services.auto-transactional.enabled=false")
                    .applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}