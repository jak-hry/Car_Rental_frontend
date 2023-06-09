package com.kodilla.view;

import com.kodilla.domain.dto.CustomerDto;
import com.kodilla.view.service.CarService;
import com.kodilla.view.service.CustomerService;
import com.kodilla.view.service.ExchangeRateService;
import com.kodilla.view.service.RentalService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Getter
@Route("main")
public class MainView extends VerticalLayout {
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();
    private final CarService carService = new CarService();
    private final CustomerService customerService = new CustomerService();
    private final RentalService rentalService = new RentalService();
    Button rentalsButton = new Button("Rentals", event -> navigateToRentalsPage());
    private final String API_CARS_NAVIGATE_URL = "http://localhost:8081/api-cars";
    private final String CUSTOMERS_NAVIGATE_URL = "http://localhost:8081/customers";
    private final String RENTALS_NAVIGATE_URL = "http://localhost:8081/rentals";

    public MainView() {
        configureCustomerGrid();
        carService.configureCarGrid(this);
        rentalService.configureRentalDurationComboBox(this);
        rentalService.configureCreateRentalButton(this);
        addComponents();
        updateCustomerGrid();
        carService.updateCarGrid();
        exchangeRateService.addExchangeRateLabels(this);
        exchangeRateService.updateExchangeRateLabels(this);
        add(rentalsButton);
        if (carService.isCarListEmpty()) {
            Label infoLabel = new Label("The car database is empty. Please populate the car database.");
            Button apiCarsButton = new Button("Go to API Cars", event -> navigateToApiCarsPage());

            add(infoLabel, apiCarsButton);
        }
        if (customerService.isCustomerListEmpty()) {
            Label customerInfoLabel = new Label("The customer database is empty. Please populate the customer database.");
            Button apiCustomersButton = new Button("Go to API Customers", event -> navigateToCustomersPage());

            add(customerInfoLabel, apiCustomersButton);
        }
    }

    public void configureCustomerGrid() {
        customerService.grid.removeAllColumns();
        customerService.grid.addColumn(CustomerDto::getFirstName).setHeader("First Name");
        customerService.grid.addColumn(CustomerDto::getLastName).setHeader("Last Name");
    }

    public void updateCustomerGrid() {
        ResponseEntity<CustomerDto[]> response = new RestTemplate().getForEntity(customerService.getCUSTOMER_BASE_URL(), CustomerDto[].class);
        List<CustomerDto> customers = Arrays.asList(response.getBody());
        customerService.grid.setItems(customers);
    }

    private void addComponents() {
        HorizontalLayout gridLayout = new HorizontalLayout(customerService.grid, carService.getGrid());
        gridLayout.setSizeFull();

        VerticalLayout rentalLayout = new VerticalLayout(rentalService.getRentalDurationComboBox(), rentalService.getCreateRentalButton());
        rentalLayout.setSizeUndefined();

        add(gridLayout, rentalLayout);
    }
    private void navigateToApiCarsPage() {
        UI.getCurrent().navigate(API_CARS_NAVIGATE_URL);
    }

    private void navigateToCustomersPage() {
        UI.getCurrent().navigate(CUSTOMERS_NAVIGATE_URL);
    }

    private void navigateToRentalsPage() {
        UI.getCurrent().navigate(RENTALS_NAVIGATE_URL);
    }
}