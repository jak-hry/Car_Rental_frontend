package com.kodilla.view.list;

import com.kodilla.domain.dto.CarDto;
import com.kodilla.view.service.CarService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

@Route("api-cars")
public class APICarsView extends VerticalLayout {

    private CarService carService = new CarService();
    private final Label selectedCarLabel = new Label();
    private final String CARS_NAVIGATE_URL = "http://localhost:8081/cars";

    public APICarsView() {
        configureGrid();
        updateGrid();

        Button saveSelectedButton = new Button("Save Selected to Database", event -> saveSelectedCarToDatabase());
        Button saveAllButton = new Button("Save All to Database", event -> saveAllCarsToDatabase());

        Label infoLabel = new Label("Please note: After saving the car to the database, you need to fill in the remaining car details.");

        Button fillDataButton = new Button("Fill Remaining Data", event -> navigateToCarsView());

        add(carService.grid, selectedCarLabel, infoLabel, saveSelectedButton, saveAllButton, fillDataButton);
    }

    private void configureGrid() {
        carService.grid.setColumns("model", "available");
        carService.grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                CarDto selectedCar = event.getValue();
                selectedCarLabel.setText("Selected Car: " + selectedCar.getModel());
            } else {
                selectedCarLabel.setText("Selected Car: None");
            }
        });
    }

    private void updateGrid() {
        ResponseEntity<CarDto[]> response = carService.restTemplate.getForEntity(carService.API_CAR_BASE_URL, CarDto[].class);
        List<CarDto> cars = Arrays.asList(response.getBody());
        carService.grid.setItems(cars);
    }

    private void saveAllCarsToDatabase() {
        ResponseEntity<CarDto[]> response = carService.restTemplate.getForEntity(carService.API_CAR_BASE_URL, CarDto[].class);
        List<CarDto> cars = Arrays.asList(response.getBody());
        for (CarDto car : cars) {
            HttpEntity<CarDto> request = new HttpEntity<>(car);
            carService.restTemplate.postForEntity(carService.BASE_URL, request, CarDto.class);
        }
        Notification.show("All cars saved to the database.");
    }

    private void saveSelectedCarToDatabase() {
        CarDto selectedCar = carService.grid.asSingleSelect().getValue();
        if (selectedCar != null) {
            HttpEntity<CarDto> request = new HttpEntity<>(selectedCar);
            carService.restTemplate.postForEntity(carService.BASE_URL, request, CarDto.class);
            Notification.show("Selected car saved to the database.");
        } else {
            Notification.show("Please select a car.");
        }
    }

    private void navigateToCarsView() {
        UI.getCurrent().navigate(CARS_NAVIGATE_URL);
    }
}