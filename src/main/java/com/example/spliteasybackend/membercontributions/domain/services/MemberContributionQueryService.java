// MemberContributionQueryService.java
package com.example.spliteasybackend.membercontributions.domain.services;

import com.example.spliteasybackend.membercontributions.domain.models.aggregates.MemberContribution;
import com.example.spliteasybackend.membercontributions.domain.models.queries.GetAllMemberContributionsQuery;
import com.example.spliteasybackend.membercontributions.domain.models.queries.GetMemberContributionByIdQuery;

import java.util.List;
import java.util.Optional;

public interface MemberContributionQueryService {
    Optional<MemberContribution> handle(GetMemberContributionByIdQuery query);
    List<MemberContribution> handle(GetAllMemberContributionsQuery query);
}
