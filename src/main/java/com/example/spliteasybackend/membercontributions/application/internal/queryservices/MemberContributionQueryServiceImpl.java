// MemberContributionQueryServiceImpl.java
package com.example.spliteasybackend.membercontributions.application.internal.queryservices;

import com.example.spliteasybackend.membercontributions.domain.models.aggregates.MemberContribution;
import com.example.spliteasybackend.membercontributions.domain.models.queries.GetAllMemberContributionsQuery;
import com.example.spliteasybackend.membercontributions.domain.models.queries.GetMemberContributionByIdQuery;
import com.example.spliteasybackend.membercontributions.domain.services.MemberContributionQueryService;
import com.example.spliteasybackend.membercontributions.infrastructure.persistance.jpa.repositories.MemberContributionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberContributionQueryServiceImpl implements MemberContributionQueryService {

    private final MemberContributionRepository repository;

    public MemberContributionQueryServiceImpl(MemberContributionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<MemberContribution> handle(GetMemberContributionByIdQuery query) {
        return repository.findById(query.id());
    }

    @Override
    public List<MemberContribution> handle(GetAllMemberContributionsQuery query) {
        return repository.findAll();
    }
}
