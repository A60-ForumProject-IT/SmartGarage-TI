package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.services.contracts.CurrencyConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class CurrencyConversionServiceImpl implements CurrencyConversionService {
    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String apiKey;

    @Autowired
    public CurrencyConversionServiceImpl(RestTemplate restTemplate,
                                         @Value("${exchangerate.api.url}") String apiUrl,
                                         @Value("${exchangerate.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }

    @Override
    public double convertCurrency(double amount, String fromCurrency, String toCurrency) {
        String urlToEur = String.format("%s?access_key=%s&symbols=BGN", apiUrl, apiKey);
        double bgnRate = fetchConversionRate(urlToEur, "BGN");

        double amountInEur = amount / bgnRate;

        if ("EUR".equalsIgnoreCase(toCurrency)) {
            return amountInEur;
        }

        String urlToTarget = String.format("%s?access_key=%s&symbols=%s", apiUrl, apiKey, toCurrency);
        double targetRate = fetchConversionRate(urlToTarget, toCurrency);

        return amountInEur * targetRate;
    }

    private double fetchConversionRate(String url, String currency) {
        ResponseEntity<Map<String, Object>> response = restTemplate.getForEntity(url, (Class<Map<String, Object>>) (Class<?>) Map.class);
        Map<String, Object> responseBody = response.getBody();

        if (responseBody == null || !responseBody.containsKey("rates")) {
            throw new RuntimeException("Failed to fetch conversion rate. API request was not successful.");
        }

        Map<String, Object> rates = (Map<String, Object>) responseBody.get("rates");
        if (rates == null || !rates.containsKey(currency)) {
            throw new RuntimeException("Failed to fetch conversion rate. Rates are missing for currency: " + currency);
        }

        return (double) rates.get(currency);
    }
}
