package com.kodilla.view.list;

import com.kodilla.domain.dto.CarDto;
import com.kodilla.domain.dto.CustomerDto;
import com.kodilla.domain.dto.DamagePenaltyDto;
import com.kodilla.domain.dto.RentalDto;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Route("rentals")
public class RentalsView extends VerticalLayout {

    private final RestTemplate restTemplate;
    private final Grid<RentalDto> grid = new Grid<>(RentalDto.class);
    private final Button showCustomerDetailsButton = new Button("Show Customer Details", event -> showCustomerDetails());
    private final Button showCarDetailsButton = new Button("Show Car Details", event -> showCarDetails());
    private static final String CUSTOMER_NAVIGATE_URL = "http://localhost:8081/customer";
    private static final String CAR_NAVIGATE_URL = "http://localhost:8081/car";
    private static final String MAIN_MENU_NAVIGATE_URL = "http://localhost:8081/main";
    private static final String BASE_URL = "http://localhost:8080/v1/rentals";
    private static final String DAMAGE_PENALTY_BASE_URL = "http://localhost:8080/v1/damage-penalties";
    private final Button backButton = new Button("Back", this::navigateToMainPage);
    private final Button addDamagePenaltyButton = new Button("Add Damage Penalty", event -> addDamagePenalty());
    private final Button removeDamagePenaltyButton = new Button("Remove Damage Penalty", event -> removeDamagePenalty());


    public RentalsView() {
        restTemplate = new RestTemplate();

        configureGrid();
        updateGrid();
        add(grid, showCustomerDetailsButton, showCarDetailsButton, addDamagePenaltyButton, removeDamagePenaltyButton, backButton);
    }

    private void configureGrid() {
        grid.removeAllColumns();
        grid.addColumn(RentalDto::getId).setHeader("ID");
        grid.addColumn(rental -> rental.getCar().getModel()).setHeader("Car");
        grid.addColumn(rental -> rental.getCustomer().getFirstName()).setHeader("Customer");
        grid.addColumn(RentalDto::getStartDate).setHeader("Start Date");
        grid.addColumn(RentalDto::getEndDate).setHeader("End Date");
        grid.addColumn(RentalDto::getTotalCost).setHeader("Total Cost");
        grid.addColumn(RentalDto::getRentalDuration).setHeader("Rental Duration");
        grid.addColumn(rental -> rental.getDamagePenalty() != null ? rental.getDamagePenalty().getAmount() : "").setHeader("Damage Penalty");
        grid.asSingleSelect().addValueChangeListener(event -> {
            updateButtonState();
            updateDamagePenaltyButtonState();
            updateRemoveDamagePenaltyButtonState();
        });

        grid.asSingleSelect().addValueChangeListener(event -> updateButtonState());
    }

    private void updateGrid() {
        ResponseEntity<RentalDto[]> response = restTemplate.getForEntity(BASE_URL, RentalDto[].class);
        List<RentalDto> rentals = Arrays.asList(response.getBody());
        grid.setItems(rentals);
    }

    private void updateButtonState() {
        RentalDto selectedRental = grid.asSingleSelect().getValue();
        boolean rentalSelected = (selectedRental != null);

        showCustomerDetailsButton.setEnabled(rentalSelected);
        showCarDetailsButton.setEnabled(rentalSelected);
        updateDamagePenaltyButtonState();
        updateRemoveDamagePenaltyButtonState();
    }

    private void updateDamagePenaltyButtonState() {
        RentalDto selectedRental = grid.asSingleSelect().getValue();
        boolean rentalSelected = (selectedRental != null);
        boolean damagePenaltyExists = (selectedRental != null && selectedRental.getDamagePenalty() != null);

        addDamagePenaltyButton.setEnabled(rentalSelected && !damagePenaltyExists);
    }

