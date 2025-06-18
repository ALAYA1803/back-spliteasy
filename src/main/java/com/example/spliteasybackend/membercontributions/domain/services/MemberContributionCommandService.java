// MemberContributionCommandService.java
package com.example.spliteasybackend.membercontributions.domain.services;

import com.example.spliteasybackend.membercontributions.domain.models.aggregates.MemberContribution;
import com.example.spliteasybackend.membercontributions.domain.models.commands.CreateMemberContributionCommand;

import java.util.Optional;

public interface MemberContributionCommandService {
    Optional<MemberContribution> handle(CreateMemberContributionCommand command);
    Optional<MemberContribution> update(Long id, CreateMemberContributionCommand command);
    boolean delete(Long id);
}
