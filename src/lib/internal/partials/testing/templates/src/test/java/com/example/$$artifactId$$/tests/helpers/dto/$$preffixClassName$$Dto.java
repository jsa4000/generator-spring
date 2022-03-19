package com.example.<%- artifactId %>.tests.helpers.dto;

import lombok.*;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class <%- preffixClassName %>Dto {

     private Long id;
     private OffsetDateTime creationDate;
     private OffsetDateTime dischargedDate;
     @NotBlank
     private String name;
     private String description;
     @NotNull
     @Min(1)
     private Double amount;
     @NotNull
     private <%- preffixClassName %>TypeDto type;

     public enum <%- preffixClassName %>TypeDto {
        ENUM_TYPE_1("Enum-Type-1"), 

        ENUM_TYPE_2("Enum-Type-2"),

        ENUM_TYPE_3("Enum-Type-3");

        private String value;

        <%- preffixClassName %>TypeDto(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }        

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static <%- preffixClassName %>TypeDto fromValue(String value) {
            for (<%- preffixClassName %>TypeDto current : <%- preffixClassName %>TypeDto.values()) {
                if (current.value.equals(value)) {
                    return current;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
     }
}
