package io.github.andasulca.conversiontracker.controller;

import io.github.andasulca.conversiontracker.dto.CommissionDto;
import io.github.andasulca.conversiontracker.dto.ConversionRateDto;
import io.github.andasulca.conversiontracker.dto.ProductConversionDto;
import io.github.andasulca.conversiontracker.service.ConversionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ConversionController {

    private final ConversionService conversionService;

    public ConversionController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @GetMapping("/conversion-rate")
    public ConversionRateDto getConversionRate(
            @RequestParam String landingPageCode,
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.atTime(LocalTime.MAX);

        return conversionService.getConversionRate(landingPageCode, fromDateTime, toDateTime);
    }

    @GetMapping("/commission")
    public CommissionDto getCommission(
            @RequestParam String landingPageCode,
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.atTime(LocalTime.MAX);

        return conversionService.getCommission(landingPageCode, fromDateTime, toDateTime);
    }

    @GetMapping("/products/conversions")
    public List<ProductConversionDto> getProductConversions(
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.atTime(LocalTime.MAX);

        return conversionService.getProductConversions(fromDateTime, toDateTime);
    }
}
