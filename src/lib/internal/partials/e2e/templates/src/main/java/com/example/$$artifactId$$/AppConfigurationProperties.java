package com.example.<%- artifactId %>;

import lombok.Getter;
import lombok.Setter;
import com.example.<%- artifactId %>.utils.<%- preffixClassName %>Constants;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app", ignoreUnknownFields = true)
@Getter @Setter
public class AppConfigurationProperties {
<%_ if (microserviceType !== 'none' || __self__._isSoapService()) { _%>
    private Long defaultPageSize = <%- preffixClassName %>Constants.DEFAULT_PAGE_SIZE;
<%_ } _%>
}
