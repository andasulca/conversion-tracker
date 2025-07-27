package io.github.andasulca.conversiontracker.scheduler;

import io.github.andasulca.conversiontracker.client.JunoClient;
import io.github.andasulca.conversiontracker.dto.SalesDataDto;
import io.github.andasulca.conversiontracker.entity.SalesData;
import io.github.andasulca.conversiontracker.repository.SalesDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SalesDataSchedulerTest {

    private JunoClient junoClient;
    private SalesDataRepository repository;
    private SalesDataScheduler scheduler;

    @BeforeEach
    void setup() {
        junoClient = mock(JunoClient.class);
        repository = mock(SalesDataRepository.class);
        scheduler = new SalesDataScheduler(junoClient, repository);
    }

    @Test
    void testFetchAndStoreData_savesMappedAndFilteredData() {
        // Given
        SalesDataDto dto = new SalesDataDto(
                1L,
                "TBS001",
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                BigDecimal.valueOf(100),
                "Smartwatch",
                BigDecimal.valueOf(15)
        );

        when(junoClient.fetchSalesData(any(), any())).thenReturn(List.of(dto));

        // When
        scheduler.pollNewData();

        // Then
        ArgumentCaptor<List<SalesData>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository).saveAll(captor.capture());

        List<SalesData> saved = captor.getValue();
        assertEquals(1, saved.size());

        SalesData mapped = saved.get(0);
        assertEquals("TBS001", mapped.getLandingPageCode());
        assertEquals("purchase", mapped.getActionType());
        assertEquals("Smartwatch", mapped.getProductId());
        assertEquals(BigDecimal.valueOf(15), mapped.getCommissionAmount());
    }
}
