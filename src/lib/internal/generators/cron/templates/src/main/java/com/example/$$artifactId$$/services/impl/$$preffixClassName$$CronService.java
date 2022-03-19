package com.example.<%- artifactId %>.services.impl;

import com.example.architecture.services.annotations.TransactionNever;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class <%- preffixClassName %>CronService {

    private final <%- preffixClassName %>Service <%- preffixVariableName %>Service;

    @TransactionNever
    public void run() {
        final var streamResult = this.<%- preffixVariableName %>Service.findByNotReviewed();
        log.info("Found {} <%- preffixVariableName %>s for apply review", streamResult.size());
        streamResult.forEach(<%- preffixVariableName %> -> this.<%- preffixVariableName %>Service.review<%- preffixClassName %>ById(<%- preffixVariableName %>.getId()));
        if (log.isInfoEnabled()) {
            log.info("Has been finish all of them except {} <%- preffixVariableName %>s", this.<%- preffixVariableName %>Service.countByNotReviewed());
        }
    }
}
