package com.kodilla.view.service;

import com.kodilla.domain.dto.CarDto;
import com.kodilla.view.MainView;
import com.vaadin.flow.component.html.Label;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Map;

public class ExchangeRateService {

    private static final String EXCHANGE_BASE_URL = "http://localhost:8080/v1/exchange";
    private final Label usdRateLabel = new Label();
    private final Label eurRateLabel = new Label();
    private final Label plnRateLabel = new Label();

    public void addExchangeRateLabels(MainView mainView) {
        mainView.add(usdRateLabel, eurRateLabel, plnRateLabel);
    }

    public void updateExchangeRateLabels(MainView mainView) {
        CarDto selectedCar = mainView.getCarService().getGrid().asSingleSelect().getValue();
        Integer rentalDuration = mainView.getRentalService().getRentalDurationComboBox().getValue();

        if (selectedCar != null && selectedCar.getCostPerDay() != null) {
            BigDecimal costPerDay = new BigDecimal(selectedCar.getCostPerDay().toString());

            Double usdRate = getExchangeRate("USD");
            Double eurRate = getExchangeRate("EUR");
            Double plnRate = getExchangeRate("PLN");

            if (usdRate != null && eurRate != null && plnRate != null) {
                DecimalFormat decimalFormat = new DecimalFormat("#.00");

                BigDecimal usdTotal = costPerDay.multiply(BigDecimal.valueOf(rentalDuration)).multiply(BigDecimal.valueOf(usdRate));
                BigDecimal eurTotal = costPerDay.multiply(BigDecimal.valueOf(rentalDuration)).multiply(BigDecimal.valueOf(eurRate));
                BigDecimal plnTotal = costPerDay.multiply(BigDecimal.valueOf(rentalDuration)).multiply(BigDecimal.valueOf(plnRate));

                usdRateLabel.setText("USD Exchange Rate: " + decimalFormat.format(usdTotal));
                eurRateLabel.setText("EUR Exchange Rate: " + decimalFormat.format(eurTotal));
                plnRateLabel.setText("PLN Exchange Rate: " + decimalFormat.format(plnTotal));
            } else {
                usdRateLabel.setText("Failed to fetch USD exchange rate.");
                eurRateLabel.setText("Failed to fetch EUR exchange rate.");
                plnRateLabel.setText("Failed to fetch PLN exchange rate.");
            }

        } else {
            usdRateLabel.setText("Please select a car.");
            eurRateLabel.setText("");
            plnRateLabel.setText("");
        }
    }

    public Double getExchangeRate(String currency) {
        String apiUrl = EXCHANGE_BASE_URL + "?currency=" + currency;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map<String, Double>> response = new RestTemplate().exchange(
                apiUrl,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Map<String, Double>>() {
                }
        );

        Map<String, Double> exchangeRates = response.getBody();

        if (exchangeRates != null) {
            Double rate = exchangeRates.get(currency);
            if (rate != null) {
                System.out.println("Current exchange rate for " + currency + ": " + rate);
                return rate;
            }
        }

        System.out.println("Failed to fetch exchange rate for " + currency);
        return null;
    }
}
