package com.example.<%- artifactId %>.services.impl;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
<%_ if (persistenceLayer) { _%>
import org.springframework.boot.test.mock.mockito.MockBean;
import com.example.architecture.core.exceptions.ConflictException;
import com.example.architecture.core.exceptions.NotFoundException;
import com.example.architecture.services.mapper.api.MapperFactory;
import com.example.architecture.services.vo.PaginationResult;
import com.example.<%- artifactId %>.dao.repositories.<%- preffixClassName %>Repository;
import com.example.<%- artifactId %>.dao.entities.<%- preffixClassName %>;
    <%_ if (__self__._isSoapService()) { _%>
import com.example.<%- artifactId %>.soap.api.<%- preffixClassName %>Dto;
    <%_ } else if (microserviceType === 'none' && !!persistenceLayer) { _%>
import com.example.<%- artifactId %>.tests.helpers.dto.<%- preffixClassName %>Dto;
    <%_ } else { _%>
import com.example.<%- artifactId %>.dto.<%- preffixClassName %>Dto;
    <%_ } _%>
import com.example.<%- artifactId %>.tests.helpers.motherobjects.<%- preffixClassName %>MotherObject;
import org.hamcrest.CustomMatcher;
import org.hamcrest.MatcherAssert;
import org.springframework.data.domain.*;
import java.util.Optional;

import static org.mockito.Mockito.*;
<%_ } _%>
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        classes = <%- preffixClassName %>ServiceTest.TestConfig.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class <%- preffixClassName %>ServiceTest {

    private static final Faker faker = new Faker();

    @Autowired
    private <%- preffixClassName %>Service <%- preffixVariableName %>Service;
<%_ if (persistenceLayer) { _%>
    @MockBean
    private <%- preffixClassName %>Repository <%- preffixVariableName %>Repository;

    @MockBean
    private MapperFactory mapperFactory;

    @Test
    void save<%- preffixClassName %>TestSuccess() {
        final var <%- preffixVariableName %> = <%- preffixClassName %>MotherObject.create();
        final var <%- preffixVariableName %>Persisted = <%- preffixClassName %>MotherObject.withId(<%- preffixVariableName %>);

        when(this.mapperFactory.map(any(<%- preffixClassName %>Dto.class), eq(<%- preffixClassName %>.class))).thenReturn(<%- preffixVariableName %>);
        when(this.<%- preffixVariableName %>Repository.saveAndFlush(any(<%- preffixClassName %>.class))).thenReturn(<%- preffixVariableName %>Persisted);
        when(this.mapperFactory.map(any(<%- preffixClassName %>.class), eq(<%- preffixClassName %>Dto.class))).thenReturn(<%- preffixClassName %>MotherObject.to<%- preffixClassName %>Dto(<%- preffixVariableName %>Persisted));

        final var result = this.<%- preffixVariableName %>Service.saveOrUpdate(<%- preffixClassName %>MotherObject.to<%- preffixClassName %>Dto(<%- preffixVariableName %>));

        verify(this.mapperFactory).map(any(<%- preffixClassName %>Dto.class), eq(<%- preffixClassName %>.class));
        verify(this.<%- preffixVariableName %>Repository).saveAndFlush(any(<%- preffixClassName %>.class));
        verify(this.mapperFactory).map(any(<%- preffixClassName %>.class), eq(<%- preffixClassName %>Dto.class));

        assertNotNull(result.getId());
        assertEquals(<%- preffixVariableName %>.getName(), result.getName());
        assertEquals(<%- preffixVariableName %>.getDescription(), result.getDescription());
    }

    @Test
    @SuppressWarnings("unchecked")
    void findByExamplePageTest() {
        final var currentPage = faker.random().nextInt(1, 2);
        final var elementsPerPage = faker.random().nextInt(1, 5);
        final var totalElements = faker.random().nextInt(10, 20);
        final var entityList = <%- preffixClassName %>MotherObject.list(totalElements);

        final var pageRequest = PageRequest.of(currentPage, elementsPerPage);
        when(<%- preffixVariableName %>Repository.findAll(any(Example.class), any(Pageable.class))).thenReturn(new PageImpl<>(entityList, pageRequest, totalElements));
        when(mapperFactory.map(any(<%- preffixClassName %>.class), eq(<%- preffixClassName %>Dto.class))).thenAnswer((answer) -> {
            final var entity = answer.getArgument(0, <%- preffixClassName %>.class);
            return <%- preffixClassName %>MotherObject.to<%- preffixClassName %>Dto(entity);
        });

        final var paginationResult = this.<%- preffixVariableName %>Service.findByExamplePage(Example.of(<%- preffixClassName %>.builder().build(), ExampleMatcher.matchingAll().withIgnoreCase().withIgnoreNullValues().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)), pageRequest, <%- preffixClassName %>Dto.class);

        verify(<%- preffixVariableName %>Repository).findAll(any(Example.class), any(Pageable.class));
        verify(mapperFactory, times(entityList.size())).map(any(<%- preffixClassName %>.class), eq(<%- preffixClassName %>Dto.class));

        MatcherAssert.assertThat(paginationResult, new CustomMatcher<PaginationResult<<%- preffixClassName %>Dto>>("paginationResult") {
            @Override
            public boolean matches(final Object actual) {
                boolean matches = true;
                final var currentPagination = (PaginationResult<<%- preffixClassName %>Dto>) actual;
                matches &= currentPagination.getPageNumber() == currentPage + 1;
                matches &= currentPagination.getPageNumberZeroBased() == currentPage;
                matches &= currentPagination.getPageSize() == elementsPerPage;
                matches &= currentPagination.getTotalResult() == totalElements;
                matches &= currentPagination.getOffset() == currentPage * elementsPerPage;
                for (final var currentCivilServantDto : currentPagination.getResult()) {
                    matches &= entityList.stream().anyMatch(dto -> dto.getName().equals(currentCivilServantDto.getName()));
                }
                return matches;
            }
        });
    }

    @Test
    void findByIdTest() {
        final var <%- preffixVariableName %> = <%- preffixClassName %>MotherObject.withId();
        final var <%- preffixVariableName %>Dto = <%- preffixClassName %>MotherObject.to<%- preffixClassName %>Dto(<%- preffixVariableName %>);

        when(this.<%- preffixVariableName %>Repository.findById(<%- preffixVariableName %>.getId())).thenReturn(Optional.of(<%- preffixVariableName %>));
        when(this.mapperFactory.map(any(<%- preffixClassName %>.class), eq(<%- preffixClassName %>Dto.class))).thenReturn(<%- preffixVariableName %>Dto);

        final var <%- preffixVariableName %>Found = this.<%- preffixVariableName %>Service.findById(<%- preffixVariableName %>.getId(), <%- preffixClassName %>Dto.class);

        verify(this.<%- preffixVariableName %>Repository).findById(<%- preffixVariableName %>.getId());
        verify(this.mapperFactory).map(any(<%- preffixClassName %>.class), eq(<%- preffixClassName %>Dto.class));

        assertEquals(<%- preffixVariableName %>Found.getId(), <%- preffixVariableName %>Dto.getId());
        assertEquals(<%- preffixVariableName %>Found.getName(), <%- preffixVariableName %>Dto.getName());
        assertEquals(<%- preffixVariableName %>Found.getType(), <%- preffixVariableName %>Dto.getType());
    }

    @Test
    void saveTestConflictException() {
        final var <%- preffixVariableName %> = <%- preffixClassName %>MotherObject.create();
        final var <%- preffixVariableName %>Dto = <%- preffixClassName %>MotherObject.to<%- preffixClassName %>Dto(<%- preffixVariableName %>);
        when(this.mapperFactory.map(any(<%- preffixClassName %>Dto.class), eq(<%- preffixClassName %>.class))).thenReturn(<%- preffixVariableName %>);
        when(this.<%- preffixVariableName %>Repository.countByNameAndType(<%- preffixVariableName %>.getName(), <%- preffixVariableName %>.getType())).thenThrow(ConflictException.class);
        assertThrows(ConflictException.class, () -> {
            this.<%- preffixVariableName %>Service.saveOrUpdate(<%- preffixVariableName %>Dto);
        });
        verify(this.mapperFactory).map(any(<%- preffixClassName %>Dto.class), eq(<%- preffixClassName %>.class));
        verify(this.<%- preffixVariableName %>Repository).countByNameAndType(<%- preffixVariableName %>.getName(), <%- preffixVariableName %>.getType());
    }


    @Test
    void updateTestConflictException() {
        final var <%- preffixVariableName %> = <%- preffixClassName %>MotherObject.withId();
        final var <%- preffixVariableName %>Dto = <%- preffixClassName %>MotherObject.to<%- preffixClassName %>Dto(<%- preffixVariableName %>);
        when(this.mapperFactory.map(any(<%- preffixClassName %>Dto.class), eq(<%- preffixClassName %>.class))).thenReturn(<%- preffixVariableName %>);
        when(this.<%- preffixVariableName %>Repository.countByNameAndTypeAndDistinctId(<%- preffixVariableName %>.getName(), <%- preffixVariableName %>.getType(), <%- preffixVariableName %>.getId())).thenThrow(ConflictException.class);
        assertThrows(ConflictException.class, () -> {
            this.<%- preffixVariableName %>Service.saveOrUpdate(<%- preffixVariableName %>Dto);
        });
        verify(this.mapperFactory).map(any(<%- preffixClassName %>Dto.class), eq(<%- preffixClassName %>.class));
        verify(this.<%- preffixVariableName %>Repository).countByNameAndTypeAndDistinctId(<%- preffixVariableName %>.getName(), <%- preffixVariableName %>.getType(), <%- preffixVariableName %>.getId());
    }

    @Test
    void deleteByIdTestSuccessfull() {
        final var <%- preffixVariableName %> = <%- preffixClassName %>MotherObject.withId();
        this.<%- preffixVariableName %>Service.deleteById(<%- preffixVariableName %>.getId());
        verify(this.<%- preffixVariableName %>Repository).deleteById(<%- preffixVariableName %>.getId());
    }

    @Test
    void deleteByIdTestNotFound() {
        final var randomId = faker.random().nextLong();
        doThrow(NotFoundException.class).when(this.<%- preffixVariableName %>Repository).deleteById(randomId);
        assertThrows(NotFoundException.class, () -> this.<%- preffixVariableName %>Service.deleteById(randomId));
        verify(this.<%- preffixVariableName %>Repository).deleteById(randomId);
    }


<%_ } else { _%>

    @Test
    void find<%- preffixClassName %>PageTest() {
        fail();
    }

    @Test
    void find<%- preffixClassName %>ByIdTest() {
        fail();
    }

    @Test
    void create<%- preffixClassName %>() {
        fail();
    }

    @Test
    void update<%- preffixClassName %>ByIdTest() {
        fail();
    }

    @Test
    void delete<%- preffixClassName %>ById() {
        fail();
    }
<%_ } _%>

    @Configuration
    static class TestConfig {
        @Bean
        public <%- preffixClassName %>Service <%- preffixVariableName %>Service(<%_ if (persistenceLayer) { _%> MapperFactory mapperFactory, <%- preffixClassName %>Repository <%- preffixVariableName %>Repository <%_ } _%>) {
            return new <%- preffixClassName %>Service(<%_ if (persistenceLayer) { _%> mapperFactory, <%- preffixVariableName %>Repository <%_ } _%>);
        }
    }
}