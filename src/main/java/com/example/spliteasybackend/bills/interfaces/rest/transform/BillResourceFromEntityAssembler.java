package com.example.spliteasybackend.bills.interfaces.rest.transform;

import com.example.spliteasybackend.bills.domain.models.aggregates.Bill;
import com.example.spliteasybackend.bills.interfaces.rest.resources.BillResource;

import java.math.BigDecimal;

public class BillResourceFromEntityAssembler {

    public static BillResource toResourceFromEntity(Bill entity) {
        return new BillResource(
                entity.getId(),
                entity.getHousehold() != null ? entity.getHousehold().getId() : null,
                entity.getDescription(),
                entity.getMonto() != null ? entity.getMonto().value() : BigDecimal.ZERO, /// Extraer BigDecimal desde el value object Money
                entity.getCreatedBy() != null ? entity.getCreatedBy().getId() : null,
                entity.getFecha()
        );
    }
}
