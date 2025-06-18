package com.example.spliteasybackend.bills.interfaces.rest.transform;

import com.example.spliteasybackend.bills.domain.models.aggregates.Bill;
import com.example.spliteasybackend.bills.interfaces.rest.resources.BillResource;

import java.math.BigDecimal;

public class BillResourceFromEntityAssembler {

    public static BillResource toResourceFromEntity(Bill entity) {
        return new BillResource(
                entity.getId(),
                entity.getHouseholdId(),
                entity.getDescription(),
                entity.getMonto() != null ? entity.getMonto().value() : BigDecimal.ZERO, /// Extraer BigDecimal desde el value object Money
                entity.getCreatedBy(),
                entity.getFecha()
        );
    }
}
