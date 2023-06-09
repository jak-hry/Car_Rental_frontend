package com.kodilla.view.detail;

import com.kodilla.domain.dto.CustomerDto;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Route("customer")
public class CustomerDetailView extends VerticalLayout implements HasUrlParameter<Long> {

    private final RestTemplate restTemplate;
    private Long customerId;
    private final Label idLabel = new Label();
    private final Label nameLabel = new Label();
    private final Label emailLabel = new Label();
    private final Label phoneLabel = new Label();
    private final Label addressLabel = new Label();
    private final Button backButton = new Button("Back", this::navigateToCustomersPage);

    private static final String BASE_URL = "http://localhost:8080/v1/customers";
    private static final String NAVIGATE_URL = "http://localhost:8081/customers";

    public CustomerDetailView(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        configureComponents();
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Long customerId) {
        if (customerId != null) {
            this.customerId = customerId;
            updateCustomerDetails();
        } else {
            Notification.show("No customer ID provided.");
        }
    }

    private void configureComponents() {
        add(idLabel, nameLabel, emailLabel, phoneLabel, addressLabel, backButton);
    }

    private void updateCustomerDetails() {
        ResponseEntity<CustomerDto> response = restTemplate.getForEntity(BASE_URL + "/" + customerId, CustomerDto.class);
        CustomerDto customerDto = response.getBody();
        if (customerDto != null) {
            idLabel.setText("ID: " + customerDto.getId());
            nameLabel.setText("First Name: " + customerDto.getFirstName());
            nameLabel.setText("Last Name: " + customerDto.getLastName());
            emailLabel.setText("Email: " + customerDto.getEmail());
            phoneLabel.setText("Phone: " + customerDto.getPhoneNumber());
            addressLabel.setText("Address: " + customerDto.getAddress());
        } else {
            Notification.show("Customer details not found.");
        }
    }

    private void navigateToCustomersPage(ClickEvent<Button> event) {
        UI.getCurrent().getPage().setLocation(NAVIGATE_URL);
    }
}