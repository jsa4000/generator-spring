package com.example.<%- artifactId %>.controllers.delegates;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.example.<%- artifactId %>.controllers.<%- preffixClassName %>ApiController;
<%_ if (persistenceLayer) { _%>
import com.example.architecture.services.mapper.converters.impl.DateToOffsetDateTimeConverter;
import com.example.architecture.services.mapper.converters.impl.OffsetDateTimeToDateConverter;
import com.example.architecture.services.mapper.impl.ModelMapperFactory;
import org.springframework.data.domain.Pageable;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeMap;
<%_ } _%>
import com.example.<%- artifactId %>.dto.PaginationResult<%- preffixClassName %>Dto;
import com.github.javafaker.Faker;
import com.example.<%- artifactId %>.AppConfigurationProperties;
import com.example.<%- artifactId %>.dto.<%- preffixClassName %>Dto;
import com.example.<%- artifactId %>.services.impl.<%- preffixClassName %>Service;
import com.example.<%- artifactId %>.tests.helpers.motherobjects.<%- preffixClassName %>DtoMotherObject;
import org.hamcrest.CustomMatcher;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(<%- preffixClassName %>ApiController.class)
@Import(<%- preffixClassName %>DelegateTest.DefaultConfigWithoutCsrf.class)
@ContextConfiguration(classes = {<%- preffixClassName %>DelegateTest.TestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@WithMockUser(username = "fakeuser", roles = {"FAKE"})
class <%- preffixClassName %>DelegateTest {

    private static Faker faker = new Faker();

    @Autowired
    private AppConfigurationProperties appConfigurationProperties;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
<%_ if (persistenceLayer) { _%>
    @MockBean
    private DateToOffsetDateTimeConverter dateToOffsetDateTimeConverter;
    @MockBean
    private OffsetDateTimeToDateConverter offsetDateTimeToDateConverter;
<%_ } _%>
    @MockBean
    private <%- preffixClassName %>Service <%- preffixVariableName %>Service;

    @Test
    void find<%- preffixClassName %>PageTest() throws Exception {
        final var page<%- preffixClassName %> = <%- preffixClassName %>DtoMotherObject.paginationResult(appConfigurationProperties.getDefaultPageSize().intValue(), 
                <%- preffixClassName %>DtoMotherObject.PaginationResultOptions.builder().withId(true).build());
<%_ if (persistenceLayer) { _%>
        when(this.<%- preffixVariableName %>Service.findByExamplePage(any(), any(Pageable.class), eq(<%- preffixClassName %>Dto.class))).thenReturn(page<%- preffixClassName %>);
<%_ } else { _%>
        when(this.<%- preffixVariableName %>Service.find<%- preffixClassName %>Page(isNull(), isNull(), isNull(), isNull(), anyLong(), anyLong())).thenReturn(page<%- preffixClassName %>);
<%_ } _%>
        final var mockMvcResult = mvc.perform(get("/api/<%- projectName %>").accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        final PaginationResult<%- preffixClassName %>Dto apiResultList = this.objectMapper.readValue(
                mockMvcResult.getResponse().getContentAsByteArray(), new TypeReference<PaginationResult<%- preffixClassName %>Dto>() {});
        
<%_ if (persistenceLayer) { _%>
        verify(this.<%- preffixVariableName %>Service).findByExamplePage(any(), any(Pageable.class), eq(<%- preffixClassName %>Dto.class));
<%_ } else { _%>
        verify(this.<%- preffixVariableName %>Service).find<%- preffixClassName %>Page(isNull(), isNull(), isNull(), isNull(), anyLong(), anyLong());
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
                        matches &= paginationResult.getTotal().equals(page<%- preffixClassName %>.getTotalResult());
                        matches &= paginationResult.getOffset() == page<%- preffixClassName %>.getOffset();
<%_ } else { _%>
                        matches &= paginationResult.getPageNumber().equals(page<%- preffixClassName %>.getPageNumber());
                        matches &= paginationResult.getPageNumber().equals(page<%- preffixClassName %>.getPageNumber());
                        matches &= paginationResult.getPageSize().equals(page<%- preffixClassName %>.getPageSize());
                        matches &= paginationResult.getTotal().equals(page<%- preffixClassName %>.getTotal());
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
        final var mockMvcResult = mvc.perform(get("/api/<%- projectName %>/{id}",
                <%- preffixVariableName %>Dto.getId()).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        final var resultDto = this.objectMapper.readValue(mockMvcResult.getResponse().getContentAsByteArray(), <%- preffixClassName %>Dto.class);
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
        final var <%- preffixVariableName %>DtoPersisted = <%- preffixClassName %>DtoMotherObject.copy(<%- preffixVariableName %>Dto);
        <%- preffixVariableName %>DtoPersisted.setId(faker.random().nextLong());
        when(this.<%- preffixVariableName %>Service.saveOrUpdate(any(<%- preffixClassName %>Dto.class))).thenReturn(<%- preffixVariableName %>DtoPersisted);
<%_ } else { _%>
        final var <%- preffixVariableName %>Id = faker.random().nextLong();
        when(this.<%- preffixVariableName %>Service.create<%- preffixClassName %>(any(<%- preffixClassName %>Dto.class))).thenReturn(<%- preffixVariableName %>Id);
<%_ } _%>

        mvc.perform(post("/api/<%- projectName %>").contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsBytes(<%- preffixVariableName %>Dto))).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", new CustomMatcher<String>("locationHeaderMatcher") {
                    @Override
                    public boolean matches(Object actual) {
<%_ if (persistenceLayer) { _%>
                        return actual.toString().endsWith("/api/<%- projectName %>/" + <%- preffixVariableName %>DtoPersisted.getId());
<%_ } else { _%>
                        return actual.toString().endsWith("/api/<%- projectName %>/" + <%- preffixVariableName %>Id);
<%_ } _%>
                    }
                }));
<%_ if (persistenceLayer) { _%>
        verify(this.<%- preffixVariableName %>Service).saveOrUpdate(any(<%- preffixClassName %>Dto.class));
<%_ } else { _%>
        verify(this.<%- preffixVariableName %>Service).create<%- preffixClassName %>(any(<%- preffixClassName %>Dto.class));
<%_ } _%>
        
    }

    @Test
    void update<%- preffixClassName %>ByIdTest() throws Exception {
        final var <%- preffixVariableName %>Dto = <%- preffixClassName %>DtoMotherObject.withId();
        mvc.perform(put("/api/<%- projectName %>/{id}", <%- preffixVariableName %>Dto.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(<%- preffixVariableName %>Dto))).andDo(print())
                .andExpect(status().isNoContent());
<%_ if (persistenceLayer) { _%>
        verify(this.<%- preffixVariableName %>Service).saveOrUpdate(any(<%- preffixClassName %>Dto.class));
<%_ } else { _%>
        verify(this.<%- preffixVariableName %>Service).update<%- preffixClassName %>ById(eq(<%- preffixVariableName %>Dto.getId()), any(<%- preffixClassName %>Dto.class));
<%_ } _%>
    }

    @Test
    void delete<%- preffixClassName %>ByIdTest() throws Exception {
        final var randomId = Integer.valueOf(faker.random().nextInt(1, Integer.MAX_VALUE)).longValue();
        mvc.perform(delete("/api/<%- projectName %>/{id}", randomId).contentType(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isNoContent());
<%_ if (persistenceLayer) { _%>
        verify(this.<%- preffixVariableName %>Service).deleteById(randomId);
<%_ } else { _%>
        verify(this.<%- preffixVariableName %>Service).delete<%- preffixClassName %>ById(randomId);
<%_ } _%>
        
    }

    @Configuration
    static class TestConfig {
        @Bean
        public AppConfigurationProperties appConfigurationProperties() {
            return new AppConfigurationProperties();
        }
        @Bean
        public <%- preffixClassName %>ApiController <%- preffixVariableName %>ApiController(<%- preffixClassName %>Delegate <%- preffixVariableName %>Delegate) {
            return new <%- preffixClassName %>ApiController(<%- preffixVariableName %>Delegate);
        }
        @Bean
        public <%- preffixClassName %>Delegate <%- preffixVariableName %>Delegate(AppConfigurationProperties appConfigurationProperties,
<%_ if (persistenceLayer) { _%>
                                                 DateToOffsetDateTimeConverter dateToOffsetDateTimeConverter,
                                                 OffsetDateTimeToDateConverter offsetDateTimeToDateConverter,
                                                 ModelMapperFactory modelMapperFactory,
<%_ } _%>
                                                 <%- preffixClassName %>Service <%- preffixVariableName %>Service) {
            return new <%- preffixClassName %>Delegate(appConfigurationProperties, <%_ if (persistenceLayer) { _%>dateToOffsetDateTimeConverter, offsetDateTimeToDateConverter, modelMapperFactory,<%_ } _%> <%- preffixVariableName %>Service);
        }
<%_ if (persistenceLayer) { _%>
        @Bean
        public ModelMapperFactory modelMapperFactory() {
            final var modelMapperFactoryMock = mock(ModelMapperFactory.class);
            when(modelMapperFactoryMock.registryCustomMapper(any(PropertyMap.class))).thenReturn(mock(TypeMap.class));
            return modelMapperFactoryMock;
        }
<%_ } _%>
        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .registerModules(new JavaTimeModule());
        }
    }

    @Configuration
    static class DefaultConfigWithoutCsrf extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(final HttpSecurity http) throws Exception {
            super.configure(http);
            http.csrf().disable();
        }
    }
}