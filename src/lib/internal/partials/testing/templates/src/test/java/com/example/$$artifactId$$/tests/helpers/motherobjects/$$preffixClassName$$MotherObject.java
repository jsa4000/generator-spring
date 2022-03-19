package com.example.<%- artifactId %>.tests.helpers.motherobjects;

import com.github.javafaker.Faker;
import lombok.experimental.UtilityClass;

<%_ if (__self__._isSoapService()) { _%>
import lombok.SneakyThrows;
import javax.xml.datatype.DatatypeFactory;
import java.util.GregorianCalendar;
import com.example.<%- artifactId %>.soap.api.<%- preffixClassName %>Dto;
<%_ } else if (microserviceType === 'none' && !!persistenceLayer) { _%>
import com.example.<%- artifactId %>.tests.helpers.dto.<%- preffixClassName %>Dto;
<%_ } else { _%>
import com.example.<%- artifactId %>.dto.<%- preffixClassName %>Dto;
<%_ } _%>
<%_ if (__self__._isSoapService()) { _%>
import com.example.<%- artifactId %>.soap.api.<%- preffixClassName %>TypeDto;
<%_ } else if (microserviceType === 'apifirst') { _%>
import com.example.<%- artifactId %>.dto.<%- preffixClassName %>TypeDto;
<%_ } _%>
import com.example.<%- artifactId %>.dao.entities.<%- preffixClassName %>;

import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@UtilityClass
public class <%- preffixClassName %>MotherObject {

    private static Faker faker = new Faker();

    public static List<<%- preffixClassName %>> list(int size) {
        return IntStream.range(0, size).mapToObj((idx) -> create())
                .collect(Collectors.toList());
    }

    public static <%- preffixClassName %> withId() {
        return internalCreate().id(Integer.valueOf(faker.random().nextInt(1, Integer.MAX_VALUE)).longValue()).build();
    }

    public static <%- preffixClassName %> withId(<%- preffixClassName %> from) {
        return internalCopy(from).id(Integer.valueOf(faker.random().nextInt(1, Integer.MAX_VALUE)).longValue()).build();
    }

    public static <%- preffixClassName %> create() {
        return internalCreate().build();
    }

    public static <%- preffixClassName %> copy(<%- preffixClassName %> from) {
        return internalCopy(from).build();
    }
<%_ if (__self__._isSoapService()) { _%>
    @SneakyThrows
<%_ } _%>
    public static <%- preffixClassName %>Dto to<%- preffixClassName %>Dto(<%- preffixClassName %> <%- preffixVariableName %>) {
<%_ if (__self__._isSoapService()) { _%>
    final var <%- preffixVariableName %>Result = new <%- preffixClassName %>Dto();
    <%- preffixVariableName %>Result.setId(<%- preffixVariableName %>.getId());
    <%- preffixVariableName %>Result.setName(<%- preffixVariableName %>.getName());
    <%- preffixVariableName %>Result.setDescription(<%- preffixVariableName %>.getDescription());
    <%- preffixVariableName %>Result.setAmount(<%- preffixVariableName %>.getAmount());

    final var creationDateCalendar = new GregorianCalendar();
    creationDateCalendar.setTime(<%- preffixVariableName %>.getCreationDate());
    <%- preffixVariableName %>Result.setCreationDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(creationDateCalendar));

    if (<%- preffixVariableName %>.getDischargedDate() != null) {
        final var dischargedDateCalendar = new GregorianCalendar();
        dischargedDateCalendar.setTime(<%- preffixVariableName %>.getDischargedDate());
        <%- preffixVariableName %>Result.setDischargedDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(dischargedDateCalendar));
    }
    <%- preffixVariableName %>Result.setType(<%- preffixClassName %>TypeDto.valueOf(<%- preffixVariableName %>.getType().name()));
    return <%- preffixVariableName %>Result;
<%_ } else if (microserviceType === 'apifirst') { _%>
        return new <%- preffixClassName %>Dto().id(<%- preffixVariableName %>.getId()).name(<%- preffixVariableName %>.getName()).description(<%- preffixVariableName %>.getDescription())
                .amount(<%- preffixVariableName %>.getAmount()).creationDate(<%- preffixVariableName %>.getCreationDate().toInstant().atOffset(ZoneOffset.UTC))
                .dischargedDate(<%- preffixVariableName %>.getDischargedDate() != null ? <%- preffixVariableName %>.getDischargedDate().toInstant().atOffset(ZoneOffset.UTC) : null)
                .type(<%- preffixClassName %>TypeDto.fromValue(<%- preffixVariableName %>.getType().toString()));
<%_ } else if (microserviceType === 'codefirst' || microserviceType === 'none') { _%>
        return <%- preffixClassName %>Dto.builder().id(<%- preffixVariableName %>.getId()).name(<%- preffixVariableName %>.getName()).description(<%- preffixVariableName %>.getDescription())
                .amount(<%- preffixVariableName %>.getAmount()).creationDate(<%- preffixVariableName %>.getCreationDate().toInstant().atOffset(ZoneOffset.UTC))
                .dischargedDate(<%- preffixVariableName %>.getDischargedDate() != null ? <%- preffixVariableName %>.getDischargedDate().toInstant().atOffset(ZoneOffset.UTC) : null)
                .type(<%- preffixClassName %>Dto.<%- preffixClassName %>TypeDto.fromValue(<%- preffixVariableName %>.getType().toString())).build();
<%_ } else {
    throw new Error(`Unknown ${microserviceType}`);
} _%>
    }

    private static <%- preffixClassName %>.<%- preffixClassName %>Builder internalCreate() {
        final var enumValues = List.of(<%- preffixClassName %>.<%- preffixClassName %>Type.values());
        return <%- preffixClassName %>.builder().name(faker.internet().uuid()).description(faker.lorem().paragraph())
                .amount(faker.number().randomDouble(1, 1, 10)).creationDate(new Date())
                .type(enumValues.get(faker.random().nextInt(enumValues.size())));
    }

    private static <%- preffixClassName %>.<%- preffixClassName %>Builder internalCopy(<%- preffixClassName %> from) {
        return <%- preffixClassName %>.builder().id(from.getId()).name(from.getName()).description(from.getDescription())
                .amount(from.getAmount()).creationDate(new Date(from.getCreationDate().toInstant().toEpochMilli()))
                .dischargedDate(from.getDischargedDate() != null ? new Date(from.getCreationDate().toInstant().toEpochMilli()) : null)
                .type(from.getType());
    }
}
