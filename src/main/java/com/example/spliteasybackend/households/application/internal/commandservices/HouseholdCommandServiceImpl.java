package com.example.spliteasybackend.households.application.internal.commandservices;

import com.example.spliteasybackend.households.domain.models.aggregates.Household;
import com.example.spliteasybackend.households.domain.models.commands.CreateHouseholdCommand;
import com.example.spliteasybackend.households.domain.services.HouseholdCommandService;
import com.example.spliteasybackend.households.infrastructure.persistance.jpa.repositories.HouseholdRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HouseholdCommandServiceImpl implements HouseholdCommandService {

    private final HouseholdRepository repository;

    public HouseholdCommandServiceImpl(HouseholdRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Household> handle(CreateHouseholdCommand command) {
        var household = new Household(command);
        repository.save(household);
        return Optional.of(household);
    }

    @Override
    public Optional<Household> update(Long id, CreateHouseholdCommand command) {
        var optional = repository.findById(id);
        if (optional.isEmpty()) return Optional.empty();

        var household = optional.get();
        household.update(command); // Este método lo implementaremos en el agregado

        repository.save(household);
        return Optional.of(household);
    }

    @Override
    public boolean delete(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }
}
