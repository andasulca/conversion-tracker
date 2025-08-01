package io.github.andasulca.conversiontracker.service;

import io.github.andasulca.conversiontracker.dto.CommissionDto;
import io.github.andasulca.conversiontracker.dto.ConversionRateDto;
import io.github.andasulca.conversiontracker.dto.ProductConversionDto;
import io.github.andasulca.conversiontracker.entity.SalesData;
import io.github.andasulca.conversiontracker.repository.SalesDataRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ConversionService {

    private final SalesDataRepository salesDataRepository;

    public ConversionService(SalesDataRepository salesDataRepository) {
        this.salesDataRepository = salesDataRepository;
    }

    public ConversionRateDto getConversionRate(String code, LocalDateTime from, LocalDateTime to) {
        List<SalesData> data = salesDataRepository.findByLandingPageCodeAndTimestampBetween(code, from, to);

        long visits = data.stream()
                .filter(d -> "visit".equalsIgnoreCase(d.getActionType()))
                .count();

        long purchases = data.stream()
                .filter(d -> "purchase".equalsIgnoreCase(d.getActionType()))
                .count();

        double rate = (visits == 0) ? 0.0 : (double) purchases / visits;
        return new ConversionRateDto(code, rate);
    }

    public CommissionDto getCommission(String code, LocalDateTime from, LocalDateTime to) {
        List<SalesData> data = salesDataRepository.findByLandingPageCodeAndTimestampBetween(code, from, to);

        double total = data.stream()
                .filter(d -> "purchase".equalsIgnoreCase(d.getActionType()))
                .mapToDouble(d -> d.getCommissionAmount() != null ? d.getCommissionAmount().doubleValue() : 0.0)
                .sum();

        return new CommissionDto(code, total);
    }

    public List<ProductConversionDto> getProductConversions(LocalDateTime from, LocalDateTime to) {
        List<SalesData> data = salesDataRepository.findByTimestampBetween(from, to);

        // Group by productId + landingPageCode
        Map<String, Map<String, List<SalesData>>> grouped = data.stream()
                .collect(Collectors.groupingBy(
                        SalesData::getProductId,
                        Collectors.groupingBy(SalesData::getLandingPageCode)
                ));

        // Flatten the nested map of productId → landingPageCode → SalesData list,
        // then calculate conversion rate (purchases / visits) for each unique
        // (productId, landingPageCode) pair and map to ProductConversionDto.
        return grouped.entrySet().stream()
                .flatMap(productEntry -> {
                    String productId = productEntry.getKey();
                    Map<String, List<SalesData>> byLandingPage = productEntry.getValue();

                    return byLandingPage.entrySet().stream().map(lpEntry -> {
                        String landingPageCode = lpEntry.getKey();
                        List<SalesData> entries = lpEntry.getValue();

                        long visits = entries.stream()
                                .filter(d -> "visit".equalsIgnoreCase(d.getActionType()))
                                .count();
                        long purchases = entries.stream()
                                .filter(d -> "purchase".equalsIgnoreCase(d.getActionType()))
                                .count();

                        double rate = (visits == 0) ? 0.0 : (double) purchases / visits;

                        return new ProductConversionDto(productId, landingPageCode, rate);
                    });
                })
                .toList();
    }
}
