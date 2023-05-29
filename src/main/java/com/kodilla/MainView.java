package com.kodilla;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

@Route("main")
public class MainView extends VerticalLayout {

    private final RestTemplate restTemplate;

    @Autowired
    public MainView(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

        Button getButton = new Button("Get Data");
        getButton.addClickListener(e -> {
            String url = "http://localhost:8080/v1/customers/1";

            String response = restTemplate.getForObject(url, String.class);

            Notification.show("Response: " + response);
        });

        add(getButton);
    }
}