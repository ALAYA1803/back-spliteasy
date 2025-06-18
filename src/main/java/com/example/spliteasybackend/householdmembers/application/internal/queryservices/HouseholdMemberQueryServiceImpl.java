package com.example.spliteasybackend.householdmembers.application.internal.queryservices;

import com.example.spliteasybackend.householdmembers.domain.models.aggregates.HouseholdMember;
import com.example.spliteasybackend.householdmembers.domain.models.queries.GetAllHouseholdMembersQuery;
import com.example.spliteasybackend.householdmembers.domain.models.queries.GetHouseholdMemberByIdQuery;
import com.example.spliteasybackend.householdmembers.domain.services.HouseholdMemberQueryService;
import com.example.spliteasybackend.householdmembers.infrastructure.persistance.jpa.repositories.HouseholdMemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HouseholdMemberQueryServiceImpl implements HouseholdMemberQueryService {

    private final HouseholdMemberRepository repository;

    public HouseholdMemberQueryServiceImpl(HouseholdMemberRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<HouseholdMember> handle(GetHouseholdMemberByIdQuery query) {
        return repository.findById(query.id());
    }

    @Override
    public List<HouseholdMember> handle(GetAllHouseholdMembersQuery query) {
        return repository.findAll();
    }
}
