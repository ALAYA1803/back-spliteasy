// MemberContributionsContextFacade.java
package com.example.spliteasybackend.membercontributions.interfaces.acl;

import java.time.LocalDateTime;

public interface MemberContributionsContextFacade {
    Long createMemberContribution(Long contributionId, Long memberId, double monto, String status, LocalDateTime pagadoEn);
    boolean existsMemberContributionById(Long id);
}
