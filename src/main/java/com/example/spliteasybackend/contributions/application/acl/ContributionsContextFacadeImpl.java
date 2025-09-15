package com.example.spliteasybackend.contributions.application.acl;

import com.example.spliteasybackend.contributions.domain.models.commands.CreateContributionCommand;
import com.example.spliteasybackend.contributions.domain.models.queries.GetContributionByIdQuery;
import com.example.spliteasybackend.contributions.domain.models.valueobjects.Strategy;
import com.example.spliteasybackend.contributions.domain.services.ContributionCommandService;
import com.example.spliteasybackend.contributions.domain.services.ContributionQueryService;
import com.example.spliteasybackend.contributions.interfaces.acl.ContributionsContextFacade;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ContributionsContextFacadeImpl implements ContributionsContextFacade {

    private final ContributionCommandService commandService;
    private final ContributionQueryService queryService;

    public ContributionsContextFacadeImpl(ContributionCommandService commandService, ContributionQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @Override
    public Long createContribution(Long billId, Long householdId, String description, LocalDate fechaLimite, String strategy) {
        return createContribution(billId, householdId, description, fechaLimite, strategy, null);
    }

    @Override
    public Long createContribution(Long billId, Long householdId, String description, LocalDate fechaLimite, String strategy, List<Long> memberIds) {
        var command = new CreateContributionCommand(
                billId,
                householdId,
                description,
                fechaLimite,
                Strategy.valueOf(strategy.toUpperCase()),
                memberIds
        );
        var result = commandService.handle(command);
        return result.map(c -> c.getId()).orElse(0L);
    }

    @Override
    public boolean existsContributionById(Long id) {
        var query = new GetContributionByIdQuery(id);
        return queryService.handle(query).isPresent();
    }
}
