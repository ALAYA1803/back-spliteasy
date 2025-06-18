package com.example.spliteasybackend.households.application.internal.queryservices;

import com.example.spliteasybackend.households.domain.models.aggregates.Household;
import com.example.spliteasybackend.households.domain.models.queries.GetAllHouseholdsQuery;
import com.example.spliteasybackend.households.domain.models.queries.GetHouseholdByIdQuery;
import com.example.spliteasybackend.households.domain.services.HouseholdQueryService;
import com.example.spliteasybackend.households.infrastructure.persistance.jpa.repositories.HouseholdRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HouseholdQueryServiceImpl implements HouseholdQueryService {

    private final HouseholdRepository repository;

    public HouseholdQueryServiceImpl(HouseholdRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Household> handle(GetHouseholdByIdQuery query) {
        return repository.findById(query.id());
    }

    @Override
    public List<Household> handle(GetAllHouseholdsQuery query) {
        return repository.findAll();
    }
}
