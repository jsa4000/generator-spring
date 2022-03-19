package com.example.<%- artifactId %>.services.impl;

import com.github.javafaker.Faker;
import com.example.architecture.core.autoconfigure.CoreAutoConfiguration;
import com.example.architecture.core.exceptions.ConflictException;
import com.example.architecture.core.exceptions.NotFoundException;
import com.example.architecture.jpa.autoconfigure.JpaAutoConfiguration;
import com.example.architecture.services.autoconfigure.ServicesAutoConfiguration;
import com.example.architecture.services.mapper.impl.ModelMapperFactory;
import com.example.architecture.services.vo.PaginationResult;
import com.example.<%- artifactId %>.dao.entities.<%- preffixClassName %>;
import com.example.<%- artifactId %>.dao.repositories.<%- preffixClassName %>Repository;
<%_ if (__self__._isSoapService()) { _%>
import com.example.<%- artifactId %>.soap.api.<%- preffixClassName %>Dto;
import com.example.<%- artifactId %>.dto.Internal<%- preffixClassName %>Dto;
<%_ } else if (microserviceType === 'none' && !!persistenceLayer) { _%>
import com.example.<%- artifactId %>.tests.helpers.dto.<%- preffixClassName %>Dto;
<%_ } else { _%>
import com.example.<%- artifactId %>.dto.<%- preffixClassName %>Dto;
<%_ } _%>
<%_ if (__self__._isSoapService() || microserviceType === 'apifirst') { _%>
    <%_ if (__self__._isSoapService()) { _%>
import com.example.<%- artifactId %>.soap.api.<%- preffixClassName %>TypeDto;
import com.example.architecture.services.mapper.converters.impl.DateToXMLGregorianCalendarConverter;
    <%_ } else if (microserviceType === 'apifirst') { _%>
import com.example.architecture.services.mapper.converters.impl.DateToOffsetDateTimeConverter;
import com.example.architecture.services.mapper.converters.impl.OffsetDateTimeToDateConverter;
import com.example.<%- artifactId %>.dto.<%- preffixClassName %>TypeDto;
    <%_ } _%>
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
<%_ } _%>
import org.modelmapper.PropertyMap;
import com.example.<%- artifactId %>.tests.helpers.integration.base.AbstractMSSQLServerContainerTest;
import com.example.<%- artifactId %>.tests.helpers.motherobjects.<%- preffixClassName %>DtoMotherObject;
import org.hamcrest.CustomMatcher;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = {CoreAutoConfiguration.class, JpaAutoConfiguration.class,
        ServicesAutoConfiguration.class, <%- preffixClassName %>ServiceITTest.TestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class <%- preffixClassName %>ServiceITTest extends AbstractMSSQLServerContainerTest {

    private static Faker faker = new Faker();

    @Autowired
    private <%- preffixClassName %>Service <%- preffixVariableName %>Service;
    @Autowired
    private <%- preffixClassName %>Repository <%- preffixVariableName %>Repository;

    @AfterEach
    void afterEach() {
        this.<%- preffixVariableName %>Repository.deleteAll();
    }

    @Test
    void save<%- preffixClassName %>TestSuccess() {
        final var <%- preffixVariableName %>Dto = <%- preffixClassName %>DtoMotherObject.create();
<%_ if (__self__._isSoapService()) { _%>
        final var <%- preffixVariableName %>Persisted = this.<%- preffixVariableName %>Service.saveOrUpdate(Internal<%- preffixClassName %>Dto.from<%- preffixClassName %>Dto(<%- preffixVariableName %>Dto));
<%_ } else { _%>
        final var <%- preffixVariableName %>Persisted = this.<%- preffixVariableName %>Service.saveOrUpdate(<%- preffixVariableName %>Dto);
<%_ } _%>
        assertNotNull(<%- preffixVariableName %>Persisted.getId());
    }

    @Test
    void findAll<%- preffixClassName %>sTest() {
        final var dtoList = <%- preffixClassName %>DtoMotherObject.list(10);
<%_ if (__self__._isSoapService()) { _%>
        dtoList.stream().map(Internal<%- preffixClassName %>Dto::from<%- preffixClassName %>Dto).forEach(<%- preffixVariableName %>Service::saveOrUpdate);
<%_ } else { _%>
        dtoList.forEach(<%- preffixVariableName %>Service::saveOrUpdate);
<%_ } _%>

        final var dtosGroupByType = dtoList.stream().collect(Collectors.groupingBy(<%- preffixClassName %>Dto::getType));
        for (var entry : dtosGroupByType.entrySet()) {
<%_ if (microserviceType === 'apifirst') { _%>
            final var queryResult = this.<%- preffixVariableName %>Service.findByExample(Example.of(
                            <%- preffixClassName %>.builder().type(<%- preffixClassName %>.<%- preffixClassName %>Type.fromValue(entry.getKey().getValue())).build(),
                            ExampleMatcher.matchingAll().withIgnoreCase().withIgnoreNullValues()
                                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)),
                    <%- preffixClassName %>Dto.class);
<%_ } else if (microserviceType === 'codefirst' || microserviceType === 'none') { _%>
            final var queryResult = this.<%- preffixVariableName %>Service.findByExample(Example.of(
                            <%- preffixClassName %>.builder().type(<%- preffixClassName %>.<%- preffixClassName %>Type.<%- __self__._isSoapService() ? 'valueOf' : 'fromValue' %>(entry.getKey().toString())).build(),
                            ExampleMatcher.matchingAll().withIgnoreCase().withIgnoreNullValues()
                                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)),
                    <%- preffixClassName %>Dto.class);
<%_ } else {
    throw new Error(`Unknown ${microserviceType}`);
} _%>
            assertEquals(entry.getValue().size(), queryResult.size());
            for (var entity : queryResult) {
                var dtoMatch = dtoList.stream().filter(dto -> entity.getName().equals(dto.getName())).findFirst().orElseThrow();
                assertEquals(dtoMatch.getType(), entity.getType());
            }
        }
    }

    @Test
    void findByExamplePageTest() {
        final var currentPage = faker.random().nextInt(1, 2);
        final var elementsPerPage = faker.random().nextInt(1, 5);
        final var totalElements = faker.random().nextInt(10, 20);
        final var dtoList = <%- preffixClassName %>DtoMotherObject.list(totalElements);
<%_ if (__self__._isSoapService()) { _%>
        dtoList.stream().map(Internal<%- preffixClassName %>Dto::from<%- preffixClassName %>Dto).forEach(<%- preffixVariableName %>Service::saveOrUpdate);
<%_ } else { _%>
        dtoList.forEach(<%- preffixVariableName %>Service::saveOrUpdate);
<%_ } _%>

        final var paginationResult = this.<%- preffixVariableName %>Service.findByExamplePage(Example.of(<%- preffixClassName %>.builder().build(), ExampleMatcher.matchingAll().withIgnoreCase().withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)), PageRequest.of(currentPage, elementsPerPage), <%- preffixClassName %>Dto.class);

        MatcherAssert.assertThat(paginationResult, new CustomMatcher<PaginationResult<<%- preffixClassName %>Dto>>("<%- preffixVariableName %>DtoResultList") {
            @Override
            @SuppressWarnings("unchecked")
            public boolean matches(Object actual) {
                boolean matches = true;
                final var currentPagination = (PaginationResult<<%- preffixClassName %>Dto>) actual;

                matches &= currentPagination.getPageNumber() == currentPage + 1;
                matches &= currentPagination.getPageNumberZeroBased() == currentPage;
                matches &= currentPagination.getPageSize() == elementsPerPage;
                matches &= currentPagination.getTotalResult() == totalElements;
                matches &= currentPagination.getOffset() == currentPage * elementsPerPage;

                for (var current<%- preffixClassName %>Dto : currentPagination.getResult()) {
                    matches &= dtoList.stream().anyMatch(dto -> dto.getName().equals(current<%- preffixClassName %>Dto.getName()));
                }
                return matches;
            }
        });
    }

    @Test
    void findByIdTest() {
        final var <%- preffixVariableName %>Dto = <%- preffixClassName %>DtoMotherObject.create();
<%_ if (__self__._isSoapService()) { _%>
        final var entityPersisted = this.<%- preffixVariableName %>Service.saveOrUpdate(Internal<%- preffixClassName %>Dto.from<%- preffixClassName %>Dto(<%- preffixVariableName %>Dto));
<%_ } else { _%>
        final var entityPersisted = this.<%- preffixVariableName %>Service.saveOrUpdate(<%- preffixVariableName %>Dto);
<%_ } _%>
        
        final var entitySearch = this.<%- preffixVariableName %>Service.findById(entityPersisted.getId(), <%- preffixClassName %>Dto.class);
        assertEquals(entitySearch.getId(), entityPersisted.getId());
        assertEquals(entitySearch.getName(), entityPersisted.getName());
        assertEquals(entitySearch.getType(), entityPersisted.getType());
    }

    @Test
    void saveTestConflictException() {
<%_ if (__self__._isSoapService()) { _%>
        final var <%- preffixVariableName %>Dto = Internal<%- preffixClassName %>Dto.from<%- preffixClassName %>Dto(<%- preffixClassName %>DtoMotherObject.create());
<%_ } else { _%>
        final var <%- preffixVariableName %>Dto = <%- preffixClassName %>DtoMotherObject.create();
<%_ } _%>

        this.<%- preffixVariableName %>Service.saveOrUpdate(<%- preffixVariableName %>Dto);
        assertThrows(ConflictException.class, () -> {
            this.<%- preffixVariableName %>Service.saveOrUpdate(<%- preffixVariableName %>Dto);
        });
    }

    @Test
    void updateTestSuccess() {
        final var <%- preffixVariableName %>Dto = Internal<%- preffixClassName %>Dto.from<%- preffixClassName %>Dto(<%- preffixClassName %>DtoMotherObject.create());;
        var <%- preffixVariableName %>Persisted = this.<%- preffixVariableName %>Service.saveOrUpdate(<%- preffixVariableName %>Dto);
<%_ if (__self__._isSoapService()) { _%>
        <%- preffixVariableName %>Persisted.setType(<%- preffixClassName %>TypeDto.ENUM_TYPE_1);
<%_ } else if (microserviceType === 'apifirst') { _%>
        <%- preffixVariableName %>Persisted.setType(<%- preffixClassName %>TypeDto._1);
<%_ } else if (microserviceType === 'codefirst' || microserviceType === 'none') { _%>
        <%- preffixVariableName %>Persisted.setType(<%- preffixClassName %>Dto.<%- preffixClassName %>TypeDto.ENUM_TYPE_1);
<%_ } else {
    throw new Error(`Unknown ${microserviceType}`);
} _%>
        <%- preffixVariableName %>Persisted = this.<%- preffixVariableName %>Service.saveOrUpdate(<%- preffixVariableName %>Persisted);
<%_ if (__self__._isSoapService()) { _%>
        assertEquals(<%- preffixClassName %>TypeDto.ENUM_TYPE_1, <%- preffixVariableName %>Persisted.getType());
<%_ } else if (microserviceType === 'apifirst') { _%>
        assertEquals(<%- preffixClassName %>TypeDto._1, <%- preffixVariableName %>Persisted.getType());
<%_ } else if (microserviceType === 'codefirst' || microserviceType === 'none') { _%>
        assertEquals(<%- preffixClassName %>Dto.<%- preffixClassName %>TypeDto.ENUM_TYPE_1, <%- preffixVariableName %>Persisted.getType());
<%_ } else {
    throw new Error(`Unknown ${microserviceType}`);
} _%>
    }

    @Test
    void updateTestConflictException() {
<%_ if (__self__._isSoapService()) { _%>
        final var first<%- preffixClassName %>Dto = Internal<%- preffixClassName %>Dto.from<%- preffixClassName %>Dto(<%- preffixClassName %>DtoMotherObject.create());    
<%_ } else { _%>
        final var first<%- preffixClassName %>Dto = <%- preffixClassName %>DtoMotherObject.create();
<%_ } _%>
        final var first<%- preffixClassName %>Persisted = this.<%- preffixVariableName %>Service.saveOrUpdate(first<%- preffixClassName %>Dto);
<%_ if (__self__._isSoapService()) { _%>
        final var second<%- preffixClassName %>Dto = Internal<%- preffixClassName %>Dto.from<%- preffixClassName %>Dto(<%- preffixClassName %>DtoMotherObject.create());
<%_ } else { _%>
        final var second<%- preffixClassName %>Dto = <%- preffixClassName %>DtoMotherObject.create();
<%_ } _%>
        final var second<%- preffixClassName %>Persisted = this.<%- preffixVariableName %>Service.saveOrUpdate(second<%- preffixClassName %>Dto);

        second<%- preffixClassName %>Persisted.setName(first<%- preffixClassName %>Persisted.getName());
        second<%- preffixClassName %>Persisted.setType(first<%- preffixClassName %>Persisted.getType());

        assertThrows(ConflictException.class, () -> {
            this.<%- preffixVariableName %>Service.saveOrUpdate(second<%- preffixClassName %>Persisted);
        });
    }

    @Test
    void deleteByIdTestSuccessfull() {
<%_ if (__self__._isSoapService()) { _%>
        final var <%- preffixVariableName %>Dto = Internal<%- preffixClassName %>Dto.from<%- preffixClassName %>Dto(<%- preffixClassName %>DtoMotherObject.create());
<%_ } else { _%>
        final var <%- preffixVariableName %>Dto = <%- preffixClassName %>DtoMotherObject.create();
<%_ } _%>
        var <%- preffixVariableName %>Persisted = this.<%- preffixVariableName %>Service.saveOrUpdate(<%- preffixVariableName %>Dto);
        assertEquals(1L, this.<%- preffixVariableName %>Service.countAll());

        this.<%- preffixVariableName %>Service.deleteById(<%- preffixVariableName %>Persisted.getId());
        assertEquals(0L, this.<%- preffixVariableName %>Service.countAll());
    }

    @Test
    void deleteByIdTestNotFound() {
        final var randomId = faker.random().nextLong();
        assertThrows(NotFoundException.class, () -> this.<%- preffixVariableName %>Service.deleteById(randomId));
    }

    @Configuration
    static class TestConfig {
        @Bean
        public <%- preffixClassName %>Service <%- preffixVariableName %>Service(ModelMapperFactory modelMapperFactory, 
<%_ if (__self__._isSoapService()) { _%>
                        DateToXMLGregorianCalendarConverter dateToXMLGregorianCalendarConverter,
<%_ } else { _%>
                        DateToOffsetDateTimeConverter dateToOffsetDateTimeConverter, 
                        OffsetDateTimeToDateConverter offsetDateTimeToDateConverter, 
<%_ } _%>
                        <%- preffixClassName %>Repository <%- preffixVariableName %>Repository) {
            modelMapperFactory.registryCustomMapper(new PropertyMap<<%- preffixClassName %>, <%- preffixClassName %>Dto>() {
                @Override
                protected void configure() {
                    using(<%- __self__._isSoapService() ? 'dateToXMLGregorianCalendarConverter' : 'dateToOffsetDateTimeConverter' %>).map(source.getCreationDate(), destination.getCreationDate());
                    using(<%- __self__._isSoapService() ? 'dateToXMLGregorianCalendarConverter' : 'dateToOffsetDateTimeConverter' %>).map(source.getDischargedDate(), destination.getDischargedDate());
                }
<%_ if (__self__._isSoapService() || microserviceType === 'apifirst') { _%>
            }).setPostConverter(new Converter<<%- preffixClassName %>, <%- preffixClassName %>Dto>() {
                @Override
                public <%- preffixClassName %>Dto convert(MappingContext<<%- preffixClassName %>, <%- preffixClassName %>Dto> context) {
                    if (context.getSource().getType() != null) {
<%_ if (__self__._isSoapService()) { _%>
                        context.getDestination().setType(<%- preffixClassName %>TypeDto.valueOf(context.getSource().getType().name()));
<%_ } else if (microserviceType === 'apifirst') { _%>
                        context.getDestination().setType(<%- preffixClassName %>TypeDto.fromValue(context.getSource().getType().toString()));
<%_ } _%>
                    }
                    return context.getDestination();
                }
<%_ } _%>
            });

            modelMapperFactory.registryCustomMapper(new PropertyMap<<%- __self__._isSoapService() ? 'Internal' : '' %><%- preffixClassName %>Dto, <%- preffixClassName %>>() {
                @Override
                protected void configure() {
<%_ if (microserviceType === 'apifirst') { _%>
                    using(offsetDateTimeToDateConverter).map(source.getCreationDate(), destination.getCreationDate());
                    using(offsetDateTimeToDateConverter).map(source.getDischargedDate(), destination.getDischargedDate());
<%_ } _%>
                }
<%_ if (__self__._isSoapService() || microserviceType === 'apifirst') { _%>
            }).setPostConverter(new Converter<<%- __self__._isSoapService() ? 'Internal' : '' %><%- preffixClassName %>Dto, <%- preffixClassName %>>() {
                @Override
                public <%- preffixClassName %> convert(MappingContext<<%- __self__._isSoapService() ? 'Internal' : '' %><%- preffixClassName %>Dto, <%- preffixClassName %>> context) {
                    if (context.getSource().getType() != null) {
<%_ if (__self__._isSoapService()) { _%>
                        context.getDestination().setType(<%- preffixClassName %>.<%- preffixClassName %>Type.valueOf(context.getSource().getType().name()));
<%_ } else if (microserviceType === 'apifirst') { _%>
                        context.getDestination().setType(<%- preffixClassName %>.<%- preffixClassName %>Type.fromValue(context.getSource().getType().getValue()));
<%_ } _%>
                        
                    }
                    return context.getDestination();
                }
<%_ } _%>
            });
            return new <%- preffixClassName %>Service(modelMapperFactory, <%- preffixVariableName %>Repository);
        }
    } 
}