// MemberContributionsContextFacadeImpl.java
package com.example.spliteasybackend.membercontributions.application.acl;

import com.example.spliteasybackend.membercontributions.domain.models.commands.CreateMemberContributionCommand;
import com.example.spliteasybackend.membercontributions.domain.models.queries.GetMemberContributionByIdQuery;
import com.example.spliteasybackend.membercontributions.domain.models.valueobjects.Status;
import com.example.spliteasybackend.membercontributions.domain.services.MemberContributionCommandService;
import com.example.spliteasybackend.membercontributions.domain.services.MemberContributionQueryService;
import com.example.spliteasybackend.membercontributions.interfaces.acl.MemberContributionsContextFacade;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class MemberContributionsContextFacadeImpl implements MemberContributionsContextFacade {

    private final MemberContributionCommandService commandService;
    private final MemberContributionQueryService queryService;

    public MemberContributionsContextFacadeImpl(MemberContributionCommandService commandService,
                                                MemberContributionQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @Override
    public Long createMemberContribution(Long contributionId, Long memberId, double monto, String status, LocalDateTime pagadoEn) {
        var createCommand = new CreateMemberContributionCommand(
                contributionId,
                memberId,
                BigDecimal.valueOf(monto),
                Status.valueOf(status.toUpperCase()),
                pagadoEn
        );
        var result = commandService.handle(createCommand);
        return result.map(mc -> mc.getId()).orElse(0L);
    }

    @Override
    public boolean existsMemberContributionById(Long id) {
        var query = new GetMemberContributionByIdQuery(id);
        return queryService.handle(query).isPresent();
    }
}
