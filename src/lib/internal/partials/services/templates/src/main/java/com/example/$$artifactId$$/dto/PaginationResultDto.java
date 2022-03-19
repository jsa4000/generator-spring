package com.example.<%- artifactId %>.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class PaginationResultDto<T> {

    private transient List<T> result;
    private Long pageNumber;
    private Long pageSize;
    private Long offset;
    private Long total;
}
