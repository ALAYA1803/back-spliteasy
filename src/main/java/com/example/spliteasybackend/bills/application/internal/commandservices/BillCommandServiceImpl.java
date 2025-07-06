package com.example.spliteasybackend.bills.application.internal.commandservices;

import com.example.spliteasybackend.bills.domain.models.aggregates.Bill;
import com.example.spliteasybackend.bills.domain.models.commands.CreateBillCommand;
import com.example.spliteasybackend.bills.domain.services.BillCommandService;
import com.example.spliteasybackend.bills.infrastructure.persistance.jpa.repositories.BillRepository;
import com.example.spliteasybackend.households.infrastructure.persistance.jpa.repositories.HouseholdRepository;
import com.example.spliteasybackend.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BillCommandServiceImpl implements BillCommandService {

    private final BillRepository billRepository;
    private final HouseholdRepository householdRepository;
    private final UserRepository userRepository;

    public BillCommandServiceImpl(
            BillRepository billRepository,
            HouseholdRepository householdRepository,
            UserRepository userRepository
    ) {
        this.billRepository = billRepository;
        this.householdRepository = householdRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<Bill> handle(CreateBillCommand command) {
        var household = householdRepository.findById(command.householdId())
                .orElseThrow(() -> new IllegalArgumentException("Household not found"));

        var creator = userRepository.findById(command.createdBy())
                .orElseThrow(() -> new IllegalArgumentException("User (creator) not found"));

        var bill = Bill.create(command, household, creator);
        billRepository.save(bill);

        return Optional.of(bill);
    }

    @Override
    public Optional<Bill> update(Long id, CreateBillCommand command) {
        var optional = billRepository.findById(id);
        if (optional.isEmpty()) return Optional.empty();

        var household = householdRepository.findById(command.householdId())
                .orElseThrow(() -> new IllegalArgumentException("Household not found"));

        var creator = userRepository.findById(command.createdBy())
                .orElseThrow(() -> new IllegalArgumentException("User (creator) not found"));

        var bill = optional.get();
        bill.update(command, household, creator);
        billRepository.save(bill);

        return Optional.of(bill);
    }

    @Override
    public boolean delete(Long id) {
        if (!billRepository.existsById(id)) return false;
        billRepository.deleteById(id);
        return true;
    }
}
