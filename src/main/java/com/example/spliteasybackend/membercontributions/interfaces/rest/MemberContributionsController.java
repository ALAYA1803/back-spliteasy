package com.example.spliteasybackend.membercontributions.interfaces.rest;

import com.example.spliteasybackend.membercontributions.domain.models.queries.GetAllMemberContributionsQuery;
import com.example.spliteasybackend.membercontributions.domain.models.queries.GetMemberContributionByIdQuery;
import com.example.spliteasybackend.membercontributions.domain.services.MemberContributionCommandService;
import com.example.spliteasybackend.membercontributions.domain.services.MemberContributionQueryService;
import com.example.spliteasybackend.membercontributions.infrastructure.persistance.jpa.repositories.MemberContributionRepository;
import com.example.spliteasybackend.membercontributions.interfaces.rest.resources.CreateMemberContributionResource;
import com.example.spliteasybackend.membercontributions.interfaces.rest.resources.MemberContributionResource;
import com.example.spliteasybackend.membercontributions.interfaces.rest.transform.CreateMemberContributionCommandFromResourceAssembler;
import com.example.spliteasybackend.membercontributions.interfaces.rest.transform.MemberContributionResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/member-contributions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Member Contributions", description = "Available Member Contribution Endpoints")
public class MemberContributionsController {

    private final MemberContributionCommandService commandService;
    private final MemberContributionQueryService queryService;
    private final MemberContributionRepository memberContributionRepository;

    public MemberContributionsController(MemberContributionCommandService commandService,
                                         MemberContributionQueryService queryService,
                                         MemberContributionRepository memberContributionRepository) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.memberContributionRepository = memberContributionRepository;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_REPRESENTANTE')")
    @Operation(summary = "Create a member contribution")
    public ResponseEntity<MemberContributionResource> createMemberContribution(
            @RequestBody CreateMemberContributionResource resource) {
        var command = CreateMemberContributionCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = commandService.handle(command);
        if (result.isEmpty()) return ResponseEntity.badRequest().build();
        var responseResource = MemberContributionResourceFromEntityAssembler.toResourceFromEntity(result.get());
        return new ResponseEntity<>(responseResource, HttpStatus.CREATED);
    }

    @GetMapping(params = "memberId")
    @PreAuthorize("hasAnyAuthority('ROLE_MIEMBRO','ROLE_REPRESENTANTE')")
    @Operation(summary = "Get member contributions by member (user) id")
    public ResponseEntity<List<MemberContributionResource>> getByMemberId(@RequestParam Long memberId) {
        var list = memberContributionRepository.findAllByMember_Id(memberId);
        var resources = list.stream()
                .map(MemberContributionResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping(params = {"householdId","memberId"})
    @PreAuthorize("hasAnyAuthority('ROLE_MIEMBRO','ROLE_REPRESENTANTE')")
    @Operation(summary = "Get member contributions by household and member")
    public ResponseEntity<List<MemberContributionResource>> getByHouseholdAndMember(
            @RequestParam Long householdId,
            @RequestParam Long memberId) {
        var list = memberContributionRepository
                .findAllByMember_IdAndContribution_Household_Id(memberId, householdId);
        var resources = list.stream()
                .map(MemberContributionResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_REPRESENTANTE')")
    @Operation(summary = "Get all member contributions (representative only)")
    public ResponseEntity<List<MemberContributionResource>> getAll() {
        var results = queryService.handle(new GetAllMemberContributionsQuery());
        var resources = results.stream()
                .map(MemberContributionResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get member contribution by ID")
    public ResponseEntity<MemberContributionResource> getById(@PathVariable Long id) {
        var query = new GetMemberContributionByIdQuery(id);
        var result = queryService.handle(query);
        if (result.isEmpty()) return ResponseEntity.notFound().build();
        var resource = MemberContributionResourceFromEntityAssembler.toResourceFromEntity(result.get());
        return ResponseEntity.ok(resource);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_REPRESENTANTE')")
    @Operation(summary = "Update member contribution by ID")
    public ResponseEntity<MemberContributionResource> updateById(
            @PathVariable Long id,
            @RequestBody CreateMemberContributionResource resource) {
        var command = CreateMemberContributionCommandFromResourceAssembler.toCommandFromResource(resource);
        var updated = commandService.update(id, command);
        if (updated.isEmpty()) return ResponseEntity.notFound().build();
        var resourceUpdated = MemberContributionResourceFromEntityAssembler.toResourceFromEntity(updated.get());
        return ResponseEntity.ok(resourceUpdated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_REPRESENTANTE')")
    @Operation(summary = "Delete member contribution by ID")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        boolean deleted = commandService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
