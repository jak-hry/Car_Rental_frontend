package com.kodilla.view.list;

import com.kodilla.domain.dto.CarCategoryDto;
import com.kodilla.domain.dto.CarDto;
import com.kodilla.domain.dto.TransmissionTypeDto;
import com.kodilla.view.service.CarService;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Route("cars")
public class CarsView extends VerticalLayout {

    private final CarService carService = new CarService();
    private final RestTemplate restTemplate = new RestTemplate();
    private final Grid<CarDto> grid = new Grid<>(CarDto.class);
    private final TextField modelField = new TextField("Model");
    private final ComboBox<CarCategoryDto> categoryComboBox = new ComboBox<>("Category");
    private final ComboBox<TransmissionTypeDto> transmissionTypeComboBox = new ComboBox<>("Transmission Type");
    private final Button backButton = new Button("Rentals", this::navigateToRentalsPage);
    private static final String CATEGORY_URL = "http://localhost:8080/v1/category";
    private static final String TRANSMISSION_TYPE_URL = "http://localhost:8080/v1/transmission";
    private static final String RENTALS_NAVIGATE_URL = "http://localhost:8081/main";

    public CarsView() {
        configureGrid();
        updateGrid();
        updateCategoryComboBox();
        updateTransmissionTypeComboBox();

        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button updateButton = new Button("Update", event -> updateCar());
        Button deleteButton = new Button("Delete", event -> deleteCar());
        buttonLayout.add(updateButton, deleteButton);

        Button showDetailsButton = new Button("Show Details", event -> showCarDetails());
        buttonLayout.add(showDetailsButton);

        add(grid, modelField, categoryComboBox, transmissionTypeComboBox, buttonLayout, backButton);
    }

    private void configureGrid() {
        grid.removeAllColumns();
        categoryComboBox.setItemLabelGenerator(CarCategoryDto::getName);
        grid.addColumn(car -> car.getCostPerDay() != null ? car.getCostPerDay().toString() : "").setHeader("Cost Per Day");
        grid.addColumn(CarDto::getModel).setHeader("Model");
        grid.addColumn(car -> car.getCategory() != null ? car.getCategory().getName() : "").setHeader("Category");
        grid.addColumn(car -> car.getTransmissionType() != null ? car.getTransmissionType().getName() : "").setHeader("Transmission Type");
        grid.addColumn(CarDto::isAvailable).setHeader("Available");
        grid.addColumn(CarDto::isDamaged).setHeader("Damaged");

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                CarDto selectedCar = event.getValue();
                modelField.setValue(selectedCar.getModel());
                categoryComboBox.setValue(selectedCar.getCategory());
                transmissionTypeComboBox.setValue(selectedCar.getTransmissionType());
            } else {
                clearFields();
            }
        });
    }

    private void updateGrid() {
        ResponseEntity<CarDto[]> response = restTemplate.getForEntity(carService.BASE_URL, CarDto[].class);
        List<CarDto> cars = Arrays.asList(response.getBody());
        grid.setItems(cars);
    }

    private void updateCar() {
        CarDto selectedCar = grid.asSingleSelect().getValue();
        if (selectedCar != null) {
            selectedCar.setModel(modelField.getValue());
            selectedCar.setTransmissionType(transmissionTypeComboBox.getValue());

            CarCategoryDto selectedCategory = categoryComboBox.getValue();
            if (selectedCategory != null) {
                selectedCar.setCategory(selectedCategory);

                System.out.println("Updated Car: " + selectedCar);

                ResponseEntity<CarDto> response = restTemplate.exchange(carService.BASE_URL + "/" +
                        selectedCar.getId(), HttpMethod.PUT, new HttpEntity<>(selectedCar), CarDto.class);
                CarDto updatedCar = response.getBody();

                if (updatedCar != null) {
                    updateGrid();
                    grid.select(updatedCar);
                    clearFields();
                    Notification.show("Car updated successfully.");
                } else {
                    Notification.show("Failed to update car. Please try again.");
                }
            } else {
                Notification.show("Please select a category.");
            }
        } else {
            Notification.show("Please select a car to update.");
        }
    }

    private void deleteCar() {
        CarDto selectedCar = grid.asSingleSelect().getValue();
        if (selectedCar != null) {
            restTemplate.delete(carService.BASE_URL + "/" + selectedCar.getId());
            updateGrid();
            clearFields();
            Notification.show("Car deleted successfully.");
        } else {
            Notification.show("Please select a car to delete.");
        }
    }

    private void clearFields() {
        modelField.clear();
        categoryComboBox.clear();
        transmissionTypeComboBox.clear();
    }

    private void updateCategoryComboBox() {
        ResponseEntity<CarCategoryDto[]> response = restTemplate.getForEntity(CATEGORY_URL, CarCategoryDto[].class);
        List<CarCategoryDto> categories = Arrays.asList(response.getBody());
        categoryComboBox.setItems(categories);
        categoryComboBox.setItemLabelGenerator(CarCategoryDto::getName);
    }

    private void updateTransmissionTypeComboBox() {
        ResponseEntity<TransmissionTypeDto[]> response = restTemplate.getForEntity(TRANSMISSION_TYPE_URL, TransmissionTypeDto[].class);
        List<TransmissionTypeDto> transmissionTypes = Arrays.asList(response.getBody());
        transmissionTypeComboBox.setItems(transmissionTypes);
        transmissionTypeComboBox.setItemLabelGenerator(TransmissionTypeDto::getName);
    }

    private void showCarDetails() {
        CarDto selectedCar = grid.asSingleSelect().getValue();
        if (selectedCar != null) {
            Long carId = selectedCar.getId();
            UI.getCurrent().navigate("car/" + carId);
        } else {
            Notification.show("Please select a car to show details.");
        }
    }

    private void navigateToRentalsPage(ClickEvent<Button> event) {
        UI.getCurrent().getPage().setLocation(RENTALS_NAVIGATE_URL);
    }
}