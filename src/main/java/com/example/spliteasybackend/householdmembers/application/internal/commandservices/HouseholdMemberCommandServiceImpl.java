package com.example.spliteasybackend.householdmembers.application.internal.commandservices;

import com.example.spliteasybackend.householdmembers.domain.models.aggregates.HouseholdMember;
import com.example.spliteasybackend.householdmembers.domain.models.commands.CreateHouseholdMemberCommand;
import com.example.spliteasybackend.householdmembers.domain.services.HouseholdMemberCommandService;
import com.example.spliteasybackend.householdmembers.infrastructure.persistance.jpa.repositories.HouseholdMemberRepository;
import com.example.spliteasybackend.households.infrastructure.persistance.jpa.repositories.HouseholdRepository;
import com.example.spliteasybackend.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HouseholdMemberCommandServiceImpl implements HouseholdMemberCommandService {

    private final HouseholdMemberRepository memberRepository;
    private final HouseholdRepository householdRepository;
    private final UserRepository userRepository;

    public HouseholdMemberCommandServiceImpl(
            HouseholdMemberRepository memberRepository,
            HouseholdRepository householdRepository,
            UserRepository userRepository) {
        this.memberRepository = memberRepository;
        this.householdRepository = householdRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<HouseholdMember> handle(CreateHouseholdMemberCommand command) {
        var household = householdRepository.findById(command.householdId())
                .orElseThrow(() -> new IllegalArgumentException("Household not found"));

        var user = userRepository.findById(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var alreadyMember = memberRepository.existsByHouseholdAndUser(household, user);

        var member = HouseholdMember.create(household, user, alreadyMember);
        memberRepository.save(member);

        return Optional.of(member);
    }

    @Override
    public Optional<HouseholdMember> update(Long id, CreateHouseholdMemberCommand command) {
        var optional = memberRepository.findById(id);
        if (optional.isEmpty()) return Optional.empty();

        var household = householdRepository.findById(command.householdId())
                .orElseThrow(() -> new IllegalArgumentException("Household not found"));

        var user = userRepository.findById(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var member = optional.get();
        member.update(household, user);

        memberRepository.save(member);
        return Optional.of(member);
    }

    @Override
    public boolean delete(Long id) {
        if (!memberRepository.existsById(id)) return false;
        memberRepository.deleteById(id);
        return true;
    }
}
