package com.kodilla.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarDto {
    private Long id;
    private String model;
    private boolean available;
    private CarCategoryDto category;
    private TransmissionTypeDto transmissionType;
    private BigDecimal costPerDay;
    private List<RentalDto> rentals;
    private boolean damaged;
}