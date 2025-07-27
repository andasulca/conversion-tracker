package io.github.andasulca.conversiontracker.service;

import io.github.andasulca.conversiontracker.dto.CommissionDto;
import io.github.andasulca.conversiontracker.dto.ConversionRateDto;
import io.github.andasulca.conversiontracker.dto.ProductConversionDto;
import io.github.andasulca.conversiontracker.entity.SalesData;
import io.github.andasulca.conversiontracker.repository.SalesDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConversionServiceTest {

    private SalesDataRepository repository;
    private ConversionService service;

    @BeforeEach
    void setup() {
        repository = mock(SalesDataRepository.class);
        service = new ConversionService(repository);
    }

    @Test
    void testGetConversionRate() {
        SalesData visit = new SalesData();
        visit.setLandingPageCode("TBS001");
        visit.setActionType("visit");

        SalesData purchase = new SalesData();
        purchase.setLandingPageCode("TBS001");
        purchase.setActionType("purchase");

        when(repository.findByLandingPageCodeAndTimestampBetween(eq("TBS001"), any(), any()))
                .thenReturn(List.of(visit, purchase, purchase));

        ConversionRateDto dto = service.getConversionRate("TBS001", LocalDateTime.now().minusDays(1), LocalDateTime.now());

        assertEquals("TBS001", dto.landingPageCode());
        assertEquals(2.0, dto.conversionRate());
    }

    @Test
    void testGetCommission() {
        SalesData purchase1 = new SalesData();
        purchase1.setActionType("purchase");
        purchase1.setLandingPageCode("TBS001");
        purchase1.setCommissionAmount(BigDecimal.valueOf(50));

        SalesData purchase2 = new SalesData();
        purchase2.setActionType("purchase");
        purchase2.setLandingPageCode("TBS001");
        purchase2.setCommissionAmount(BigDecimal.valueOf(25.5));

        when(repository.findByLandingPageCodeAndTimestampBetween(eq("TBS001"), any(), any()))
                .thenReturn(List.of(purchase1, purchase2));

        CommissionDto dto = service.getCommission("TBS001", LocalDateTime.now().minusDays(1), LocalDateTime.now());

        assertEquals("TBS001", dto.landingPageCode());
        assertEquals(75.5, dto.totalCommission());
    }

    @Test
    void testGetProductConversions() {
        SalesData visit = new SalesData();
        visit.setProductId("Smartwatch");
        visit.setLandingPageCode("TBS001");
        visit.setActionType("visit");

        SalesData purchase = new SalesData();
        purchase.setProductId("Smartwatch");
        purchase.setLandingPageCode("TBS001");
        purchase.setActionType("purchase");

        when(repository.findByTimestampBetween(any(), any())).thenReturn(List.of(visit, purchase, purchase));

        List<ProductConversionDto> result = service.getProductConversions(LocalDateTime.now().minusDays(1), LocalDateTime.now());

        assertEquals(1, result.size());
        ProductConversionDto dto = result.get(0);

        assertEquals("Smartwatch", dto.productName());
        assertEquals("TBS001", dto.landingPageCode());
        assertEquals(2.0, dto.conversionRate());
    }
}
