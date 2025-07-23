package io.github.andasulca.conversiontracker.service;

import io.github.andasulca.conversiontracker.dto.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ConversionService {

    public ConversionRateDto getConversionRate(String code, LocalDate from, LocalDate to) {
        return new ConversionRateDto(code, 0.0);
    }

    public CommissionDto getCommission(String code, LocalDate from, LocalDate to) {
        return new CommissionDto(code, 0.0);
    }

    public List<ProductConversionDto> getProductConversions(LocalDate from, LocalDate to) {
        return List.of();
    }
}