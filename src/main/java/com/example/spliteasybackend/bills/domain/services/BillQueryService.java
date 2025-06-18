package com.example.spliteasybackend.bills.domain.services;

import com.example.spliteasybackend.bills.domain.models.aggregates.Bill;
import com.example.spliteasybackend.bills.domain.models.queries.GetAllBillsQuery;
import com.example.spliteasybackend.bills.domain.models.queries.GetBillByIdQuery;

import java.util.List;
import java.util.Optional;

public interface BillQueryService {
    Optional<Bill> handle(GetBillByIdQuery query);
    List<Bill> handle(GetAllBillsQuery query);
}
