package com.example.<%- artifactId %>.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class <%- preffixClassName %>Constants {
<%_ if (microserviceType !== 'none' || __self__._isSoapService()) { _%>
    public static final Long DEFAULT_PAGE_SIZE = 7L;
<%_ } _%>
}
