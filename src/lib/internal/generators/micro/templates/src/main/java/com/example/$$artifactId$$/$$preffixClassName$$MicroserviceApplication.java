package com.example.<%- artifactId %>;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;
<%_ if(feignClient){ _%>
import org.springframework.cloud.openfeign.EnableFeignClients;
<%_ } _%>
<%_ if(persistenceLayer){ _%>
import com.example.architecture.jpa.impl.JpaDaoImpl;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;
<%_ } _%>
<%_ if(microserviceType !== 'apifirst' && (microserviceType !== 'none' || !persistenceLayer)){ _%>
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import com.example.<%- artifactId %>.dto.<%- preffixClassName %>Dto;
<%_ } _%>

@SpringBootApplication
@EnableConfigurationProperties(AppConfigurationProperties.class)
<%_ if(feignClient){ _%>
@EnableFeignClients(basePackages = "com.example.<%- artifactId %>.feign")
<%_ } _%>
<%_ if(persistenceLayer){ _%>
@EntityScan(basePackages = "com.example.<%- artifactId %>.dao.entities")
@EnableJpaRepositories(basePackages = "com.example.<%- artifactId %>.dao.repositories", repositoryBaseClass = JpaDaoImpl.class)
<%_ } _%>
public class <%- preffixClassName %>MicroserviceApplication {

<%_ if((microserviceType !== 'apifirst' && (microserviceType !== 'none' || !persistenceLayer))){ _%>
    @Bean
    public Converter<<%- preffixClassName %>Dto.<%- preffixClassName %>TypeDto, String> <%- preffixVariableName %>TypeDtoStringConverter() {
        return new Converter<<%- preffixClassName %>Dto.<%- preffixClassName %>TypeDto, String>() {
            @Override
            public String convert(<%- preffixClassName %>Dto.<%- preffixClassName %>TypeDto source) {
                return source != null ? source.getValue() : null;
            }
        };
    }
    @Bean
    public Converter<String, <%- preffixClassName %>Dto.<%- preffixClassName %>TypeDto> string<%- preffixClassName %>TypeDtoConverter() {
        return new Converter<String, <%- preffixClassName %>Dto.<%- preffixClassName %>TypeDto>() {
            @Override
            public <%- preffixClassName %>Dto.<%- preffixClassName %>TypeDto convert(String source) {
                return source != null ? <%- preffixClassName %>Dto.<%- preffixClassName %>TypeDto.fromValue(source) : null;
            }
        };
    }
<%_ } _%>

    public static void main(String[] args) {
        SpringApplication.run(<%- preffixClassName %>MicroserviceApplication.class, args);
    }
}
