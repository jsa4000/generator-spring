package com.example.<%- artifactId %>.services;

<%_ if(feignClient){ _%>
import org.springframework.cloud.openfeign.EnableFeignClients;
<%_ } _%>
<%_ if(persistenceLayer){ _%>
import com.example.architecture.jpa.impl.JpaDaoImpl;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;
<%_ } _%>
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
<%_ if(feignClient){ _%>
@EnableFeignClients(basePackages = "com.example.<%- artifactId %>.feign")
<%_ } _%>
<%_ if(persistenceLayer){ _%>
@EntityScan(basePackages = "com.example.<%- artifactId %>.dao.entities")
@EnableJpaRepositories(basePackages = "com.example.<%- artifactId %>.dao.repositories", repositoryBaseClass = JpaDaoImpl.class)
<%_ } _%>
public class <%- preffixClassName %>ServicesConfiguration {
    
}
