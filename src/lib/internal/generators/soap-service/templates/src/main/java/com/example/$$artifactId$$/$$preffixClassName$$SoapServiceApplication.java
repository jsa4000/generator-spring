package com.example.<%- artifactId %>;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
<%_ if(feignClient){ _%>
import org.springframework.cloud.openfeign.EnableFeignClients;
<%_ } _%>
import com.example.<%- artifactId %>.soap.config.CXFConfig;
<%_ if(persistenceLayer){ _%>
import com.example.architecture.jpa.impl.JpaDaoImpl;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;
<%_ } _%>

@SpringBootApplication
@EnableConfigurationProperties(AppConfigurationProperties.class)
@Import(CXFConfig.class)
<%_ if(feignClient){ _%>
@EnableFeignClients(basePackages = "com.example.<%- artifactId %>.feign")
<%_ } _%>
<%_ if(persistenceLayer){ _%>
@EntityScan(basePackages = "com.example.<%- artifactId %>.dao.entities")
@EnableJpaRepositories(basePackages = "com.example.<%- artifactId %>.dao.repositories", repositoryBaseClass = JpaDaoImpl.class)
<%_ } _%>
public class <%- preffixClassName %>SoapServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(<%- preffixClassName %>SoapServiceApplication.class, args);
    }
}
