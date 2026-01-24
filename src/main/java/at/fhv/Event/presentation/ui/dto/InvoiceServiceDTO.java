package at.fhv.Event.presentation.ui.dto;

import java.math.BigDecimal;

public record InvoiceServiceDTO(
        Long equipmentId,
        String name,
        BigDecimal unitPrice
) {}
