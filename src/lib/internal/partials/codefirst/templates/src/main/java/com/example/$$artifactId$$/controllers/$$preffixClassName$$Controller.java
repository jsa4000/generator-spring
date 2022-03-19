package com.example.<%- artifactId %>.controllers;

import lombok.RequiredArgsConstructor;
import com.example.architecture.restapi.utils.RestApiConstants;
<%_ if (persistenceLayer) { _%>
import com.example.architecture.services.mapper.converters.impl.DateToOffsetDateTimeConverter;
import com.example.architecture.services.mapper.converters.impl.OffsetDateTimeToDateConverter;
import com.example.architecture.services.mapper.impl.ModelMapperFactory;
import com.example.architecture.services.vo.PaginationResult;
import com.example.<%- artifactId %>.dao.entities.<%- preffixClassName %>;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.modelmapper.PropertyMap;
import javax.annotation.PostConstruct;
<%_ } else { _%>
import com.example.<%- artifactId %>.dto.PaginationResultDto;
<%_ } _%>
import org.apache.commons.lang3.ObjectUtils;
import com.example.<%- artifactId %>.AppConfigurationProperties;
import com.example.<%- artifactId %>.dto.<%- preffixClassName %>Dto;
import com.example.<%- artifactId %>.services.impl.<%- preffixClassName %>Service;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/api/<%- projectName %>")
@RequiredArgsConstructor
public class <%- preffixClassName %>Controller {

