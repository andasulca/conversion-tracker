package io.github.andasulca.conversiontracker.controller;

import io.github.andasulca.conversiontracker.service.ConversionService;
import io.github.andasulca.conversiontracker.dto.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return conversionService.getConversionRate(landingPageCode, from, to);
    }

    @GetMapping("/commission")
    public CommissionDto getCommission(
            @RequestParam String landingPageCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return conversionService.getCommission(landingPageCode, from, to);
    }

    @GetMapping("/products/conversions")
    public List<ProductConversionDto> getProductConversions(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return conversionService.getProductConversions(from, to);
    }
}