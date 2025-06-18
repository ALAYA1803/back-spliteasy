package com.example.spliteasybackend.householdmembers.domain.models.aggregates;

import com.example.spliteasybackend.householdmembers.domain.models.commands.CreateHouseholdMemberCommand;
import com.example.spliteasybackend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class HouseholdMember extends AuditableAbstractAggregateRoot<HouseholdMember> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long householdId;

    public HouseholdMember(CreateHouseholdMemberCommand command) {
        this.userId = command.userId();
        this.householdId = command.householdId();
    }

    public HouseholdMember() {
        // Constructor requerido por JPA
    }

    public void update(CreateHouseholdMemberCommand command) {
        this.userId = command.userId();
        this.householdId = command.householdId();
    }
}
