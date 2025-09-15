package com.example.spliteasybackend.contributions.interfaces.acl;

import java.time.LocalDate;
import java.util.List;

public interface ContributionsContextFacade {
    Long createContribution(Long billId, Long householdId, String description, LocalDate fechaLimite, String strategy);

    Long createContribution(Long billId, Long householdId, String description, LocalDate fechaLimite, String strategy, List<Long> memberIds);

    boolean existsContributionById(Long id);
}
