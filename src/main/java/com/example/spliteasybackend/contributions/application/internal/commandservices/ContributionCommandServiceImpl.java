package com.example.spliteasybackend.contributions.application.internal.commandservices;

import com.example.spliteasybackend.bills.domain.models.aggregates.Bill;
import com.example.spliteasybackend.bills.infrastructure.persistance.jpa.repositories.BillRepository;
import com.example.spliteasybackend.contributions.domain.models.aggregates.Contribution;
import com.example.spliteasybackend.contributions.domain.models.commands.CreateContributionCommand;
import com.example.spliteasybackend.contributions.domain.services.ContributionCommandService;
import com.example.spliteasybackend.contributions.infrastructure.persistance.jpa.repositories.ContributionRepository;
import com.example.spliteasybackend.householdmembers.infrastructure.persistance.jpa.repositories.HouseholdMemberRepository;
import com.example.spliteasybackend.households.domain.models.aggregates.Household;
import com.example.spliteasybackend.households.infrastructure.persistance.jpa.repositories.HouseholdRepository;
import com.example.spliteasybackend.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.example.spliteasybackend.membercontributions.infrastructure.persistance.jpa.repositories.MemberContributionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ContributionCommandServiceImpl implements ContributionCommandService {

    private final ContributionRepository contributionRepository;
    private final BillRepository billRepository;
    private final HouseholdRepository householdRepository;
    private final HouseholdMemberRepository memberRepository;
    private final UserRepository userRepository;
    private final MemberContributionRepository memberContributionRepository;

    public ContributionCommandServiceImpl(
            ContributionRepository contributionRepository,
            BillRepository billRepository,
            HouseholdRepository householdRepository,
            HouseholdMemberRepository memberRepository,
            UserRepository userRepository,
            MemberContributionRepository memberContributionRepository
    ) {
        this.contributionRepository = contributionRepository;
        this.billRepository = billRepository;
        this.householdRepository = householdRepository;
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
        this.memberContributionRepository = memberContributionRepository;
    }

    @Override
    @Transactional
    public Optional<Contribution> handle(CreateContributionCommand command) {
        Bill bill = billRepository.findById(command.billId())
                .orElseThrow(() -> new IllegalArgumentException("Bill no encontrado"));

        Household household = householdRepository.findById(command.householdId())
                .orElseThrow(() -> new IllegalArgumentException("Household no encontrado"));

        Contribution contribution = Contribution.create(command, bill, household);
        contribution = contributionRepository.save(contribution);

        var members = memberRepository.findAllByHousehold_Id(household.getId());
        contribution.distribute(members, userRepository, memberContributionRepository);

        return Optional.of(contribution);
    }

    @Override
    @Transactional
    public Optional<Contribution> update(Long id, CreateContributionCommand command) {
        var contributionOpt = contributionRepository.findById(id);
        if (contributionOpt.isEmpty()) return Optional.empty();

        var bill = billRepository.findById(command.billId())
                .orElseThrow(() -> new IllegalArgumentException("Bill no encontrado"));

        var household = householdRepository.findById(command.householdId())
                .orElseThrow(() -> new IllegalArgumentException("Household no encontrado"));

        var contribution = contributionOpt.get();
        contribution.update(command, bill, household);
        contribution = contributionRepository.save(contribution);

        memberContributionRepository.deleteByContribution_Id(contribution.getId());
        var members = memberRepository.findAllByHousehold_Id(household.getId());
        contribution.distribute(members, userRepository, memberContributionRepository);

        return Optional.of(contribution);
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        if (!contributionRepository.existsById(id)) return false;
        memberContributionRepository.deleteByContribution_Id(id);
        contributionRepository.deleteById(id);
        return true;
    }

}
