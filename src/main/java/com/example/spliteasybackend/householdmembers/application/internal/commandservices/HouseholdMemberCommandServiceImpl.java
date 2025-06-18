package com.example.spliteasybackend.householdmembers.application.internal.commandservices;

import com.example.spliteasybackend.householdmembers.domain.models.aggregates.HouseholdMember;
import com.example.spliteasybackend.householdmembers.domain.models.commands.CreateHouseholdMemberCommand;
import com.example.spliteasybackend.householdmembers.domain.services.HouseholdMemberCommandService;
import com.example.spliteasybackend.householdmembers.infrastructure.persistance.jpa.repositories.HouseholdMemberRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HouseholdMemberCommandServiceImpl implements HouseholdMemberCommandService {

    private final HouseholdMemberRepository repository;

    public HouseholdMemberCommandServiceImpl(HouseholdMemberRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<HouseholdMember> handle(CreateHouseholdMemberCommand command) {
        var member = new HouseholdMember(command);
        repository.save(member);
        return Optional.of(member);
    }

    @Override
    public Optional<HouseholdMember> update(Long id, CreateHouseholdMemberCommand command) {
        var optional = repository.findById(id);
        if (optional.isEmpty()) return Optional.empty();

        var member = optional.get();
        member.update(command); // Método que definiremos en el aggregate

        repository.save(member);
        return Optional.of(member);
    }

    @Override
    public boolean delete(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }
}
