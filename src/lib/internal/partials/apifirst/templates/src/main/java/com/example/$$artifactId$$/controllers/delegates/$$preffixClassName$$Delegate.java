package com.example.<%- artifactId %>.controllers.delegates;

import lombok.RequiredArgsConstructor;
<%_ if (persistenceLayer) { _%>
import com.example.architecture.services.mapper.converters.impl.DateToOffsetDateTimeConverter;
import com.example.architecture.services.mapper.converters.impl.OffsetDateTimeToDateConverter;
import com.example.architecture.services.mapper.impl.ModelMapperFactory;
import com.example.<%- artifactId %>.dao.entities.<%- preffixClassName %>;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import javax.annotation.PostConstruct;
<%_ } _%>
import org.apache.commons.lang3.ObjectUtils;
import com.example.<%- artifactId %>.AppConfigurationProperties;
import com.example.<%- artifactId %>.controllers.<%- preffixClassName %>ApiDelegate;
import com.example.<%- artifactId %>.dto.PaginationResult<%- preffixClassName %>Dto;
import com.example.<%- artifactId %>.dto.<%- preffixClassName %>Dto;
import com.example.<%- artifactId %>.dto.<%- preffixClassName %>TypeDto;
import com.example.<%- artifactId %>.services.impl.<%- preffixClassName %>Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
@RequiredArgsConstructor
public class <%- preffixClassName %>Delegate implements <%- preffixClassName %>ApiDelegate {

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
        }).setPostConverter(new Converter<<%- preffixClassName %>, <%- preffixClassName %>Dto>() {
                @Override
                public <%- preffixClassName %>Dto convert(MappingContext<<%- preffixClassName %>, <%- preffixClassName %>Dto> context) {
                    if (context.getSource().getType() != null) {
                        context.getDestination().setType(<%- preffixClassName %>TypeDto.fromValue(context.getSource().getType().toString()));
                    }
                    return context.getDestination();
                }
        });

        modelMapperFactory.registryCustomMapper(new PropertyMap<<%- preffixClassName %>Dto, <%- preffixClassName %>>() {
            @Override
            protected void configure() {
                using(offsetDateTimeToDateConverter).map(source.getCreationDate(), destination.getCreationDate());
                using(offsetDateTimeToDateConverter).map(source.getDischargedDate(), destination.getDischargedDate());
            }
        }).setPostConverter(new Converter<<%- preffixClassName %>Dto, <%- preffixClassName %>>() {
                @Override
                public <%- preffixClassName %> convert(MappingContext<<%- preffixClassName %>Dto, <%- preffixClassName %>> context) {
                    if (context.getSource().getType() != null) {
                        context.getDestination().setType(<%- preffixClassName %>.<%- preffixClassName %>Type.fromValue(context.getSource().getType().getValue()));
                    }
                    return context.getDestination();
                }
        });
    }
<%_ } _%>

    @Override
    public ResponseEntity<PaginationResult<%- preffixClassName %>Dto> find<%- preffixClassName %>Page(String name, String description, Double amount, <%- preffixClassName %>TypeDto type, Long pageNumber, Long pageSize) {
        pageNumber = ObjectUtils.defaultIfNull(pageNumber, 0L);
        pageSize = ObjectUtils.defaultIfNull(pageSize, appConfigurationProperties.getDefaultPageSize());
        <%_ if (persistenceLayer) { _%>
        final var pageResult = this.<%- preffixVariableName %>Service.findByExamplePage(Example.of(<%- preffixClassName %>.builder()
                        .name(name)
                        .description(description)
                        .amount(amount)
                        .type(type != null ? <%- preffixClassName %>.<%- preffixClassName %>Type.fromValue(type.toString()) : null)
                        .build(), ExampleMatcher.matchingAll().withIgnoreCase()
                        .withIgnoreNullValues().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)),
                PageRequest.of(pageNumber.intValue(), pageSize.intValue()), <%- preffixClassName %>Dto.class);
        return ResponseEntity.ok(new PaginationResult<%- preffixClassName %>Dto().result(pageResult.getResult())
                .pageNumber(Integer.valueOf(pageResult.getPageNumber()).longValue())
                .pageSize(Integer.valueOf(pageResult.getPageSize()).longValue())
                .offset(Integer.valueOf(pageResult.getOffset()).longValue())
                .total(pageResult.getTotalResult()));
        <%_ } else { _%>
        return ResponseEntity.ok(this.<%- preffixVariableName %>Service.find<%- preffixClassName %>Page(name, description, amount, type, pageNumber, pageSize));
        <%_ } _%>
    }

    @Override
    public ResponseEntity<<%- preffixClassName %>Dto> find<%- preffixClassName %>ById(Long id) {
        <%_ if (persistenceLayer) { _%>
            return ResponseEntity.ok(this.<%- preffixVariableName %>Service.findById(id, <%- preffixClassName %>Dto.class));
        <%_ } else { _%>
            return ResponseEntity.ok(this.<%- preffixVariableName %>Service.find<%- preffixClassName %>ById(id));
        <%_ } _%>
    }

    @Override
    public ResponseEntity<Void> create<%- preffixClassName %>(<%- preffixClassName %>Dto <%- preffixVariableName %>Dto) {
        <%- preffixVariableName %>Dto.setId(null);
        <%_ if (persistenceLayer) { _%>
        final var <%- preffixVariableName %>CreatedId = this.<%- preffixVariableName %>Service.saveOrUpdate(<%- preffixVariableName %>Dto).getId();
        <%_ } else { _%>
        final var <%- preffixVariableName %>CreatedId = this.<%- preffixVariableName %>Service.create<%- preffixClassName %>(<%- preffixVariableName %>Dto);
        <%_ } _%>
        final var locationUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/<%- projectName %>/{id}").build(<%- preffixVariableName %>CreatedId);
        return ResponseEntity.created(locationUri).build();
    }

    @Override
    public ResponseEntity<Void> update<%- preffixClassName %>ById(Long id, <%- preffixClassName %>Dto <%- preffixVariableName %>Dto) {
        <%_ if (persistenceLayer) { _%>
        <%- preffixVariableName %>Dto.setId(id);
        this.<%- preffixVariableName %>Service.saveOrUpdate(<%- preffixVariableName %>Dto);
        <%_ } else { _%>
        this.<%- preffixVariableName %>Service.update<%- preffixClassName %>ById(id, <%- preffixVariableName %>Dto);
        <%_ } _%>
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> delete<%- preffixClassName %>ById(Long id) {
        <%_ if (persistenceLayer) { _%>
        this.<%- preffixVariableName %>Service.deleteById(id);
        <%_ } else { _%>
        this.<%- preffixVariableName %>Service.delete<%- preffixClassName %>ById(id);
        <%_ } _%>
        return ResponseEntity.noContent().build();
    }
}