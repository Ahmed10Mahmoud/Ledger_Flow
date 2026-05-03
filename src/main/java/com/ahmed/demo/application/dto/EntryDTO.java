package com.ahmed.demo.application.dto;

import java.math.BigDecimal;
import java.time.Instant;

public interface EntryDTO {
    BigDecimal getAmount();

    String getType();

    String getDescription();

    BigDecimal getRunningBalance();

    Instant getCreatedAt();
}
