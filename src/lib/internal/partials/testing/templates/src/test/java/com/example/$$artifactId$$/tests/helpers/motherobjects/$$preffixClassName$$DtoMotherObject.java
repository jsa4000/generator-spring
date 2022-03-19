package com.example.<%- artifactId %>.tests.helpers.motherobjects;

import com.github.javafaker.Faker;
<%_ if (__self__._isSoapService()) { _%>
import com.example.<%- artifactId %>.soap.api.<%- preffixClassName %>Dto;
<%_ } else if (microserviceType === 'none' && !!persistenceLayer) { _%>
import com.example.<%- artifactId %>.tests.helpers.dto.<%- preffixClassName %>Dto;
<%_ } else { _%>
import com.example.<%- artifactId %>.dto.<%- preffixClassName %>Dto;
<%_ } _%>
<%_ if (persistenceLayer) { _%>
import com.example.architecture.services.vo.PaginationResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import com.example.<%- artifactId %>.dao.entities.<%- preffixClassName %>;
<%_ } else if (__self__._isSoapService()) { _%>
import com.example.<%- artifactId %>.soap.api.PaginationResult<%- preffixClassName %>Dto;
<%_ } else if (microserviceType === 'apifirst'){ _%>
import com.example.<%- artifactId %>.dto.PaginationResult<%- preffixClassName %>Dto;
<%_ } else { _%>
import com.example.<%- artifactId %>.dto.PaginationResultDto;
<%_ } _%>
<%_ if (__self__._isSoapService()) { _%>
import com.example.<%- artifactId %>.soap.api.<%- preffixClassName %>TypeDto;
<%_ } else if (microserviceType === 'apifirst') { _%>
import com.example.<%- artifactId %>.dto.<%- preffixClassName %>TypeDto;
<%_ } _%>
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ObjectUtils;
<%_ if (__self__._isSoapService()) { _%>
import lombok.SneakyThrows;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
<%_ } else { _%>
import java.time.OffsetDateTime;
<%_ } _%>
<%_ if (__self__._isSoapService() || persistenceLayer) { _%>
import java.util.Date;
<%_ } _%>
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@UtilityClass
public class <%- preffixClassName %>DtoMotherObject {

