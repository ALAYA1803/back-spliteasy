package com.example.spliteasybackend.contributions.domain.services;

import com.example.spliteasybackend.contributions.domain.models.aggregates.Contribution;
import com.example.spliteasybackend.contributions.domain.models.queries.GetContributionByIdQuery;
import com.example.spliteasybackend.contributions.domain.models.queries.GetAllContributionsQuery;

import java.util.List;
import java.util.Optional;

public interface ContributionQueryService {
    Optional<Contribution> handle(GetContributionByIdQuery query);
    List<Contribution> handle(GetAllContributionsQuery query);
}
