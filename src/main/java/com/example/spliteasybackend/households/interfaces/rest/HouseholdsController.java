package com.example.spliteasybackend.households.interfaces.rest;

import com.example.spliteasybackend.contributions.infrastructure.persistance.jpa.repositories.ContributionRepository;
import com.example.spliteasybackend.contributions.interfaces.rest.resources.ContributionResource;
import com.example.spliteasybackend.contributions.interfaces.rest.transform.ContributionResourceFromEntityAssembler;
import com.example.spliteasybackend.householdmembers.infrastructure.persistance.jpa.repositories.HouseholdMemberRepository;
import com.example.spliteasybackend.householdmembers.interfaces.rest.resources.HouseholdMemberResource;
import com.example.spliteasybackend.householdmembers.interfaces.rest.transform.HouseholdMemberResourceFromEntityAssembler;
import com.example.spliteasybackend.households.domain.models.queries.GetAllHouseholdsQuery;
import com.example.spliteasybackend.households.domain.models.queries.GetHouseholdByIdQuery;
import com.example.spliteasybackend.households.domain.services.HouseholdCommandService;
import com.example.spliteasybackend.households.domain.services.HouseholdQueryService;
import com.example.spliteasybackend.households.interfaces.rest.resources.CreateHouseholdResource;
import com.example.spliteasybackend.households.interfaces.rest.resources.HouseholdResource;
import com.example.spliteasybackend.households.interfaces.rest.transform.CreateHouseholdCommandFromResourceAssembler;
import com.example.spliteasybackend.households.interfaces.rest.transform.HouseholdResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/households", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Households", description = "Available Household Endpoints")
public class HouseholdsController {

    private final HouseholdCommandService commandService;
    private final HouseholdQueryService queryService;

    private final HouseholdMemberRepository householdMemberRepository;
    private final ContributionRepository contributionRepository;

    public HouseholdsController(HouseholdCommandService commandService,
                                HouseholdQueryService queryService,
                                HouseholdMemberRepository householdMemberRepository,
                                ContributionRepository contributionRepository) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.householdMemberRepository = householdMemberRepository;
        this.contributionRepository = contributionRepository;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_REPRESENTANTE')")
    @Operation(summary = "Create a household")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Household created"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<HouseholdResource> createHousehold(@RequestBody CreateHouseholdResource resource) {
        var command = CreateHouseholdCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = commandService.handle(command);
        if (result.isEmpty()) return ResponseEntity.badRequest().build();
        var responseResource = HouseholdResourceFromEntityAssembler.toResourceFromEntity(result.get());
        return new ResponseEntity<>(responseResource, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all households")
    public ResponseEntity<List<HouseholdResource>> getAllHouseholds() {
        var results = queryService.handle(new GetAllHouseholdsQuery());
        var resources = results.stream()
                .map(HouseholdResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{householdId}")
    @Operation(summary = "Get household by ID")
    public ResponseEntity<HouseholdResource> getHouseholdById(@PathVariable Long householdId) {
        var query = new GetHouseholdByIdQuery(householdId);
        var result = queryService.handle(query);
        if (result.isEmpty()) return ResponseEntity.notFound().build();
        var resource = HouseholdResourceFromEntityAssembler.toResourceFromEntity(result.get());
        return ResponseEntity.ok(resource);
    }

    @PutMapping("/{householdId}")
    @Operation(summary = "Update household by ID")
    public ResponseEntity<HouseholdResource> updateHouseholdById(@PathVariable Long householdId,
                                                                 @RequestBody CreateHouseholdResource resource) {
        var command = CreateHouseholdCommandFromResourceAssembler.toCommandFromResource(resource);
        var updated = commandService.update(householdId, command);
        if (updated.isEmpty()) return ResponseEntity.notFound().build();
        var resourceUpdated = HouseholdResourceFromEntityAssembler.toResourceFromEntity(updated.get());
        return ResponseEntity.ok(resourceUpdated);
    }

    @DeleteMapping("/{householdId}")
    @PreAuthorize("hasAuthority('ROLE_REPRESENTANTE')")
    @Operation(summary = "Delete household by ID")
    public ResponseEntity<Void> deleteHouseholdById(@PathVariable Long householdId) {
        boolean deleted = commandService.delete(householdId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }


    @GetMapping("/{householdId}/members")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List members of a household")
    public ResponseEntity<List<HouseholdMemberResource>> getHouseholdMembers(@PathVariable Long householdId) {
        var list = householdMemberRepository.findAllByHousehold_Id(householdId);
        var resources = list.stream()
                .map(HouseholdMemberResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{householdId}/contributions")
    @PreAuthorize("hasAuthority('ROLE_REPRESENTANTE')")
    @Operation(summary = "List contributions of a household")
    public ResponseEntity<List<ContributionResource>> getHouseholdContributions(@PathVariable Long householdId) {
        var list = contributionRepository.findAllByHouseholdId(householdId);
        var resources = list.stream()
                .map(ContributionResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }
}
