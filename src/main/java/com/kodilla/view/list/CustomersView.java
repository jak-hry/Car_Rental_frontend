package com.kodilla.view.list;

import com.kodilla.domain.dto.CustomerDto;
import com.kodilla.view.service.CustomerService;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.Route;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Route("customers")
public class CustomersView extends VerticalLayout {

    private final CustomerService customerService = new CustomerService();
    private final RestTemplate restTemplate;
    private final Button backButton = new Button("Rentals", this::navigateToRentalsPage);
    private static final String RENTALS_NAVIGATE_URL = "http://localhost:8081/rentals";

    public CustomersView() {
        restTemplate = new RestTemplate();

        configureGrid();

        Button createButton = new Button("Create", event -> createCustomer());
        Button updateButton = new Button("Update", event -> updateCustomer());
        Button deleteButton = new Button("Delete", event -> deleteCustomer());

        HorizontalLayout formLayout = new HorizontalLayout(customerService.firstNameField,
                customerService.lastNameField, customerService.addressField,
                customerService.phoneNumberField, customerService.emailField,
                createButton, updateButton, deleteButton);
        formLayout.setAlignItems(Alignment.BASELINE);

        Button showDetailsButton = new Button("Show Details", event -> showCustomerDetails());

        add(customerService.grid, formLayout, backButton, showDetailsButton, customerService.searchByFirstNameButton,
                customerService.searchByLastNameButton, customerService.searchByPhoneNumberButton);
    }

    private void configureGrid() {
        customerService.grid.removeAllColumns();
        customerService.grid.addColumn(CustomerDto::getFirstName).setHeader("First Name");
        customerService.grid.addColumn(CustomerDto::getLastName).setHeader("Last Name");
        customerService.grid.addColumn(CustomerDto::getAddress).setHeader("Address");
        customerService.grid.addColumn(CustomerDto::getPhoneNumber).setHeader("Phone Number");
        customerService.grid.addColumn(CustomerDto::getEmail).setHeader("Email");

        customerService.grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                CustomerDto selectedCustomer = event.getValue();
                customerService.firstNameField.setValue(selectedCustomer.getFirstName());
                customerService.lastNameField.setValue(selectedCustomer.getLastName());
                customerService.addressField.setValue(selectedCustomer.getAddress());
                customerService.phoneNumberField.setValue(selectedCustomer.getPhoneNumber());
                customerService.emailField.setValue(selectedCustomer.getEmail());
            } else {
                customerService.clearFields();
            }
        });
    }

    private void createCustomer() {
        String firstName = customerService.firstNameField.getValue();
        String lastName = customerService.lastNameField.getValue();
        String address = customerService.addressField.getValue();
        String phoneNumber = customerService.phoneNumberField.getValue();
        String email = customerService.emailField.getValue();

        CustomerDto newCustomer = new CustomerDto();
        newCustomer.setFirstName(firstName);
        newCustomer.setLastName(lastName);
        newCustomer.setAddress(address);
        newCustomer.setPhoneNumber(phoneNumber);
        newCustomer.setEmail(email);

        ResponseEntity<CustomerDto> response = restTemplate.postForEntity(customerService.getBASE_URL(), newCustomer, CustomerDto.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            Notification.show("Customer created successfully.");
            updateGrid();
            customerService.clearFields();
        } else {
            Notification.show("Failed to create customer. Please try again.");
        }
    }

    private void updateGrid() {
        ResponseEntity<CustomerDto[]> response = restTemplate.getForEntity(customerService.getBASE_URL(), CustomerDto[].class);
        List<CustomerDto> customers = Arrays.asList(response.getBody());

        if (customers.size() > customerService.grid.getDataProvider().fetch(new Query<>()).toList().size()) {
            CustomerDto newCustomer = customers.get(customers.size() - 1);
            customerService.grid.setItems(Collections.singletonList(newCustomer));
            customerService.grid.select(newCustomer);
        } else {
            customerService.grid.setItems(customers);
        }
    }

    private void deleteCustomer() {
        CustomerDto selectedCustomer = customerService.grid.asSingleSelect().getValue();
        if (selectedCustomer != null) {
            String deleteUrl = customerService.getBASE_URL() + "/" + selectedCustomer.getId();
            restTemplate.delete(deleteUrl);
            Notification.show("Customer deleted successfully.");
            updateGrid();
            customerService.clearFields();
        } else {
            Notification.show("No customer selected.");
        }
    }

    private void updateCustomer() {
        CustomerDto selectedCustomer = customerService.grid.asSingleSelect().getValue();
        if (selectedCustomer != null) {
            selectedCustomer.setFirstName(customerService.firstNameField.getValue());
            selectedCustomer.setLastName(customerService.lastNameField.getValue());
            selectedCustomer.setAddress(customerService.emailField.getValue());
            selectedCustomer.setPhoneNumber(customerService.phoneNumberField.getValue());
            selectedCustomer.setEmail(customerService.emailField.getValue());

            String updateUrl = customerService.getBASE_URL() + "/" + selectedCustomer.getId();
            HttpEntity<CustomerDto> requestEntity = new HttpEntity<>(selectedCustomer);
            ResponseEntity<CustomerDto> response = restTemplate.exchange(updateUrl, HttpMethod.PUT, requestEntity, CustomerDto.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                Notification.show("Customer updated successfully.");
                updateGrid();
                customerService.clearFields();
            } else {
                Notification.show("Failed to update customer. Please try again.");
            }
        } else {
            Notification.show("No customer selected.");
        }
    }

    private void showCustomerDetails() {
        CustomerDto selectedCustomer = customerService.grid.asSingleSelect().getValue();
        if (selectedCustomer != null) {
            Long customerId = selectedCustomer.getId();
            UI.getCurrent().navigate("customer/" + customerId);
        } else {
            Notification.show("Please select a customer to show details.");
        }
    }

    private void navigateToRentalsPage(ClickEvent<Button> event) {
        UI.getCurrent().getPage().setLocation(RENTALS_NAVIGATE_URL);
    }
}