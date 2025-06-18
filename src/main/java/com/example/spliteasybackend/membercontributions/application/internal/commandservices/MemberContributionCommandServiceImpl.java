// MemberContributionCommandServiceImpl.java
package com.example.spliteasybackend.membercontributions.application.internal.commandservices;

import com.example.spliteasybackend.membercontributions.domain.models.aggregates.MemberContribution;
import com.example.spliteasybackend.membercontributions.domain.models.commands.CreateMemberContributionCommand;
import com.example.spliteasybackend.membercontributions.domain.services.MemberContributionCommandService;
import com.example.spliteasybackend.membercontributions.infrastructure.persistance.jpa.repositories.MemberContributionRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberContributionCommandServiceImpl implements MemberContributionCommandService {

    private final MemberContributionRepository repository;

    public MemberContributionCommandServiceImpl(MemberContributionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<MemberContribution> handle(CreateMemberContributionCommand command) {
        var entity = new MemberContribution(command);
        repository.save(entity);
        return Optional.of(entity);
    }

    @Override
    public Optional<MemberContribution> update(Long id, CreateMemberContributionCommand command) {
        var optional = repository.findById(id);
        if (optional.isEmpty()) return Optional.empty();

        var entity = optional.get();

        entity.update(command); // Asegúrate de tener este método implementado en el agregado

        repository.save(entity);
        return Optional.of(entity);
    }

    @Override
    public boolean delete(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }
}
