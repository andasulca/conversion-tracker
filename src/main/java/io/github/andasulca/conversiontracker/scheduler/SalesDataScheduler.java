package io.github.andasulca.conversiontracker.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SalesDataScheduler {

    @Scheduled(initialDelay = 1000, fixedDelay = Long.MAX_VALUE)
    public void fetchInitialData() {
        // TODO: Call external API and save data
    }

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void pollNewData() {
        // TODO: Call external API every 5 minutes and filter for tracked IDs
    }
}