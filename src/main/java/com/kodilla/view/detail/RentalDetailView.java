package com.kodilla.view.detail;

import com.kodilla.domain.dto.RentalDto;
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

@Route("rental")
public class RentalDetailView extends VerticalLayout implements HasUrlParameter<Long> {

    private final RestTemplate restTemplate;
    private Long rentalId;
    private final Label idLabel = new Label();
    private final Label customerLabel = new Label();
    private final Label carLabel = new Label();
    private final Label startDateLabel = new Label();
    private final Label endDateLabel = new Label();
    private final Button backButton = new Button("Back", this::navigateToRentalsPage);
    private static final String BASE_URL = "http://localhost:8080/v1/rentals";
    private static final String NAVIGATE_URL = "http://localhost:8081/rentals";

    public RentalDetailView(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        configureComponents();
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Long rentalId) {
        if (rentalId != null) {
            this.rentalId = rentalId;
            updateRentalDetails();
        } else {
            Notification.show("No rental ID provided.");
        }
    }

    private void configureComponents() {
        add(idLabel, customerLabel, carLabel, startDateLabel, endDateLabel, backButton);
    }

    private void updateRentalDetails() {
        ResponseEntity<RentalDto> response = restTemplate.getForEntity(BASE_URL + "/" + rentalId, RentalDto.class);
        RentalDto rentalDto = response.getBody();
        if (rentalDto != null) {
            idLabel.setText("ID: " + rentalDto.getId());
            customerLabel.setText("Customer: " + (rentalDto.getCustomer() != null ?
                    rentalDto.getCustomer().getFirstName() + " " + rentalDto.getCustomer().getLastName() : ""));
            carLabel.setText("Car: " + (rentalDto.getCar() != null ? rentalDto.getCar().getModel() : ""));
            startDateLabel.setText("Start Date: " + rentalDto.getStartDate().toString());
            endDateLabel.setText("End Date: " + rentalDto.getEndDate().toString());
        } else {
            Notification.show("Rental details not found.");
        }
    }

    private void navigateToRentalsPage(ClickEvent<Button> event) {
        UI.getCurrent().getPage().setLocation(NAVIGATE_URL);
    }
}