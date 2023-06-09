package com.kodilla.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;
    private String email;
}