package com.example.spliteasybackend.contributions.interfaces;

import com.example.spliteasybackend.contributions.domain.models.queries.GetAllContributionsQuery;
import com.example.spliteasybackend.contributions.domain.models.queries.GetContributionByIdQuery;
import com.example.spliteasybackend.contributions.domain.services.ContributionCommandService;
import com.example.spliteasybackend.contributions.domain.services.ContributionQueryService;
import com.example.spliteasybackend.contributions.interfaces.rest.resources.CreateContributionResource;
import com.example.spliteasybackend.contributions.interfaces.rest.resources.ContributionResource;
import com.example.spliteasybackend.contributions.interfaces.rest.transform.CreateContributionCommandFromResourceAssembler;
import com.example.spliteasybackend.contributions.interfaces.rest.transform.ContributionResourceFromEntityAssembler;
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
@RequestMapping(value = "/api/v1/contributions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Contributions", description = "Available Contribution Endpoints")
public class ContributionsController {

    private final ContributionCommandService commandService;
    private final ContributionQueryService queryService;

    public ContributionsController(ContributionCommandService commandService, ContributionQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_REPRESENTANTE')")
    @Operation(summary = "Create a contribution")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contribution created"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<ContributionResource> createContribution(@RequestBody CreateContributionResource resource) {
        var command = CreateContributionCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = commandService.handle(command);
        if (result.isEmpty()) return ResponseEntity.badRequest().build();
        var responseResource = ContributionResourceFromEntityAssembler.toResourceFromEntity(result.get());
        return new ResponseEntity<>(responseResource, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all contributions")
    public ResponseEntity<List<ContributionResource>> getAllContributions() {
        var results = queryService.handle(new GetAllContributionsQuery());
        if (results.isEmpty()) return ResponseEntity.notFound().build();
        var resources = results.stream()
                .map(ContributionResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{contributionId}")
    @Operation(summary = "Get contribution by ID")
    public ResponseEntity<ContributionResource> getContributionById(@PathVariable Long contributionId) {
        var query = new GetContributionByIdQuery(contributionId);
        var result = queryService.handle(query);
        if (result.isEmpty()) return ResponseEntity.notFound().build();
        var resource = ContributionResourceFromEntityAssembler.toResourceFromEntity(result.get());
        return ResponseEntity.ok(resource);
    }

    @PutMapping("/{contributionId}")
    @Operation(summary = "Update contribution by ID")
    public ResponseEntity<ContributionResource> updateContributionById(@PathVariable Long contributionId,
                                                                       @RequestBody CreateContributionResource resource) {
        var command = CreateContributionCommandFromResourceAssembler.toCommandFromResource(resource);
        var updated = commandService.update(contributionId, command);
        if (updated.isEmpty()) return ResponseEntity.notFound().build();
        var updatedResource = ContributionResourceFromEntityAssembler.toResourceFromEntity(updated.get());
        return ResponseEntity.ok(updatedResource);
    }

    @DeleteMapping("/{contributionId}")
    @PreAuthorize("hasAuthority('ROLE_REPRESENTANTE')")
    @Operation(summary = "Delete contribution by ID")
    public ResponseEntity<Void> deleteContributionById(@PathVariable Long contributionId) {
        boolean deleted = commandService.delete(contributionId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
