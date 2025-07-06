package com.example.spliteasybackend.households.application.internal.commandservices;

import com.example.spliteasybackend.households.domain.models.aggregates.Household;
import com.example.spliteasybackend.households.domain.models.commands.CreateHouseholdCommand;
import com.example.spliteasybackend.households.domain.services.HouseholdCommandService;
import com.example.spliteasybackend.households.infrastructure.persistance.jpa.repositories.HouseholdRepository;

import com.example.spliteasybackend.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HouseholdCommandServiceImpl implements HouseholdCommandService {

    private final HouseholdRepository householdRepository;
    private final UserRepository userRepository;

    public HouseholdCommandServiceImpl(HouseholdRepository householdRepository, UserRepository userRepository) {
        this.householdRepository = householdRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<Household> handle(CreateHouseholdCommand command) {
        // ✅ Obtener al representante por ID
        var representante = userRepository.findById(command.representanteId())
                .orElseThrow(() -> new IllegalArgumentException("Representante no encontrado"));

        // ✅ Crear hogar con lógica de negocio
        var household = Household.crear(command, representante);
        householdRepository.save(household);

        return Optional.of(household);
    }

    @Override
    public Optional<Household> update(Long id, CreateHouseholdCommand command) {
        var optional = householdRepository.findById(id);
        if (optional.isEmpty()) return Optional.empty();

        var household = optional.get();

        // ⚠️ Validamos si el representante cambió
        if (!household.getRepresentante().getId().equals(command.representanteId())) {
            var nuevoRepresentante = userRepository.findById(command.representanteId())
                    .orElseThrow(() -> new IllegalArgumentException("Representante no encontrado"));

            household.transferirRepresentacionA(nuevoRepresentante);
        }

        // ✅ Actualiza otros campos (nombre, moneda, descripción)
        household.update(command);
        householdRepository.save(household);

        return Optional.of(household);
    }

    @Override
    public boolean delete(Long id) {
        if (!householdRepository.existsById(id)) return false;
        householdRepository.deleteById(id);
        return true;
    }
}
