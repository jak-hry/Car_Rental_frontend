package com.kodilla;

import com.kodilla.domain.CarDto;
import com.kodilla.domain.CustomerDto;
import com.kodilla.domain.RentalDto;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Route("main")
public class MainView extends VerticalLayout {

    private final RestTemplate restTemplate;
    private CarDto selectedCar;


    public MainView(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        init();
    }

    CustomerDto selectedCustomer = null;

    private void init() {
        final CustomerDto[] selectedCustomer = {null};
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<List<CarDto>> response = restTemplate.exchange(
                "http://localhost:8080/v1/cars",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CarDto>>() {
                }
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            List<CarDto> cars = response.getBody();

            Grid<CarDto> carGrid = new Grid<>();
            carGrid.setItems(cars);
            carGrid.addColumn(CarDto::getId).setHeader("Car ID");
            carGrid.addColumn(CarDto::getModel).setHeader("Car Model");

            TextField firstNameField = new TextField("First Name");
            TextField lastNameField = new TextField("Last Name");
            TextField addressField = new TextField("Address");
            TextField phoneNumberField = new TextField("Phone Number");
            TextField emailField = new TextField("Email");

            Grid<CustomerDto> customerGrid = new Grid<>();
            customerGrid.addColumn(CustomerDto::getId).setHeader("Customer ID");
            customerGrid.addColumn(CustomerDto::getFirstName).setHeader("First Name");
            customerGrid.addColumn(CustomerDto::getLastName).setHeader("Last Name");
            customerGrid.addColumn(CustomerDto::getPhoneNumber).setHeader("Phone Number");

            Button searchButton = new Button("Search");
            searchButton.addClickListener(e -> {
                String phoneNumber = phoneNumberField.getValue();
                if (!phoneNumber.isEmpty()) {
                    try {
                        ResponseEntity<CustomerDto> customerResponse = restTemplate.getForEntity(
                                "http://localhost:8080/v1/customers/phone/" + phoneNumber,
                                CustomerDto.class
                        );

                        CustomerDto customerDto = customerResponse.getBody();
                        if (customerResponse.getStatusCode() == HttpStatus.OK && customerDto != null) {
                            customerGrid.setItems(Collections.singletonList(customerDto));
                        } else {
                            customerGrid.setItems(Collections.emptyList());
                            Notification.show("No customer found with the provided phone number.");
                        }
                    } catch (Exception ex) {
                        customerGrid.setItems(Collections.emptyList());
                        Notification.show("An error occurred while searching for the customer.");
                        ex.printStackTrace();
                    }
                } else {
                    customerGrid.setItems(Collections.emptyList());
                    Notification.show("Please enter a phone number.");
                }
            });

            carGrid.asSingleSelect().addValueChangeListener(event -> {
                selectedCar = event.getValue();
            });

            customerGrid.asSingleSelect().addValueChangeListener(event -> {
                selectedCustomer[0] = event.getValue();
                firstNameField.setValue(selectedCustomer[0].getFirstName());
                lastNameField.setValue(selectedCustomer[0].getLastName());
                addressField.setValue(selectedCustomer[0].getAddress());
                phoneNumberField.setValue(selectedCustomer[0].getPhoneNumber());
                emailField.setValue(selectedCustomer[0].getEmail());
            });

            Button addButton = new Button("Add");
            addButton.addClickListener(e -> {
                if (selectedCar != null && selectedCustomer[0] != null) {
                    try {
                        RentalDto rentalDto = RentalDto.builder()
                                .car(selectedCar)
                                .customer(selectedCustomer[0])
                                .build();

                        ResponseEntity<RentalDto> createResponse = restTemplate.postForEntity(
                                "http://localhost:8080/v1/rentals",
                                rentalDto,
                                RentalDto.class
                        );

                        if (createResponse.getStatusCode() == HttpStatus.OK) {
                            Notification.show("Car added to the database");
                        } else {
                            Notification.show("An error occurred while adding the car to the database.");
                        }
                    } catch (Exception ex) {
                        Notification.show("An error occurred while adding the car to the database.");
                        ex.printStackTrace();
                    }
                } else {
                    Notification.show("Please select a car and a customer.");
                }
            });

            add(carGrid, firstNameField, lastNameField, addressField, phoneNumberField, emailField, searchButton, customerGrid, addButton);
        } else {
            Notification.show("An error occurred while retrieving data from the external API.");
        }
    }
}