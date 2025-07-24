package io.github.andasulca.conversiontracker.scheduler;

import io.github.andasulca.conversiontracker.client.JunoClient;
import io.github.andasulca.conversiontracker.entity.SalesData;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class SalesDataScheduler {

    private final JunoClient junoClient;

    private final List<SalesData> inMemoryData = new ArrayList<>();

    public SalesDataScheduler(JunoClient junoClient) {
        this.junoClient = junoClient;
    }

    // On app startup — load historical data
    @PostConstruct
    public void fetchInitialData() {
        LocalDate today = LocalDate.now();
        LocalDate tenDaysAgo = today.minusDays(10);

        List<SalesData> historical = junoClient.fetchSalesData(tenDaysAgo, today);
        inMemoryData.addAll(historical);
        System.out.println("Loaded " + historical.size() + " historical records.");
    }

    // Poll Juno every 5 minutes
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void pollJuno() {
        LocalDate today = LocalDate.now();
        List<SalesData> newData = junoClient.fetchSalesData(today, today);
        inMemoryData.addAll(newData);
        System.out.println("Polled new data: " + newData.size() + " records.");
    }

    // Optional: access the data (e.g., from a controller later)
    public List<SalesData> getData() {
        return inMemoryData;
    }
}
