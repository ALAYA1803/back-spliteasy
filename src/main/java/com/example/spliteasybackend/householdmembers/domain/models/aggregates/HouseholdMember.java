package com.example.spliteasybackend.householdmembers.domain.models.aggregates;

import com.example.spliteasybackend.households.domain.models.aggregates.Household;
import com.example.spliteasybackend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.example.spliteasybackend.user.domain.models.aggregates.User;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class HouseholdMember extends AuditableAbstractAggregateRoot<HouseholdMember> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "household_id", nullable = false)
    private Household household;

    protected HouseholdMember() {
        // Required by JPA
    }

    private HouseholdMember(Household household, User user) {
        this.household = household;
        this.user = user;
    }

    /**
     * Lógica de negocio para crear un miembro válido
     */
    public static HouseholdMember create(Household household, User user, boolean alreadyMember) {
        if (!user.isMiembro()) {
            throw new IllegalArgumentException("Only users with role 'MIEMBRO' can join a household.");
        }

        if (alreadyMember) {
            throw new IllegalStateException("User is already a member of this household.");
        }

        return new HouseholdMember(household, user);
    }

    public void update(Household household, User user) {
        this.household = household;
        this.user = user;
    }
}
