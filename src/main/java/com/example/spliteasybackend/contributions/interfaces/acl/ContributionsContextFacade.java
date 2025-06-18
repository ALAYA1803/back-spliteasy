package com.example.spliteasybackend.contributions.interfaces.acl;

import java.time.LocalDate;

public interface ContributionsContextFacade {
    Long createContribution(Long billId, Long householdId, String description, LocalDate fechaLimite, String strategy);
    boolean existsContributionById(Long id);
}
