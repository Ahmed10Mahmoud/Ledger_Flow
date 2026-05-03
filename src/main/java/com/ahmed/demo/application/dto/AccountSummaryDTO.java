package com.ahmed.demo.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public interface AccountSummaryDTO {
    UUID getId();

    String getName();
    String getType();

    BigDecimal getBalance();

    String getCurrency();
}
