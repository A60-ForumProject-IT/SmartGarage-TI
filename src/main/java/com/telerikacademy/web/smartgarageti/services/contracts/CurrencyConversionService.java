package com.telerikacademy.web.smartgarageti.services.contracts;

public interface CurrencyConversionService {
    double convertCurrency(double amount, String fromCurrency, String toCurrency);
}
