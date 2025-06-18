package com.example.spliteasybackend.bills.domain.services;

import com.example.spliteasybackend.bills.domain.models.aggregates.Bill;
import com.example.spliteasybackend.bills.domain.models.commands.CreateBillCommand;

import java.util.Optional;

public interface BillCommandService {
    Optional<Bill> handle(CreateBillCommand command);
    Optional<Bill> update(Long id, CreateBillCommand command);
    boolean delete(Long id);
}
