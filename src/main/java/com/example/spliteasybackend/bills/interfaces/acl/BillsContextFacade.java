package com.example.spliteasybackend.bills.interfaces.acl;

import java.time.LocalDate;

public interface BillsContextFacade {
    Long createBill(Long householdId, String description, double monto, Long createdBy, LocalDate fecha);
    boolean existsBillById(Long id);
}
