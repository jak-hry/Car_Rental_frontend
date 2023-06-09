package com.kodilla.view.service;

import com.kodilla.domain.dto.CarDto;
import com.kodilla.view.MainView;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.ListDataProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class CarService {
    public final String BASE_URL = "http://localhost:8080/v1/cars";
    public final String AVAILABLE_CAR_BASE_URL = "http://localhost:8080/v1/cars/available";
    public final String API_CAR_BASE_URL = "http://localhost:8080/v1/cars/api";
    public final Grid<CarDto> grid = new Grid<>(CarDto.class);
    public final RestTemplate restTemplate = new RestTemplate();

    public void configureCarGrid(MainView mainView) {
        grid.removeAllColumns();
        grid.addColumn(car -> car.getCostPerDay() != null ? car.getCostPerDay().toString() : "").setHeader("Cost Per Day");
        grid.addColumn(CarDto::getModel).setHeader("Model");
        grid.addColumn(car -> car.getCategory() != null ? car.getCategory().getName() : "").setHeader("Category");
        grid.addColumn(car -> car.getTransmissionType() != null ? car.getTransmissionType().getName() : "").setHeader("Transmission Type");

        grid.asSingleSelect().addValueChangeListener(event -> {
            CarDto selectedCar = event.getValue();
            if (selectedCar != null) {
                mainView.getExchangeRateService().updateExchangeRateLabels(mainView);
            }
        });
    }

    public void updateCarGrid() {
        ResponseEntity<CarDto[]> response = new RestTemplate().getForEntity(AVAILABLE_CAR_BASE_URL, CarDto[].class);
        List<CarDto> cars = Arrays.asList(response.getBody());
        grid.setItems(cars);
    }

    public boolean isCarListEmpty() {
        ListDataProvider<CarDto> dataProvider = (ListDataProvider<CarDto>) grid.getDataProvider();
        return dataProvider.getItems().isEmpty();
    }

    public Grid<CarDto> getGrid() {
        return grid;
    }
}