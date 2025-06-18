package com.example.spliteasybackend.contributions.application.internal.queryservices;

import com.example.spliteasybackend.contributions.domain.models.aggregates.Contribution;
import com.example.spliteasybackend.contributions.domain.models.queries.GetAllContributionsQuery;
import com.example.spliteasybackend.contributions.domain.models.queries.GetContributionByIdQuery;
import com.example.spliteasybackend.contributions.domain.services.ContributionQueryService;
import com.example.spliteasybackend.contributions.infrastructure.persistance.jpa.repositories.ContributionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContributionQueryServiceImpl implements ContributionQueryService {

    private final ContributionRepository repository;

    public ContributionQueryServiceImpl(ContributionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Contribution> handle(GetContributionByIdQuery query) {
        return repository.findById(query.id());
    }

    @Override
    public List<Contribution> handle(GetAllContributionsQuery query) {
        return repository.findAll();
    }
}
