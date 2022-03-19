package com.example.<%- artifactId %>.soap.impl;

import com.github.javafaker.Faker;
<%_ if (persistenceLayer) { _%>
import com.example.architecture.jpa.autoconfigure.JpaAutoConfiguration;
import com.example.architecture.services.autoconfigure.ServicesAutoConfiguration;
import com.example.architecture.services.mapper.converters.impl.DateToXMLGregorianCalendarConverter;
import com.example.architecture.services.mapper.impl.ModelMapperFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.data.domain.Pageable;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeMap;
import com.example.<%- artifactId %>.dto.Internal<%- preffixClassName %>Dto;
<%_ } _%>
import com.example.<%- artifactId %>.AppConfigurationProperties;
import com.example.<%- artifactId %>.services.impl.<%- preffixClassName %>Service;
import com.example.<%- artifactId %>.soap.api.*;
import com.example.<%- artifactId %>.soap.config.CXFConfig;
import com.example.<%- artifactId %>.tests.helpers.motherobjects.<%- preffixClassName %>DtoMotherObject;
import lombok.SneakyThrows;
import org.hamcrest.CustomMatcher;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest(classes = { <%- preffixClassName %>WsImplTest.TestConfig.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class <%- preffixClassName %>WsImplTest {

    private static Faker faker = new Faker();

    @LocalServerPort
    private int localServerPort;
    @Autowired
    private AppConfigurationProperties appConfigurationProperties;
<%_ if (persistenceLayer) { _%>
    @MockBean
    private DateToXMLGregorianCalendarConverter dateToXMLGregorianCalendarConverter;
<%_ } _%>
    @MockBean
    private <%- preffixClassName %>Service <%- preffixVariableName %>Service;

    <%- preffixClassName %>WsPortType webServiceClient;

    @BeforeEach
    @SneakyThrows
    void beforeEach() {
        webServiceClient = new <%- preffixClassName %>WsImplService
                (new URL(String.format("http://localhost:%1$2s/services/<%- preffixClassName %>WS?wsdl", localServerPort))).get<%- preffixClassName %>WsImplPort();
    }

    @Test
    void find<%- preffixClassName %>PageTest() throws Exception {
        final var page<%- preffixClassName %> = <%- preffixClassName %>DtoMotherObject.paginationResult(appConfigurationProperties.getDefaultPageSize().intValue(),
                <%- preffixClassName %>DtoMotherObject.PaginationResultOptions.builder().withId(true).build());
<%_ if (persistenceLayer) { _%>
        when(this.<%- preffixVariableName %>Service.findByExamplePage(any(), any(Pageable.class), eq(<%- preffixClassName %>Dto.class))).thenReturn(page<%- preffixClassName %>);
<%_ } else { _%>
        when(this.<%- preffixVariableName %>Service.find<%- preffixClassName %>Page(isNull(), isNull(), isNull(), eq(<%- preffixClassName %>TypeDto.ENUM_TYPE_1), anyLong(), anyLong())).thenReturn(page<%- preffixClassName %>);
<%_ } _%>
        final PaginationResult<%- preffixClassName %>Dto apiResultList =
            webServiceClient.find<%- preffixClassName %>Page(null, null, null, <%- preffixClassName %>TypeDto.ENUM_TYPE_1, null, null);
<%_ if (persistenceLayer) { _%>
        verify(this.<%- preffixVariableName %>Service).findByExamplePage(any(), any(Pageable.class), eq(<%- preffixClassName %>Dto.class));
<%_ } else { _%>
        verify(this.<%- preffixVariableName %>Service).find<%- preffixClassName %>Page(isNull(), isNull(), isNull(), eq(<%- preffixClassName %>TypeDto.ENUM_TYPE_1), anyLong(), anyLong());
<%_ } _%> 
        MatcherAssert.assertThat(apiResultList,
                new CustomMatcher<PaginationResult<%- preffixClassName %>Dto>("apiResultList") {
                    @Override
                    @SuppressWarnings("unchecked")
                    public boolean matches(Object actual) {
                        boolean matches = true;
                        final PaginationResult<%- preffixClassName %>Dto paginationResult = (PaginationResult<%- preffixClassName %>Dto) actual;
<%_ if (persistenceLayer) { _%>
                        matches &= paginationResult.getPageNumber() == page<%- preffixClassName %>.getPageNumber();
                        matches &= paginationResult.getPageNumber() == page<%- preffixClassName %>.getPageNumber();
                        matches &= paginationResult.getPageSize() == page<%- preffixClassName %>.getPageSize();
                        matches &= paginationResult.getTotal() == page<%- preffixClassName %>.getTotalResult();
                        matches &= paginationResult.getOffset() == page<%- preffixClassName %>.getOffset();
<%_ } else { _%>
                        matches &= paginationResult.getPageNumber().equals(page<%- preffixClassName %>.getPageNumber());
                        matches &= paginationResult.getPageNumber().equals(page<%- preffixClassName %>.getPageNumber());
                        matches &= paginationResult.getPageSize().equals(page<%- preffixClassName %>.getPageSize());
                        matches &= paginationResult.getTotal() == page<%- preffixClassName %>.getTotal();
                        matches &= paginationResult.getOffset().equals(page<%- preffixClassName %>.getOffset());
<%_ } _%> 
                        for (int i = 0; i < paginationResult.getResult().size() && matches; ++i) {
                            final var <%- preffixVariableName %>Dto = paginationResult.getResult().get(i);
                            final var <%- preffixVariableName %>Entity = page<%- preffixClassName %>.getResult().get(i);
                            matches &= (<%- preffixVariableName %>Dto.getId() == null && <%- preffixVariableName %>Entity.getId() == null)
                                    || <%- preffixVariableName %>Dto.getId().equals(<%- preffixVariableName %>Entity.getId());
                            matches &= (<%- preffixVariableName %>Dto.getName() == null
                                    && <%- preffixVariableName %>Entity.getName() == null)
                                    || <%- preffixVariableName %>Dto.getName().equals(<%- preffixVariableName %>Entity.getName());
                            matches &= (<%- preffixVariableName %>Dto.getType() == null && <%- preffixVariableName %>Entity.getType() == null)
                                    || <%- preffixVariableName %>Dto.getType().toString()
                                    .equals(<%- preffixVariableName %>Entity.getType().toString());
                        }
                        return matches;
                    }
                });
    }

    @Test
    void find<%- preffixClassName %>ByIdTest() throws Exception {
        final var <%- preffixVariableName %>Dto = <%- preffixClassName %>DtoMotherObject.withId();
<%_ if (persistenceLayer) { _%>
        when(this.<%- preffixVariableName %>Service.findById(eq(<%- preffixVariableName %>Dto.getId()), eq(<%- preffixClassName %>Dto.class))).thenReturn(<%- preffixVariableName %>Dto);
<%_ } else { _%>
        when(this.<%- preffixVariableName %>Service.find<%- preffixClassName %>ById(eq(<%- preffixVariableName %>Dto.getId()))).thenReturn(<%- preffixVariableName %>Dto);
<%_ } _%>
        final var resultDto = webServiceClient.find<%- preffixClassName %>ById(<%- preffixVariableName %>Dto.getId());
<%_ if (persistenceLayer) { _%>
        verify(this.<%- preffixVariableName %>Service).findById(eq(<%- preffixVariableName %>Dto.getId()), eq(<%- preffixClassName %>Dto.class));
<%_ } else { _%>
        verify(this.<%- preffixVariableName %>Service).find<%- preffixClassName %>ById(eq(<%- preffixVariableName %>Dto.getId()));
<%_ } _%>
        MatcherAssert.assertThat(resultDto, new CustomMatcher<<%- preffixClassName %>Dto>("resultDto") {
            @Override
            public boolean matches(Object actual) {
                boolean matches = true;
                final var currentDto = (<%- preffixClassName %>Dto) actual;
                matches &= (currentDto.getId() == null && resultDto.getId() == null) || currentDto.getId().equals(resultDto.getId());
                matches &= (currentDto.getName() == null && resultDto.getName() == null) || currentDto.getName().equals(resultDto.getName());
                matches &= (currentDto.getType() == null && resultDto.getType() == null) || currentDto.getType().toString().equals(resultDto.getType().toString());
                return matches;
            }
        });
    }

    @Test
    void create<%- preffixClassName %>Test() throws Exception {
        final var <%- preffixVariableName %>Dto = <%- preffixClassName %>DtoMotherObject.create();
<%_ if (persistenceLayer) { _%>
        final var <%- preffixVariableName %>DtoPersisted = Internal<%- preffixClassName %>Dto.from<%- preffixClassName %>Dto(<%- preffixClassName %>DtoMotherObject.copy(<%- preffixVariableName %>Dto));
        <%- preffixVariableName %>DtoPersisted.setId(faker.random().nextLong());
        when(this.<%- preffixVariableName %>Service.saveOrUpdate(any(Internal<%- preffixClassName %>Dto.class))).thenReturn(<%- preffixVariableName %>DtoPersisted);
<%_ } else { _%>
        final var <%- preffixVariableName %>Id = faker.random().nextLong();
        when(this.<%- preffixVariableName %>Service.create<%- preffixClassName %>(any(<%- preffixClassName %>Dto.class))).thenReturn(<%- preffixVariableName %>Id);
<%_ } _%>
        final var newEntityId = webServiceClient.create<%- preffixClassName %>(<%- preffixVariableName %>Dto);
<%_ if (persistenceLayer) { _%>
        verify(this.<%- preffixVariableName %>Service).saveOrUpdate(any(Internal<%- preffixClassName %>Dto.class));
<%_ } else { _%>
        verify(this.<%- preffixVariableName %>Service).create<%- preffixClassName %>(any(<%- preffixClassName %>Dto.class));
<%_ } _%>
        assertNotNull(newEntityId);

    }

    @Test
    void update<%- preffixClassName %>ByIdTest() throws Exception {
        final var <%- preffixVariableName %>Dto = <%- preffixClassName %>DtoMotherObject.withId();
        webServiceClient.update<%- preffixClassName %>ById(<%- preffixVariableName %>Dto.getId(), <%- preffixVariableName %>Dto);
<%_ if (persistenceLayer) { _%>
        verify(this.<%- preffixVariableName %>Service).saveOrUpdate(any(Internal<%- preffixClassName %>Dto.class));
<%_ } else { _%>
        verify(this.<%- preffixVariableName %>Service).update<%- preffixClassName %>ById(eq(<%- preffixVariableName %>Dto.getId()), any(<%- preffixClassName %>Dto.class));
<%_ } _%>
    }

    @Test
    void delete<%- preffixClassName %>ByIdTest() throws Exception {
        final var randomId = Integer.valueOf(faker.random().nextInt(1, Integer.MAX_VALUE)).longValue();
        webServiceClient.delete<%- preffixClassName %>ById(randomId);
<%_ if (persistenceLayer) { _%>
        verify(this.<%- preffixVariableName %>Service).deleteById(randomId);
<%_ } else { _%>
        verify(this.<%- preffixVariableName %>Service).delete<%- preffixClassName %>ById(randomId);
<%_ } _%>
    }


    @SpringBootConfiguration
<%_ if (persistenceLayer) { _%>        
    @EnableAutoConfiguration(exclude = { LiquibaseAutoConfiguration.class, DataSourceAutoConfiguration.class, ServicesAutoConfiguration.class, JpaAutoConfiguration.class })
<%_ } else { _%>
    @EnableAutoConfiguration
<%_ } _%>
    @Import(CXFConfig.class)
    static class TestConfig {
        @Bean
        public AppConfigurationProperties appConfigurationProperties() {
            return new AppConfigurationProperties();
        }
<%_ if (persistenceLayer) { _%>        
        @Bean
        public ModelMapperFactory modelMapperFactory() {
            final var mock = mock(ModelMapperFactory.class);
            when(mock.registryCustomMapper(any(PropertyMap.class))).thenReturn(mock(TypeMap.class));
            return mock;
        }
<%_ } _%>
    }
}