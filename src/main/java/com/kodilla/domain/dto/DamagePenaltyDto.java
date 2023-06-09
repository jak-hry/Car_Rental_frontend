package com.kodilla.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DamagePenaltyDto {
    private Long id;
    private RentalDto rental;
    private String description;
    private BigDecimal amount;
}