    private void addDamagePenalty() {
        RentalDto selectedRental = grid.asSingleSelect().getValue();
        if (selectedRental != null) {
            if (selectedRental.getDamagePenalty() != null) {
                Notification.show("Damage penalty already exists for this rental.", 3000, Notification.Position.MIDDLE);
            } else {
                DamagePenaltyDto damagePenaltyDto = new DamagePenaltyDto();
                damagePenaltyDto.setRental(selectedRental);

                showDamagePenaltyFormDialog(damagePenaltyDto);
            }
        } else {
            Notification.show("Please select a rental to add a damage penalty.");
        }
    }

    private void updateRemoveDamagePenaltyButtonState() {
        RentalDto selectedRental = grid.asSingleSelect().getValue();
        boolean damagePenaltyExists = (selectedRental != null && selectedRental.getDamagePenalty() != null);

        removeDamagePenaltyButton.setEnabled(damagePenaltyExists);
    }

    private void removeDamagePenalty() {
        RentalDto selectedRental = grid.asSingleSelect().getValue();
        if (selectedRental != null && selectedRental.getDamagePenalty() != null) {
            Long damagePenaltyId = selectedRental.getDamagePenalty().getId();
            restTemplate.delete(DAMAGE_PENALTY_BASE_URL + "/" + damagePenaltyId);

            selectedRental.setDamagePenalty(null);
            grid.getDataProvider().refreshItem(selectedRental);
            updateGrid();

            Notification.show("Damage penalty removed successfully.", 3000, Notification.Position.MIDDLE);
        }
    }

    private void showDamagePenaltyFormDialog(DamagePenaltyDto damagePenaltyDto) {
        Dialog dialog = new Dialog();
        FormLayout formLayout = new FormLayout();

        TextField descriptionField = new TextField("Description");
        BigDecimalField amountField = new BigDecimalField("Amount");

        Button saveButton = new Button("Save", event -> {
            String description = descriptionField.getValue();
            BigDecimal amount = amountField.getValue();

            damagePenaltyDto.setDescription(description);
            damagePenaltyDto.setAmount(amount);

            saveDamagePenalty(damagePenaltyDto);

            dialog.close();
        });

        Button cancelButton = new Button("Cancel", event -> dialog.close());

        formLayout.add(descriptionField, amountField);
        dialog.add(formLayout, new HorizontalLayout(saveButton, cancelButton));
        dialog.open();
    }

    private void saveDamagePenalty(DamagePenaltyDto damagePenaltyDto) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<DamagePenaltyDto> response = restTemplate.postForEntity(DAMAGE_PENALTY_BASE_URL, damagePenaltyDto, DamagePenaltyDto.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Notification.show("Damage penalty added successfully.", 3000, Notification.Position.MIDDLE);
            updateGrid();
        } else {
            Notification.show("Failed to add damage penalty. Please try again.", 3000, Notification.Position.MIDDLE);
        }
    }

    private void showCustomerDetails() {
        RentalDto selectedRental = grid.asSingleSelect().getValue();
        if (selectedRental != null) {
            CustomerDto selectedCustomer = selectedRental.getCustomer();
            if (selectedCustomer != null) {
                Long customerId = selectedCustomer.getId();
                UI.getCurrent().navigate(CUSTOMER_NAVIGATE_URL + "/" + customerId);
            } else {
                Notification.show("No customer details available for the selected rental.");
            }
        } else {
            Notification.show("Please select a rental to show customer details.");
        }
    }

    private void showCarDetails() {
        RentalDto selectedRental = grid.asSingleSelect().getValue();
        if (selectedRental != null) {
            CarDto selectedCar = selectedRental.getCar();
            if (selectedCar != null) {
                Long carId = selectedCar.getId();
                UI.getCurrent().navigate(CAR_NAVIGATE_URL + "/" + carId);
            } else {
                Notification.show("No car details available for the selected rental.");
            }
        } else {
            Notification.show("Please select a rental to show car details.");
        }
    }

    private void navigateToMainPage(ClickEvent<Button> event) {
        UI.getCurrent().getPage().setLocation(MAIN_MENU_NAVIGATE_URL);
    }
}