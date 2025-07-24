package io.github.andasulca.conversiontracker.client;

import io.github.andasulca.conversiontracker.entity.SalesData;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Component
public class JunoClient {

    private static final String BASE_URL = "http://localhost:8081/sales-data";
    private final RestTemplate restTemplate = new RestTemplate();

    public List<SalesData> fetchSalesData(LocalDate fromDate, LocalDate toDate) {
        String uri = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("fromDate", fromDate.format(DateTimeFormatter.ISO_DATE))
                .queryParam("toDate", toDate.format(DateTimeFormatter.ISO_DATE))
                .toUriString();

        SalesData[] response = restTemplate.getForObject(uri, SalesData[].class);
        return response != null ? Arrays.asList(response) : List.of();
    }
}
