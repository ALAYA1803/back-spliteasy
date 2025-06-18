package com.example.spliteasybackend.bills.application.internal.commandservices;

import com.example.spliteasybackend.bills.domain.models.aggregates.Bill;
import com.example.spliteasybackend.bills.domain.models.commands.CreateBillCommand;
import com.example.spliteasybackend.bills.domain.services.BillCommandService;
import com.example.spliteasybackend.bills.infrastructure.persistance.jpa.repositories.BillRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BillCommandServiceImpl implements BillCommandService {

    private final BillRepository repository;

    public BillCommandServiceImpl(BillRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Bill> handle(CreateBillCommand command) {
        var bill = new Bill(command);
        repository.save(bill);
        return Optional.of(bill);
    }

    @Override
    public Optional<Bill> update(Long id, CreateBillCommand command) {
        var optional = repository.findById(id);
        if (optional.isEmpty()) return Optional.empty();

        var bill = optional.get();
        bill.update(command); // Método definido en el aggregate

        repository.save(bill);
        return Optional.of(bill);
    }

    @Override
    public boolean delete(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }
}
