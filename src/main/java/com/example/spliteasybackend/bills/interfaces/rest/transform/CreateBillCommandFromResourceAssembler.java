package com.example.spliteasybackend.bills.interfaces.rest.transform;

import com.example.spliteasybackend.bills.domain.models.commands.CreateBillCommand;
import com.example.spliteasybackend.bills.domain.models.valueobjects.Money;
import com.example.spliteasybackend.bills.interfaces.rest.resources.CreateBillResource;

public class CreateBillCommandFromResourceAssembler {

    public static CreateBillCommand toCommandFromResource(CreateBillResource resource) {
        return new CreateBillCommand(
                resource.householdId(),
                resource.description(),
                new Money(resource.monto()),
                resource.createdBy(),
                resource.fecha()
        );
    }
}
