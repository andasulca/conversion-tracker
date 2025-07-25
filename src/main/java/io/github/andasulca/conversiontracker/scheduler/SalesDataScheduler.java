package io.github.andasulca.conversiontracker.scheduler;

import io.github.andasulca.conversiontracker.client.JunoClient;
import io.github.andasulca.conversiontracker.entity.SalesData;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class SalesDataScheduler {

    private static final Logger logger = LoggerFactory.getLogger(SalesDataScheduler.class);

    private final JunoClient junoClient;

    private final List<SalesData> inMemoryData = new ArrayList<>();

    public SalesDataScheduler(JunoClient junoClient) {
        this.junoClient = junoClient;
    }

    // On app startup — load historical data
    @PostConstruct
    public void fetchInitialData() {
        try {
            LocalDate today = LocalDate.now();
            LocalDate tenDaysAgo = today.minusDays(10);

            List<SalesData> historical = junoClient.fetchSalesData(tenDaysAgo, today);

            if (historical.isEmpty()) {
                logger.warn("Fetched historical data successfully but got an empty result.");
            } else {
                inMemoryData.addAll(historical);
                logger.info("Loaded {} historical records.", historical.size());
            }
        } catch (Exception e) {
            logger.warn("Failed to fetch historical sales data on startup. Will retry later.", e);
        }
    }



    // Poll Juno every 5 minutes
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void pollJuno() {
        try {
            LocalDate today = LocalDate.now();
            List<SalesData> newData = junoClient.fetchSalesData(today, today);

            if (newData.isEmpty()) {
                logger.warn("Polled new data but got empty result.");
            } else {
                inMemoryData.addAll(newData);
                logger.info("Polled new data: {} records.", newData.size());
            }
        } catch (Exception e) {
            logger.error("Failed to poll sales data from Juno.", e);
        }
    }

    // Optional: access the data (e.g., from a controller later)
    public List<SalesData> getData() {
        return inMemoryData;
    }
}
