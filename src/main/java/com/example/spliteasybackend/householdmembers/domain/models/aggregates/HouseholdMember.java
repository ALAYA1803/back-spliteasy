package com.example.spliteasybackend.householdmembers.domain.models.aggregates;

import com.example.spliteasybackend.households.domain.models.aggregates.Household;
import com.example.spliteasybackend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.example.spliteasybackend.iam.domain.model.aggregates.User;
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
        // Requerido por JPA
    }

    private HouseholdMember(Household household, User user) {
        this.household = household;
        this.user = user;
    }

    public static HouseholdMember create(Household household, User user, boolean alreadyMember) {
        if (!user.isMiembro()) {
            throw new IllegalArgumentException("Solo los usuarios con rol 'MIEMBRO' pueden unirse a un hogar.");
        }

        if (alreadyMember) {
            throw new IllegalStateException("El usuario ya es miembro de este hogar.");
        }

        return new HouseholdMember(household, user);
    }

    public void update(Household household, User user) {
        this.household = household;
        this.user = user;
    }
}
