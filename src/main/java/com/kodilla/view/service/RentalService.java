package com.kodilla.view.service;

import com.kodilla.domain.dto.CarDto;
import com.kodilla.domain.dto.CustomerDto;
import com.kodilla.domain.dto.RentalDto;
import com.kodilla.view.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class RentalService {
    private final Grid<RentalDto> rentalGrid = new Grid<>(RentalDto.class);
    private final ComboBox<Integer> rentalDurationComboBox = new ComboBox<>("Rental Duration (days)");
    private final Button createRentalButton = new Button("Create Rental");
    private final String RENTAL_BASE_URL = "http://localhost:8080/v1/rentals";

    public void configureRentalDurationComboBox(MainView mainView) {
        rentalDurationComboBox.setItems(1, 3, 7, 14);
        rentalDurationComboBox.setLabel("Rental Duration (days)");
        rentalDurationComboBox.setRequired(true);
        rentalDurationComboBox.setValue(1);
        rentalDurationComboBox.addValueChangeListener(event -> mainView.getExchangeRateService().updateExchangeRateLabels(mainView));
    }

    public void configureCreateRentalButton(MainView mainView) {
        createRentalButton.addClickListener(event -> createRental(mainView));
    }

    private void createRental(MainView mainView) {
        CustomerDto selectedCustomer = mainView.getCustomerService().grid.asSingleSelect().getValue();
        CarDto selectedCar = mainView.getCarService().getGrid().asSingleSelect().getValue();
        LocalDateTime currentDateTime = LocalDateTime.now();

        if (selectedCustomer != null && selectedCar != null) {
            RentalDto rentalDto = new RentalDto();
            rentalDto.setCustomer(selectedCustomer);
            rentalDto.setCar(selectedCar);

            Integer rentalDuration = rentalDurationComboBox.getValue();
            rentalDto.setStartDate(currentDateTime);
            LocalDateTime endDate = currentDateTime.plusDays(rentalDuration).minusSeconds(1);
            rentalDto.setEndDate(endDate);
            rentalDto.setRentalDuration(rentalDuration);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<RentalDto> response = restTemplate.postForEntity(RENTAL_BASE_URL, rentalDto, RentalDto.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Notification.show("Rental created successfully.", 3000, Notification.Position.MIDDLE);
                updateRentalGrid();
            } else {
                Notification.show("Failed to create rental. Please try again.", 3000, Notification.Position.MIDDLE);
            }
        } else {
            Notification.show("Please select a customer and car.", 3000, Notification.Position.MIDDLE);
        }
    }

    public void updateRentalGrid() {
        ResponseEntity<RentalDto[]> response = new RestTemplate().getForEntity(RENTAL_BASE_URL, RentalDto[].class);
        List<RentalDto> rentals = Arrays.asList(response.getBody());
        rentalGrid.setItems(rentals);
    }

    public ComboBox<Integer> getRentalDurationComboBox() {
        return rentalDurationComboBox;
    }

    public Button getCreateRentalButton() {
        return createRentalButton;
    }
}