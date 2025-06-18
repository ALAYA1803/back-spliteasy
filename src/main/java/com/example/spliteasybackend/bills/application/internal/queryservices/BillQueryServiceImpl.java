package com.example.spliteasybackend.bills.application.internal.queryservices;

import com.example.spliteasybackend.bills.domain.models.aggregates.Bill;
import com.example.spliteasybackend.bills.domain.models.queries.GetAllBillsQuery;
import com.example.spliteasybackend.bills.domain.models.queries.GetBillByIdQuery;
import com.example.spliteasybackend.bills.domain.services.BillQueryService;
import com.example.spliteasybackend.bills.infrastructure.persistance.jpa.repositories.BillRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BillQueryServiceImpl implements BillQueryService {

    private final BillRepository repository;

    public BillQueryServiceImpl(BillRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Bill> handle(GetBillByIdQuery query) {
        return repository.findById(query.id());
    }

    @Override
    public List<Bill> handle(GetAllBillsQuery query) {
        return repository.findAll();
    }
}
