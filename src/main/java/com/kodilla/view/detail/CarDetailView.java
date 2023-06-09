package com.kodilla.view.detail;

import com.kodilla.domain.dto.CarDto;
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

@Route("car")
public class CarDetailView extends VerticalLayout implements HasUrlParameter<Long> {

    private final RestTemplate restTemplate;

    private Long carId;
    private final Label idLabel = new Label();
    private final Label costPerDayLabel = new Label();
    private final Label modelLabel = new Label();
    private final Label categoryLabel = new Label();
    private final Label transmissionTypeLabel = new Label();
    private final Label availableLabel = new Label();
    private final Label damagedLabel = new Label();
    private final Button backButton = new Button("Back", this::navigateToCarsPage);

    private static final String BASE_URL = "http://localhost:8080/v1/cars";
    private static final String NAVIGATE_URL = "http://localhost:8081/cars";

    public CarDetailView(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        configureComponents();
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Long carId) {
        if (carId != null) {
            this.carId = carId;
            updateCarDetails();
        } else {
            Notification.show("No car ID provided.");
        }
    }

    private void configureComponents() {
        add(idLabel, costPerDayLabel, modelLabel, categoryLabel, transmissionTypeLabel, availableLabel, damagedLabel, backButton);
    }

    private void updateCarDetails() {
        ResponseEntity<CarDto> response = restTemplate.getForEntity(BASE_URL + "/" + carId, CarDto.class);
        CarDto carDto = response.getBody();
        if (carDto != null) {
            idLabel.setText("ID: " + carDto.getId());
            costPerDayLabel.setText("CostPerDay: " + carDto.getCostPerDay());
            modelLabel.setText("Model: " + carDto.getModel());
            categoryLabel.setText("Category: " + (carDto.getCategory() != null ? carDto.getCategory().getName() : ""));
            transmissionTypeLabel.setText("Transmission Type: " + (carDto.getTransmissionType() != null ? carDto.getTransmissionType().getName() : ""));
            availableLabel.setText("Available: " + carDto.isAvailable());
            damagedLabel.setText("Damaged: " + carDto.isDamaged());
        } else {
            Notification.show("Car details not found.");
        }
    }

    private void navigateToCarsPage(ClickEvent<Button> event) {
        UI.getCurrent().getPage().setLocation(NAVIGATE_URL);
    }
}