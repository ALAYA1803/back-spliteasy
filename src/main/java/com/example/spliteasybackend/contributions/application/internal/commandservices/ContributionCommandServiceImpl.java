package com.example.spliteasybackend.contributions.application.internal.commandservices;

import com.example.spliteasybackend.bills.domain.models.aggregates.Bill;
import com.example.spliteasybackend.bills.infrastructure.persistance.jpa.repositories.BillRepository;
import com.example.spliteasybackend.contributions.domain.models.aggregates.Contribution;
import com.example.spliteasybackend.contributions.domain.models.commands.CreateContributionCommand;
import com.example.spliteasybackend.contributions.domain.services.ContributionCommandService;
import com.example.spliteasybackend.contributions.infrastructure.persistance.jpa.repositories.ContributionRepository;
import com.example.spliteasybackend.householdmembers.domain.models.aggregates.HouseholdMember;
import com.example.spliteasybackend.householdmembers.infrastructure.persistance.jpa.repositories.HouseholdMemberRepository;
import com.example.spliteasybackend.households.domain.models.aggregates.Household;
import com.example.spliteasybackend.households.infrastructure.persistance.jpa.repositories.HouseholdRepository;
import com.example.spliteasybackend.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.example.spliteasybackend.membercontributions.infrastructure.persistance.jpa.repositories.MemberContributionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
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

        List<HouseholdMember> members = resolveMembersForDistribution(household, command.memberIds());
        contribution.distribute(members, userRepository, memberContributionRepository);

        return Optional.of(contribution);
    }

    @Override
    @Transactional
    public Optional<Contribution> update(Long id, CreateContributionCommand command) {
        var contributionOpt = contributionRepository.findById(id);
        if (contributionOpt.isEmpty()) return Optional.empty();

        Bill bill = billRepository.findById(command.billId())
                .orElseThrow(() -> new IllegalArgumentException("Bill no encontrado"));

        Household household = householdRepository.findById(command.householdId())
                .orElseThrow(() -> new IllegalArgumentException("Household no encontrado"));

        Contribution contribution = contributionOpt.get();
        contribution.update(command, bill, household);
        contribution = contributionRepository.save(contribution);

        memberContributionRepository.deleteByContribution_Id(contribution.getId());

        List<HouseholdMember> members = resolveMembersForDistribution(household, command.memberIds());
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


    private List<HouseholdMember> resolveMembersForDistribution(Household household, List<Long> memberIds) {
        if (memberIds == null) {
            var all = memberRepository.findAllByHousehold_Id(household.getId());
            if (all.isEmpty()) throw new IllegalStateException("El household no tiene miembros.");
            return all;
        }

        if (memberIds.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un miembro.");
        }

        var members = memberRepository.findAllById(memberIds);
        if (members.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron miembros válidos con los IDs especificados.");
        }

        boolean allBelong = members.stream().allMatch(m -> m.getHousehold() != null
                && m.getHousehold().getId().equals(household.getId()));
        if (!allBelong) throw new IllegalArgumentException("Uno o más miembros no pertenecen al household indicado.");

        long found = members.stream().map(HouseholdMember::getId).distinct().count();
        long requested = memberIds.stream().distinct().count();
        if (found != requested) throw new IllegalArgumentException("Algunos IDs de miembros seleccionados no existen.");

        return members;
    }
}