    private static Faker faker = new Faker();
<%_ if (persistenceLayer) { _%>
    public static PaginationResult<<%- preffixClassName %>Dto> paginationResult(int size, PaginationResultOptions opts) {
        return PaginationResult.fromPage(page(size, opts));
    }

    public static Page<<%- preffixClassName %>Dto> page(int size, PaginationResultOptions opts) {
        opts = ObjectUtils.defaultIfNull(opts, PaginationResultOptions.builder().build());
        final var pageElements = opts.isWithId() ? listWithId(size) : list(size);
        return new PageImpl<<%- preffixClassName %>Dto>(
                pageElements,
                PageRequest.of(faker.random().nextInt(1, 4), pageElements.size()),
                faker.random().nextLong());
    }
<%_ } else { _%>

    <%_ if (microserviceType === 'apifirst' || __self__._isSoapService()) { _%>
    public static PaginationResult<%- preffixClassName %>Dto paginationResult(int size, PaginationResultOptions opts) {
    <%_ } else { _%>
    public static PaginationResultDto<<%- preffixClassName %>Dto> paginationResult(int size, PaginationResultOptions opts) {
    <%_ } _%>
        opts = ObjectUtils.defaultIfNull(opts, PaginationResultOptions.builder().build());
        final var pageElements = opts.isWithId() ? listWithId(size) : list(size);
        final var pageNumber = faker.random().nextLong();
    <%_ if (__self__._isSoapService()) { _%>
        final var paginationResult = new PaginationResult<%- preffixClassName %>Dto();
        paginationResult.getResult().addAll(pageElements);
        paginationResult.setPageNumber(pageNumber);
        paginationResult.setPageSize(Long.valueOf(size));
        paginationResult.setOffset((pageNumber - 1) * size);
        paginationResult.setTotal(Long.valueOf(size));
        return paginationResult;
    <%_ } else if (microserviceType === 'apifirst') { _%>
        return new PaginationResult<%- preffixClassName %>Dto().result(pageElements).pageNumber(pageNumber).pageSize(Long.valueOf(size))
                .offset((pageNumber - 1) * size)
                .total(Long.valueOf(size));
    <%_ } else { _%>
        return PaginationResultDto.<<%- preffixClassName %>Dto>builder().result(pageElements).pageNumber(pageNumber).pageSize(Long.valueOf(size))
                .offset((pageNumber - 1) * size)
                .total(Long.valueOf(size)).build();
    <%_ } _%>
    }
<%_ } _%>

    public static List<<%- preffixClassName %>Dto> listWithId(int size) {
        return IntStream.range(0, size).mapToObj((idx) -> withId())
                .collect(Collectors.toList());
    }

    public static List<<%- preffixClassName %>Dto> list(int size) {
        return IntStream.range(0, size).mapToObj((idx) -> create())
                .collect(Collectors.toList());
    }

    public static <%- preffixClassName %>Dto withId() {
        final var <%- preffixVariableName %> = create();
        <%- preffixVariableName %>.setId(Integer.valueOf(faker.random().nextInt(1, Integer.MAX_VALUE)).longValue());
        return <%- preffixVariableName %>;
    }
<%_ if (__self__._isSoapService()) { _%>
    @SneakyThrows
<%_ } _%>
    public static <%- preffixClassName %>Dto create() {
<%_ if (microserviceType === 'apifirst' || __self__._isSoapService()) { _%>
        final var enumValues = List.of(<%- preffixClassName %>TypeDto.values());
        <%_ if (microserviceType === 'apifirst') { _%>
        return new <%- preffixClassName %>Dto().name(faker.internet().uuid()).description(faker.lorem().paragraph())
                .amount(faker.number().randomDouble(1, 1, 10)).creationDate(OffsetDateTime.now())
                .type(enumValues.get(faker.random().nextInt(enumValues.size())));
        <%_ } else if (__self__._isSoapService()) { _%>
        final var <%- preffixVariableName %>Result = new <%- preffixClassName %>Dto();
        <%- preffixVariableName %>Result.setName(faker.internet().uuid());
        <%- preffixVariableName %>Result.setDescription(faker.lorem().paragraph());
        <%- preffixVariableName %>Result.setAmount(faker.number().randomDouble(1, 1, 10));

        final var creationDateCalendar = new GregorianCalendar();
        creationDateCalendar.setTime(new Date());
        <%- preffixVariableName %>Result.setCreationDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(creationDateCalendar));
        <%- preffixVariableName %>Result.setType(enumValues.get(faker.random().nextInt(enumValues.size())));
        return <%- preffixVariableName %>Result;
        <%_ } _%>
<%_ } else if (microserviceType === 'codefirst' || microserviceType === 'none') { _%>
        final var enumValues = List.of(<%- preffixClassName %>Dto.<%- preffixClassName %>TypeDto.values());
        return <%- preffixClassName %>Dto.builder().name(faker.internet().uuid()).description(faker.lorem().paragraph())
                .amount(faker.number().randomDouble(1, 1, 10)).creationDate(OffsetDateTime.now())
                .type(enumValues.get(faker.random().nextInt(enumValues.size()))).build();
<%_ } else {
    throw new Error(`Unknown ${microserviceType}`);
} _%>
    }

    public static <%- preffixClassName %>Dto copy(<%- preffixClassName %>Dto from) {
<%_ if (__self__._isSoapService()) { _%>
        final var <%- preffixVariableName %>Result = new <%- preffixClassName %>Dto();
        <%- preffixVariableName %>Result.setName(from.getName());
        <%- preffixVariableName %>Result.setDescription(from.getDescription());
        <%- preffixVariableName %>Result.setAmount(from.getAmount());
        <%- preffixVariableName %>Result.setCreationDate(from.getCreationDate());
        <%- preffixVariableName %>Result.setDischargedDate(from.getDischargedDate());
        <%- preffixVariableName %>Result.setType(from.getType());
        return <%- preffixVariableName %>Result;
<%_ } else if (microserviceType === 'apifirst') { _%>
        return new <%- preffixClassName %>Dto().name(from.getName()).description(from.getDescription())
                .amount(from.getAmount()).creationDate(from.getCreationDate()).dischargedDate(from.getDischargedDate())
                .type(from.getType());
<%_ } else if (microserviceType === 'codefirst' || microserviceType === 'none') { _%>
        return <%- preffixClassName %>Dto.builder().name(from.getName()).description(from.getDescription())
                .amount(from.getAmount()).creationDate(from.getCreationDate()).dischargedDate(from.getDischargedDate())
                .type(from.getType()).build();
<%_ } else {
    throw new Error(`Unknown ${microserviceType}`);
} _%>
    }

    @Getter @Builder
    public static final class PaginationResultOptions {
        private boolean withId;
    }
}