    private final AppConfigurationProperties appConfigurationProperties;
<%_ if (persistenceLayer) { _%>
    private final DateToOffsetDateTimeConverter dateToOffsetDateTimeConverter;
    private final OffsetDateTimeToDateConverter offsetDateTimeToDateConverter;
    private final ModelMapperFactory modelMapperFactory;
<%_ } _%>
    private final <%- preffixClassName %>Service <%- preffixVariableName %>Service;

<%_ if (persistenceLayer) { _%>
    @PostConstruct
    public void init() {
        modelMapperFactory.registryCustomMapper(new PropertyMap<<%- preffixClassName %>, <%- preffixClassName %>Dto>() {
            @Override
            protected void configure() {
                using(dateToOffsetDateTimeConverter).map(source.getCreationDate(), destination.getCreationDate());
                using(dateToOffsetDateTimeConverter).map(source.getDischargedDate(), destination.getDischargedDate());
            }
        });

        modelMapperFactory.registryCustomMapper(new PropertyMap<<%- preffixClassName %>Dto, <%- preffixClassName %>>() {
            @Override
            protected void configure() {
                using(offsetDateTimeToDateConverter).map(source.getCreationDate(), destination.getCreationDate());
                using(offsetDateTimeToDateConverter).map(source.getDischargedDate(), destination.getDischargedDate());
            }
        });
    }
<%_ } _%>

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Find <%- preffixClassName %> page",
            description = "Request for obtain pagination result of <%- preffixClassName %>'s", 
            security = { @SecurityRequirement(name = RestApiConstants.SecuritySchemes.BEARER_AUTH_SECURITY_SCHEME ) })
    public PaginationResult<%_ if (!persistenceLayer) { _%>Dto<%_ } _%><<%- preffixClassName %>Dto> find<%- preffixClassName %>Page(
            @RequestParam(name = "name", required=false) String name,
            @RequestParam(name = "description", required=false) String description,
            @RequestParam(name = "amount", required=false) @Valid @Min(1) Double amount,
            @RequestParam(name = "type", required=false) <%- preffixClassName %>Dto.<%- preffixClassName %>TypeDto type,
            @RequestParam(name = "pageNumber", required=false) Long pageNumber,
            @RequestParam(name = "pageSize", required = false) Long pageSize
    ) {
        pageNumber = ObjectUtils.defaultIfNull(pageNumber, 0L);
        pageSize = ObjectUtils.defaultIfNull(pageSize, appConfigurationProperties.getDefaultPageSize());
        <%_ if (persistenceLayer) { _%>
        return this.<%- preffixVariableName %>Service.findByExamplePage(Example.of(<%- preffixClassName %>.builder()
                        .name(name)
                        .description(description)
                        .amount(amount)
                        .type(type != null ? <%- preffixClassName %>.<%- preffixClassName %>Type.fromValue(type.toString()) : null)
                        .build(), ExampleMatcher.matchingAll().withIgnoreCase()
                        .withIgnoreNullValues().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)),
                PageRequest.of(pageNumber.intValue(), pageSize.intValue()), <%- preffixClassName %>Dto.class);
        <%_ } else { _%>
        return this.<%- preffixVariableName %>Service.find<%- preffixClassName %>Page(name, description, amount, type, pageNumber, pageSize);
        <%_ } _%>
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get <%- preffixClassName %> by id",
            description = "Get <%- preffixClassName %> by id", 
            security = { @SecurityRequirement(name = RestApiConstants.SecuritySchemes.BEARER_AUTH_SECURITY_SCHEME ) })
    public <%- preffixClassName %>Dto find<%- preffixClassName %>ById(@PathVariable(name = "id") Long id) {
        <%_ if (persistenceLayer) { _%>
            return this.<%- preffixVariableName %>Service.findById(id, <%- preffixClassName %>Dto.class);
        <%_ } else { _%>
            return this.<%- preffixVariableName %>Service.find<%- preffixClassName %>ById(id);
        <%_ } _%>
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new <%- preffixClassName %>",
            description = "Create a new <%- preffixClassName %>", 
            security = { @SecurityRequirement(name = RestApiConstants.SecuritySchemes.BEARER_AUTH_SECURITY_SCHEME ) })
    public void create<%- preffixClassName %>(@RequestBody @Valid <%- preffixClassName %>Dto <%- preffixVariableName %>Dto, HttpServletResponse response) {
        <%- preffixVariableName %>Dto.setId(null);
        <%_ if (persistenceLayer) { _%>
        final var <%- preffixVariableName %>CreatedId = this.<%- preffixVariableName %>Service.saveOrUpdate(<%- preffixVariableName %>Dto).getId();
        <%_ } else { _%>
        final var <%- preffixVariableName %>CreatedId = this.<%- preffixVariableName %>Service.create<%- preffixClassName %>(<%- preffixVariableName %>Dto);
        <%_ } _%>
        final var locationUri =  MvcUriComponentsBuilder
                .fromMethodCall(MvcUriComponentsBuilder
                        .on(this.getClass())
                        .find<%- preffixClassName %>ById(<%- preffixVariableName %>CreatedId))
                .build().toUri();
        response.setHeader(HttpHeaders.LOCATION, locationUri.toString());
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Modify a existing <%- preffixClassName %>",
            description = "Create a existing <%- preffixClassName %>", 
            security = { @SecurityRequirement(name = RestApiConstants.SecuritySchemes.BEARER_AUTH_SECURITY_SCHEME ) })
    public void update<%- preffixClassName %>ById(@PathVariable(name = "id") Long id, @RequestBody @Valid <%- preffixClassName %>Dto <%- preffixVariableName %>Dto) {
        <%- preffixVariableName %>Dto.setId(id);
        <%_ if (persistenceLayer) { _%>
        this.<%- preffixVariableName %>Service.saveOrUpdate(<%- preffixVariableName %>Dto);
        <%_ } else { _%>
        this.<%- preffixVariableName %>Service.update<%- preffixClassName %>ById(id, <%- preffixVariableName %>Dto);
        <%_ } _%>
    }

    @DeleteMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete a existing <%- preffixClassName %>",
            description = "Delete a existing <%- preffixClassName %>", 
            security = { @SecurityRequirement(name = RestApiConstants.SecuritySchemes.BEARER_AUTH_SECURITY_SCHEME ) })
    public void delete<%- preffixClassName %>ById(@PathVariable(name = "id") Long id) {
        <%_ if (persistenceLayer) { _%>
        this.<%- preffixVariableName %>Service.deleteById(id);
        <%_ } else { _%>
        this.<%- preffixVariableName %>Service.delete<%- preffixClassName %>ById(id);
        <%_ } _%>
    }
}