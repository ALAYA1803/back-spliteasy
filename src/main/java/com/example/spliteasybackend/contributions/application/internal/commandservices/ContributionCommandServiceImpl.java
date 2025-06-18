package com.example.spliteasybackend.contributions.application.internal.commandservices;

import com.example.spliteasybackend.contributions.domain.models.aggregates.Contribution;
import com.example.spliteasybackend.contributions.domain.models.commands.CreateContributionCommand;
import com.example.spliteasybackend.contributions.domain.services.ContributionCommandService;
import com.example.spliteasybackend.contributions.infrastructure.persistance.jpa.repositories.ContributionRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ContributionCommandServiceImpl implements ContributionCommandService {

    private final ContributionRepository repository;

    public ContributionCommandServiceImpl(ContributionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Contribution> handle(CreateContributionCommand command) {
        var contribution = new Contribution(command);
        repository.save(contribution);
        return Optional.of(contribution);
    }

    @Override
    public Optional<Contribution> update(Long id, CreateContributionCommand command) {
        var optional = repository.findById(id);
        if (optional.isEmpty()) return Optional.empty();

        var contribution = optional.get();

        contribution.update(command); // Método definido en el agregado

        repository.save(contribution);
        return Optional.of(contribution);
    }

    @Override
    public boolean delete(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }
}
