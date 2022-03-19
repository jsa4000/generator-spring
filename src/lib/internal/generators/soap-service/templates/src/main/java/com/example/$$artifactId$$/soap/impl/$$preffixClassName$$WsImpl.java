package com.example.<%- artifactId %>.soap.impl;

<%_ if (persistenceLayer) { _%>
import com.example.architecture.services.mapper.converters.impl.DateToXMLGregorianCalendarConverter;
import com.example.architecture.services.mapper.impl.ModelMapperFactory;
import com.example.<%- artifactId %>.dao.entities.<%- preffixClassName %>;
import com.example.<%- artifactId %>.dto.Internal<%- preffixClassName %>Dto;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import javax.annotation.PostConstruct;
<%_ } _%>
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import com.example.<%- artifactId %>.AppConfigurationProperties;
import com.example.<%- artifactId %>.services.impl.<%- preffixClassName %>Service;
import com.example.<%- artifactId %>.soap.api.*;

import javax.jws.WebService;

@WebService(endpointInterface = "com.example.<%- artifactId %>.soap.api.<%- preffixClassName %>WsPortType")
@RequiredArgsConstructor
public class <%- preffixClassName %>WsImpl implements <%- preffixClassName %>WsPortType {

    private final AppConfigurationProperties appConfigurationProperties;
    <%_ if (persistenceLayer) { _%>
    private final DateToXMLGregorianCalendarConverter dateToXMLGregorianCalendarConverter;
    private final ModelMapperFactory modelMapperFactory;
<%_ } _%>
    private final <%- preffixClassName %>Service <%- preffixVariableName %>Service;

    <%_ if (persistenceLayer) { _%>
    @PostConstruct
    public void init() {
        modelMapperFactory.registryCustomMapper(new PropertyMap<<%- preffixClassName %>, <%- preffixClassName %>Dto>() {
            @Override
            protected void configure() {
                using(dateToXMLGregorianCalendarConverter).map(source.getCreationDate(), destination.getCreationDate());
                using(dateToXMLGregorianCalendarConverter).map(source.getDischargedDate(), destination.getDischargedDate());
            }
        }).setPostConverter(new Converter<<%- preffixClassName %>, <%- preffixClassName %>Dto>() {
                @Override
                public <%- preffixClassName %>Dto convert(MappingContext<<%- preffixClassName %>, <%- preffixClassName %>Dto> context) {
                    if (context.getSource().getType() != null) {
                        context.getDestination().setType(<%- preffixClassName %>TypeDto.valueOf(context.getSource().getType().name()));
                    }
                    return context.getDestination();
                }
        });

        modelMapperFactory.registryCustomMapper(new PropertyMap<Internal<%- preffixClassName %>Dto, <%- preffixClassName %>>() {
            @Override
            protected void configure() {
            }
        }).setPostConverter(new Converter<Internal<%- preffixClassName %>Dto, <%- preffixClassName %>>() {
                @Override
                public <%- preffixClassName %> convert(MappingContext<Internal<%- preffixClassName %>Dto, <%- preffixClassName %>> context) {
                    if (context.getSource().getType() != null) {
                        context.getDestination().setType(<%- preffixClassName %>.<%- preffixClassName %>Type.valueOf(context.getSource().getType().name()));
                    }
                    return context.getDestination();
                }
        });
    }
<%_ } _%>

    @Override
    public PaginationResult<%- preffixClassName %>Dto find<%- preffixClassName %>Page(String name, String description, Double amount, <%- preffixClassName %>TypeDto type, Long pageNumber, Long pageSize) {
        pageNumber = ObjectUtils.defaultIfNull(pageNumber, 0L);
        pageSize = ObjectUtils.defaultIfNull(pageSize, appConfigurationProperties.getDefaultPageSize());
        <%_ if (persistenceLayer) { _%>
        final var pageResult = this.<%- preffixVariableName %>Service.findByExamplePage(Example.of(<%- preffixClassName %>.builder()
                        .name(name)
                        .description(description)
                        .amount(amount)
                        .type(type != null ? <%- preffixClassName %>.<%- preffixClassName %>Type.valueOf(type.toString()) : null)
                        .build(), ExampleMatcher.matchingAll().withIgnoreCase()
                        .withIgnoreNullValues().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)),
                PageRequest.of(pageNumber.intValue(), pageSize.intValue()), <%- preffixClassName %>Dto.class);
        final var result = new PaginationResult<%- preffixClassName %>Dto();
        result.getResult().addAll(pageResult.getResult());
        result.setPageNumber(Integer.valueOf(pageResult.getPageNumber()).longValue());
        result.setPageSize(Integer.valueOf(pageResult.getPageSize()).longValue());
        result.setOffset(Integer.valueOf(pageResult.getOffset()).longValue());
        result.setTotal(pageResult.getTotalResult());
        return result;
        <%_ } else { _%>
        return <%- preffixVariableName %>Service.find<%- preffixClassName %>Page(name, description, amount, type, pageNumber, pageSize);
        <%_ } _%>
    }

    @Override
    public <%- preffixClassName %>Dto find<%- preffixClassName %>ById(long id) {
        <%_ if (persistenceLayer) { _%>
            return this.<%- preffixVariableName %>Service.findById(id, <%- preffixClassName %>Dto.class);
        <%_ } else { _%>
            return <%- preffixVariableName %>Service.find<%- preffixClassName %>ById(id);
        <%_ } _%>        
    }

    @Override
    public long create<%- preffixClassName %>(<%- preffixClassName %>Dto <%- preffixVariableName %>Dto) {
        <%_ if (persistenceLayer) { _%>
        <%- preffixVariableName %>Dto.setId(null);    
        return this.<%- preffixVariableName %>Service.saveOrUpdate(Internal<%- preffixClassName %>Dto.from<%- preffixClassName %>Dto(<%- preffixVariableName %>Dto)).getId();
        <%_ } else { _%>
        return <%- preffixVariableName %>Service.create<%- preffixClassName %>(<%- preffixVariableName %>Dto);    
        <%_ } _%>                
    }

    @Override
    public void update<%- preffixClassName %>ById(long id, <%- preffixClassName %>Dto <%- preffixVariableName %>Dto) {
        <%_ if (persistenceLayer) { _%>
        <%- preffixVariableName %>Dto.setId(id);
        this.<%- preffixVariableName %>Service.saveOrUpdate(Internal<%- preffixClassName %>Dto.from<%- preffixClassName %>Dto(<%- preffixVariableName %>Dto));
        <%_ } else { _%>
        <%- preffixVariableName %>Service.update<%- preffixClassName %>ById(id, <%- preffixVariableName %>Dto);
        <%_ } _%>        
    }

    @Override
    public void delete<%- preffixClassName %>ById(long id) {
        <%_ if (persistenceLayer) { _%>
        this.<%- preffixVariableName %>Service.deleteById(id);   
        <%_ } else { _%>
        <%- preffixVariableName %>Service.delete<%- preffixClassName %>ById(id);    
        <%_ } _%>        
    }
}