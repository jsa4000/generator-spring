package com.example.<%- artifactId %>.soap.config;
<%_ if(persistenceLayer){ _%>
import com.example.architecture.services.mapper.converters.impl.DateToXMLGregorianCalendarConverter;
import com.example.architecture.services.mapper.impl.ModelMapperFactory;
<%_ } _%>
import com.example.<%- artifactId %>.AppConfigurationProperties;
import com.example.<%- artifactId %>.services.impl.<%- preffixClassName %>Service;
import com.example.<%- artifactId %>.soap.impl.<%- preffixClassName %>WsImpl;
import org.apache.cxf.Bus;
import com.example.architecture.cxf.jaxws.CXFEndpointImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.ws.Endpoint;

@Configuration
public class CXFConfig {

    @Bean
    public Endpoint endpoint(@Qualifier(Bus.DEFAULT_BUS_ID) final Bus bus, final <%- preffixClassName %>WsImpl <%- preffixVariableName %>WsImpl) {
        final var endpoint = new CXFEndpointImpl(bus, <%- preffixVariableName %>WsImpl);
        endpoint.publish("/<%- preffixClassName %>WS");
        return endpoint;
    }

    @Bean
    public <%- preffixClassName %>WsImpl <%- preffixVariableName %>WsImpl(final AppConfigurationProperties appConfigurationProperties,
            <%_ if (persistenceLayer) { _%>
            final DateToXMLGregorianCalendarConverter dateToXMLGregorianCalendarConverter,
            final ModelMapperFactory modelMapperFactory, 
            <%_ } _%>
            final <%- preffixClassName %>Service <%- preffixVariableName %>Service) {
        return new <%- preffixClassName %>WsImpl(appConfigurationProperties<%_ if (persistenceLayer) { _%>, dateToXMLGregorianCalendarConverter, modelMapperFactory<%_ } _%>, <%- preffixVariableName %>Service);
    }
}
