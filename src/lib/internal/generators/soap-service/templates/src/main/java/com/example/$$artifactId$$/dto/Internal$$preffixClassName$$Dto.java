package com.example.<%- artifactId %>.dto;

import com.example.<%- artifactId %>.soap.api.<%- preffixClassName %>Dto;
import com.example.<%- artifactId %>.soap.api.<%- preffixClassName %>TypeDto;
import lombok.*;

import java.util.Date;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Internal<%- preffixClassName %>Dto {

    private Long id;
    private String name;
    private String description;
    private Date creationDate;
    private Date dischargedDate;
    private Double amount;
    private <%- preffixClassName %>TypeDto type;

    public static Internal<%- preffixClassName %>Dto from<%- preffixClassName %>Dto(<%- preffixClassName %>Dto <%- preffixVariableName %>Dto) {
        return Internal<%- preffixClassName %>Dto.builder().id(<%- preffixVariableName %>Dto.getId()).name(<%- preffixVariableName %>Dto.getName())
                .description(<%- preffixVariableName %>Dto.getDescription())
                .creationDate(<%- preffixVariableName %>Dto.getCreationDate() != null
                        ? <%- preffixVariableName %>Dto.getCreationDate().toGregorianCalendar().getTime() : null)
                .dischargedDate(<%- preffixVariableName %>Dto.getDischargedDate() != null
                        ? <%- preffixVariableName %>Dto.getDischargedDate().toGregorianCalendar().getTime() : null)
                .amount(<%- preffixVariableName %>Dto.getAmount())
                .type(<%- preffixVariableName %>Dto.getType())
                .build();
    }
}
