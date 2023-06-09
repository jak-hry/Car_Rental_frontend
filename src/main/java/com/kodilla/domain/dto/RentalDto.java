package com.kodilla.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RentalDto {
    private Long id;
    private CarDto car;
    private CustomerDto customer;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal totalCost;
    private Integer rentalDuration;
    private DamagePenaltyDto damagePenalty;
}