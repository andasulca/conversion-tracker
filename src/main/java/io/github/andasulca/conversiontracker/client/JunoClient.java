package io.github.andasulca.conversiontracker.client;

import io.github.andasulca.conversiontracker.dto.SalesDataDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class JunoClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl;

    public JunoClient(@Value("${external.api.url}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public List<SalesDataDto> fetchSalesData(LocalDate fromDate, LocalDate toDate) {
        String uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("fromDate", fromDate.format(DateTimeFormatter.ISO_DATE))
                .queryParam("toDate", toDate.format(DateTimeFormatter.ISO_DATE))
                .toUriString();

        log.info("Requesting data from Juno: {}", uri);

        try {
            ResponseEntity<String> rawResponse = restTemplate.getForEntity(uri, String.class);
            String body = rawResponse.getBody();
            if (body != null) {
                String preview = body.length() > 100 ? body.substring(0, 100) + "..." : body;
                log.info("Raw JSON preview: {}", preview);
            }

            SalesDataDto[] response = restTemplate.getForObject(uri, SalesDataDto[].class);
            if (response != null) {
                log.info("Parsed {} sales records", response.length);
                return Arrays.asList(response);
            }
        } catch (Exception e) {
            log.error("Failed to fetch or parse sales data", e);
        }

        return List.of();
    }
}
