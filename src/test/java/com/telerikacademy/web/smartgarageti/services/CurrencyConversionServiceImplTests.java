package com.telerikacademy.web.smartgarageti.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CurrencyConversionServiceImplTests {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CurrencyConversionServiceImpl currencyConversionService;

    @Test
    void convertCurrency_ShouldReturnConvertedAmount_WhenCurrencyIsEUR() {
        // Arrange
        String apiUrl = "http://example.com";
        String apiKey = "mockApiKey";
        currencyConversionService = new CurrencyConversionServiceImpl(restTemplate, apiUrl, apiKey);

        double amount = 100.0;
        String fromCurrency = "BGN";
        String toCurrency = "EUR";

        Map<String, Object> rates = new HashMap<>();
        rates.put("BGN", 2.0);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("rates", rates);
        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), any(Class.class)))
                .thenReturn(responseEntity);

        // Act
        double result = currencyConversionService.convertCurrency(amount, fromCurrency, toCurrency);

        // Assert
        assertEquals(50.0, result);
    }

    @Test
    void convertCurrency_ShouldReturnConvertedAmount_WhenCurrencyIsNotEUR() {
        // Arrange
        String apiUrl = "http://example.com";
        String apiKey = "mockApiKey";
        currencyConversionService = new CurrencyConversionServiceImpl(restTemplate, apiUrl, apiKey);

        double amount = 100.0;
        String fromCurrency = "BGN";
        String toCurrency = "USD";

        Map<String, Object> bgnRates = new HashMap<>();
        bgnRates.put("BGN", 2.0);
        Map<String, Object> usdRates = new HashMap<>();
        usdRates.put("USD", 1.5);

        Map<String, Object> responseBody1 = new HashMap<>();
        responseBody1.put("rates", bgnRates);

        Map<String, Object> responseBody2 = new HashMap<>();
        responseBody2.put("rates", usdRates);

        ResponseEntity<Map<String, Object>> responseEntity1 = new ResponseEntity<>(responseBody1, HttpStatus.OK);
        ResponseEntity<Map<String, Object>> responseEntity2 = new ResponseEntity<>(responseBody2, HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), any(Class.class)))
                .thenReturn(responseEntity1)
                .thenReturn(responseEntity2);

        // Act
        double result = currencyConversionService.convertCurrency(amount, fromCurrency, toCurrency);

        // Assert
        assertEquals(75.0, result);
    }

    @Test
    void convertCurrency_ShouldThrowRuntimeException_WhenRatesAreMissing() {
        // Arrange
        String apiUrl = "http://example.com";
        String apiKey = "mockApiKey";
        currencyConversionService = new CurrencyConversionServiceImpl(restTemplate, apiUrl, apiKey);

        double amount = 100.0;
        String fromCurrency = "BGN";
        String toCurrency = "USD";

        Map<String, Object> responseBody = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), any(Class.class)))
                .thenReturn(responseEntity);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> currencyConversionService.convertCurrency(amount, fromCurrency, toCurrency));
    }

//    @Test
//    void convertCurrency_ShouldReturnConvertedAmount() {
//        // Arrange
//        RestTemplate restTemplate = mock(RestTemplate.class);
//        CurrencyConversionServiceImpl currencyConversionService = new CurrencyConversionServiceImpl(
//                restTemplate, "https://api.example.com", "apiKey");
//
//        double amount = 100.0;
//        String fromCurrency = "BGN";
//        String toCurrency = "USD";
//
//        // Mock response for BGN to EUR conversion
//        Map<String, Object> rates = new HashMap<>();
//        rates.put("BGN", 2.0);  // Example rate
//        Map<String, Object> responseBody = new HashMap<>();
//        responseBody.put("rates", rates);
//        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
//
//        // Mock the behavior of getForEntity to return the response entity
//        when(restTemplate.getForEntity(anyString(), any(Class.class)))
//                .thenReturn(responseEntity);
//
//        // Act
//        double result = currencyConversionService.convertCurrency(amount, fromCurrency, toCurrency);
//
//        // Assert
//        assertEquals(50.0, result);  // Adjust the expected result based on the conversion logic
//    }
}
