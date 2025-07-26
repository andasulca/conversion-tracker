
package io.github.andasulca.conversiontracker.service;

import io.github.andasulca.conversiontracker.dto.CommissionDto;
import io.github.andasulca.conversiontracker.dto.ConversionRateDto;
import io.github.andasulca.conversiontracker.dto.ProductConversionDto;
import io.github.andasulca.conversiontracker.entity.SalesData;
import io.github.andasulca.conversiontracker.repository.SalesDataRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    public ConversionRateDto getConversionRate(String code, LocalDate from, LocalDate to) {
        List<SalesData> data = salesDataRepository.findAll();
        long visits = data.stream()
                .filter(d -> "visit".equalsIgnoreCase(d.getActionType()))
                .filter(d -> code.equals(d.getLandingPageCode()))
                .filter(d -> isInRange(d.getTimestamp(), from, to))
                .count();

        long purchases = data.stream()
                .filter(d -> "purchase".equalsIgnoreCase(d.getActionType()))
                .filter(d -> code.equals(d.getLandingPageCode()))
                .filter(d -> isInRange(d.getTimestamp(), from, to))
                .count();

        double rate = (visits == 0) ? 0.0 : (double) purchases / visits;
        return new ConversionRateDto(code, rate);
    }

    public CommissionDto getCommission(String code, LocalDate from, LocalDate to) {
        List<SalesData> data = salesDataRepository.findAll();
        double total = data.stream()
                .filter(d -> "purchase".equalsIgnoreCase(d.getActionType()))
                .filter(d -> code.equals(d.getLandingPageCode()))
                .filter(d -> isInRange(d.getTimestamp(), from, to))
                .mapToDouble(d -> d.getCommissionAmount() != null ? d.getCommissionAmount().doubleValue() : 0.0)
                .sum();

        return new CommissionDto(code, total);
    }

    public List<ProductConversionDto> getProductConversions(LocalDate from, LocalDate to) {
        List<SalesData> data = salesDataRepository.findAll().stream()
                .filter(d -> isInRange(d.getTimestamp(), from, to))
                .toList();

        Map<String, List<SalesData>> grouped = data.stream()
                .collect(Collectors.groupingBy(SalesData::getProductId));

        return grouped.entrySet().stream().map(entry -> {
            String productId = entry.getKey();
            List<SalesData> entries = entry.getValue();
            long visits = entries.stream().filter(d -> "visit".equalsIgnoreCase(d.getActionType())).count();
            long purchases = entries.stream().filter(d -> "purchase".equalsIgnoreCase(d.getActionType())).count();
            double rate = (visits == 0) ? 0.0 : (double) purchases / visits;
            return new ProductConversionDto(productId, rate);
        }).toList();
    }

    private boolean isInRange(LocalDateTime timestamp, LocalDate from, LocalDate to) {
        return timestamp != null && !timestamp.toLocalDate().isBefore(from) && !timestamp.toLocalDate().isAfter(to);
    }
}
