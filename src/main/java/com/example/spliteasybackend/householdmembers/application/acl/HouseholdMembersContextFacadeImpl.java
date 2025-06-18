package com.example.spliteasybackend.householdmembers.application.acl;

import com.example.spliteasybackend.householdmembers.domain.models.commands.CreateHouseholdMemberCommand;
import com.example.spliteasybackend.householdmembers.domain.models.queries.GetHouseholdMemberByIdQuery;
import com.example.spliteasybackend.householdmembers.domain.services.HouseholdMemberCommandService;
import com.example.spliteasybackend.householdmembers.domain.services.HouseholdMemberQueryService;
import com.example.spliteasybackend.householdmembers.interfaces.acl.HouseholdMembersContextFacade;
import org.springframework.stereotype.Service;

@Service
public class HouseholdMembersContextFacadeImpl implements HouseholdMembersContextFacade {

    private final HouseholdMemberCommandService commandService;
    private final HouseholdMemberQueryService queryService;

    public HouseholdMembersContextFacadeImpl(HouseholdMemberCommandService commandService,
                                             HouseholdMemberQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @Override
    public Long createHouseholdMember(Long userId, Long householdId) {
        var command = new CreateHouseholdMemberCommand(userId, householdId);
        var result = commandService.handle(command);
        return result.map(hm -> hm.getId()).orElse(0L);
    }

    @Override
    public boolean existsHouseholdMemberById(Long id) {
        var query = new GetHouseholdMemberByIdQuery(id);
        return queryService.handle(query).isPresent();
    }
}
