package com.example.<%- artifactId %>.cron;

import com.example.<%- artifactId %>.services.impl.<%- preffixClassName %>CronService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class <%- preffixClassName %>Cron {

    private final <%- preffixClassName %>CronService <%- preffixVariableName %>CronService;

    @Scheduled(fixedDelayString = "#{${spring.<%- artifactId %>.cron.delayInSeconds:60} * 1000}")
    public void run() {
        final var startDate = new Date();
        try {
            log.info("Run cron at {}", startDate);
            this.<%- preffixVariableName %>CronService.run();
        } finally {
            final var endDate = new Date();
            log.info("Finish cron at {} ({} seconds)", endDate, (endDate.getTime() - startDate.getTime()) / (1000 * 60));
        }
    }
}
