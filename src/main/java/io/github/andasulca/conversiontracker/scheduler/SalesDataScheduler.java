package io.github.andasulca.conversiontracker.scheduler;

import io.github.andasulca.conversiontracker.client.JunoClient;
import io.github.andasulca.conversiontracker.dto.SalesDataDto;
import io.github.andasulca.conversiontracker.entity.SalesData;
import io.github.andasulca.conversiontracker.repository.SalesDataRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SalesDataScheduler {

    private final JunoClient junoClient;
    private final SalesDataRepository salesDataRepository;

    private static final List<String> TRACKING_IDS = Arrays.asList(
            "ABB001", "ABB002", "ABB003", "ABB004", "ABB005",
            "TBS001", "TBS002", "TBS003", "TBS004", "TBS005",
            "EKW001", "EKW002", "EKW003", "EKW004", "EKW005"
    );

    public SalesDataScheduler(JunoClient junoClient, SalesDataRepository salesDataRepository) {
        this.junoClient = junoClient;
        this.salesDataRepository = salesDataRepository;
    }

    @PostConstruct
    public void init() {
        log.info("Fetching historical data on startup...");
        fetchAndStoreData(LocalDate.now().minusMonths(1), LocalDate.now());
    }

    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void pollNewData() {
        log.info("Polling new data...");
        fetchAndStoreData(LocalDate.now().minusDays(1), LocalDate.now());
    }

    private void fetchAndStoreData(LocalDate from, LocalDate to) {
        List<SalesDataDto> dtos = junoClient.fetchSalesData(from, to);
        List<SalesData> allData = dtos.stream()
                .map(this::mapToEntity)
                .toList();

        log.info("Fetched {} total records from Juno API (range: {} to {})", allData.size(), from, to);

        allData.stream()
                .limit(5)
                .forEach(data -> log.info("Fetched: trackingId={}, actionType={}, productId={}",
                        data.getTrackingId(), data.getActionType(), data.getProductId()));

        if (allData.size() > 5) {
            log.info("...and {} more records not shown", allData.size() - 5);
        }

        List<SalesData> filteredData = allData.stream()
                .filter(data -> TRACKING_IDS.contains(data.getTrackingId()))
                .distinct() // optional, in-memory deduplication
                .filter(data -> !salesDataRepository.existsByTrackingIdAndVisitDateAndSaleDateAndProductIdAndActionType(
                        data.getTrackingId(),
                        data.getVisitDate(),
                        data.getSaleDate(),
                        data.getProductId(),
                        data.getActionType()
                ))
                .toList();

        log.info("Filtered to {} records matching known tracking IDs and not already in DB", filteredData.size());

        try {
            salesDataRepository.saveAll(filteredData);
            log.info("Stored {} filtered sales records", filteredData.size());
        } catch (Exception e) {
            log.warn("Error while saving records to DB: {}", e.getMessage());
        }
    }


    private SalesData mapToEntity(SalesDataDto dto) {
        SalesData data = new SalesData();
        data.setTrackingId(dto.trackingId());//Used as Data source input
        data.setLandingPageCode(dto.trackingId());//Used for querying in app
        data.setVisitDate(dto.visitDate());
        data.setSaleDate(dto.saleDate());
        data.setSalePrice(dto.salePrice());
        data.setCommissionAmount(dto.commissionAmount());
        data.setProductId(dto.product());

        // Infer actionType based on saleDate
        if (dto.saleDate() != null) {
            data.setActionType("purchase");
        } else {
            data.setActionType("visit");
        }

        return data;
    }
}
