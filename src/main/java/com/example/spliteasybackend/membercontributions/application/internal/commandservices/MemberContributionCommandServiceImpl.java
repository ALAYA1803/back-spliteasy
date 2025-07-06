// MemberContributionCommandServiceImpl.java
package com.example.spliteasybackend.membercontributions.application.internal.commandservices;

import com.example.spliteasybackend.contributions.domain.models.aggregates.Contribution;
import com.example.spliteasybackend.contributions.infrastructure.persistance.jpa.repositories.ContributionRepository;
import com.example.spliteasybackend.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.example.spliteasybackend.membercontributions.domain.models.aggregates.MemberContribution;
import com.example.spliteasybackend.membercontributions.domain.models.commands.CreateMemberContributionCommand;
import com.example.spliteasybackend.membercontributions.domain.services.MemberContributionCommandService;
import com.example.spliteasybackend.membercontributions.infrastructure.persistance.jpa.repositories.MemberContributionRepository;
import com.example.spliteasybackend.iam.domain.model.aggregates.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberContributionCommandServiceImpl implements MemberContributionCommandService {

    private final MemberContributionRepository memberContributionRepository;
    private final ContributionRepository contributionRepository;
    private final UserRepository userRepository;

    public MemberContributionCommandServiceImpl(
            MemberContributionRepository memberContributionRepository,
            ContributionRepository contributionRepository,
            UserRepository userRepository) {
        this.memberContributionRepository = memberContributionRepository;
        this.contributionRepository = contributionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<MemberContribution> handle(CreateMemberContributionCommand command) {
        Optional<Contribution> contributionOpt = contributionRepository.findById(command.contributionId());
        Optional<User> userOpt = userRepository.findById(command.memberId());

        if (contributionOpt.isEmpty() || userOpt.isEmpty()) return Optional.empty();

        MemberContribution entity = new MemberContribution(
                contributionOpt.get(),
                userOpt.get(),
                command.monto()
        );

        memberContributionRepository.save(entity);
        return Optional.of(entity);
    }

    @Override
    public Optional<MemberContribution> update(Long id, CreateMemberContributionCommand command) {
        var optional = memberContributionRepository.findById(id);
        if (optional.isEmpty()) return Optional.empty();

        var entity = optional.get();
        entity.update(command);

        memberContributionRepository.save(entity);
        return Optional.of(entity);
    }

    @Override
    public boolean delete(Long id) {
        if (!memberContributionRepository.existsById(id)) return false;
        memberContributionRepository.deleteById(id);
        return true;
    }
}
