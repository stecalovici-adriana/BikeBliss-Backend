package com.bb.bikebliss.service.dto;

import java.time.LocalDate;

public record UnavailableDateDTO (
        LocalDate startDate,
        LocalDate endDate
){}
