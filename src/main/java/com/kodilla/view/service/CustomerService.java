package com.kodilla.view.service;

import com.kodilla.domain.dto.CustomerDto;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Getter
public class CustomerService {
    private final String CUSTOMER_BASE_URL = "http://localhost:8080/v1/customers";
    private final String BASE_URL = "http://localhost:8080/v1/customers";
    public final Grid<CustomerDto> grid = new Grid<>(CustomerDto.class);
    private final RestTemplate restTemplate = new RestTemplate();
    public final Button searchByLastNameButton = new Button("Search by Last Name", event -> getCustomersByLastName());
    public final Button searchByFirstNameButton = new Button("Search by First Name", event -> getCustomersByFirstName());
    public final Button searchByPhoneNumberButton = new Button("Search by Phone Number", event -> getCustomerByPhoneNumber());
    public final TextField firstNameField = new TextField("First Name");
    public final TextField lastNameField = new TextField("Last Name");
    public final TextField addressField = new TextField("Address");
    public final TextField phoneNumberField = new TextField("Phone Number");
    public final TextField emailField = new TextField("Email");

    public void getCustomersByLastName() {
        String lastName = lastNameField.getValue();
        if (!lastName.isEmpty()) {
            String url = BASE_URL + "?lastName=" + lastName;
            ResponseEntity<CustomerDto[]> response = restTemplate.getForEntity(url, CustomerDto[].class);
            List<CustomerDto> customers = Arrays.asList(response.getBody());
            grid.setItems(customers);
            clearFields();
        } else {
            Notification.show("Please enter a last name to search.");
        }
    }

    public void getCustomersByFirstName() {
        String firstName = firstNameField.getValue();
        if (!firstName.isEmpty()) {
            String url = BASE_URL + "?firstName=" + firstName;
            ResponseEntity<CustomerDto[]> response = restTemplate.getForEntity(url, CustomerDto[].class);
            List<CustomerDto> customers = Arrays.asList(response.getBody());
            grid.setItems(customers);
            clearFields();
        } else {
            Notification.show("Please enter a first name to search.");
        }
    }

    public void getCustomerByPhoneNumber() {
        String phoneNumber = phoneNumberField.getValue();
        if (!phoneNumber.isEmpty()) {
            String url = BASE_URL + "?phoneNumber=" + phoneNumber;
            ResponseEntity<CustomerDto[]> response = restTemplate.getForEntity(url, CustomerDto[].class);
            List<CustomerDto> customers = Arrays.asList(response.getBody());
            grid.setItems(customers);
            clearFields();
        } else {
            Notification.show("Please enter a phone number to search.");
        }
    }

    public boolean isCustomerListEmpty() {
        ListDataProvider<CustomerDto> dataProvider = (ListDataProvider<CustomerDto>) grid.getDataProvider();
        return dataProvider.getItems().isEmpty();
    }

    public void clearFields() {
        firstNameField.clear();
        lastNameField.clear();
        addressField.clear();
        phoneNumberField.clear();
        emailField.clear();
    }
}
