package com.example.spliteasybackend.settings.interfaces.rest;

import com.example.spliteasybackend.settings.domain.models.queries.GetAllSettingsQuery;
import com.example.spliteasybackend.settings.domain.models.queries.GetSettingByIdQuery;
import com.example.spliteasybackend.settings.domain.services.SettingCommandService;
import com.example.spliteasybackend.settings.domain.services.SettingQueryService;
import com.example.spliteasybackend.settings.interfaces.rest.resources.CreateSettingResource;
import com.example.spliteasybackend.settings.interfaces.rest.resources.SettingResource;
import com.example.spliteasybackend.settings.interfaces.rest.transform.CreateSettingCommandFromResourceAssembler;
import com.example.spliteasybackend.settings.interfaces.rest.transform.SettingResourceFromEntityAssembler;
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
@RequestMapping(value = "/api/v1/settings", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Settings", description = "Available Settings Endpoints")
public class SettingsController {

    private final SettingCommandService settingCommandService;
    private final SettingQueryService settingQueryService;

    public SettingsController(SettingCommandService settingCommandService, SettingQueryService settingQueryService) {
        this.settingCommandService = settingCommandService;
        this.settingQueryService = settingQueryService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_REPRESENTANTE')")
    @Operation(summary = "Create a setting")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Setting created"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<SettingResource> createSetting(@RequestBody CreateSettingResource resource) {
        var command = CreateSettingCommandFromResourceAssembler.toCommandFromResource(resource);
        var setting = settingCommandService.handle(command);
        if (setting.isEmpty()) return ResponseEntity.badRequest().build();
        var settingResource = SettingResourceFromEntityAssembler.toResourceFromEntity(setting.get());
        return new ResponseEntity<>(settingResource, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all settings")
    public ResponseEntity<List<SettingResource>> getAllSettings() {
        var settings = settingQueryService.handle(new GetAllSettingsQuery());
        if (settings.isEmpty()) return ResponseEntity.notFound().build();
        var resources = settings.stream()
                .map(SettingResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{settingId}")
    @Operation(summary = "Get setting by ID")
    public ResponseEntity<SettingResource> getSettingById(@PathVariable Long settingId) {
        var query = new GetSettingByIdQuery(settingId);
        var setting = settingQueryService.handle(query);
        if (setting.isEmpty()) return ResponseEntity.notFound().build();
        var resource = SettingResourceFromEntityAssembler.toResourceFromEntity(setting.get());
        return ResponseEntity.ok(resource);
    }

    @PutMapping("/{settingId}")
    @Operation(summary = "Update setting by ID")
    public ResponseEntity<SettingResource> updateSettingById(@PathVariable Long settingId, @RequestBody CreateSettingResource resource) {
        var command = CreateSettingCommandFromResourceAssembler.toCommandFromResource(resource);
        var setting = settingCommandService.update(settingId, command);
        if (setting.isEmpty()) return ResponseEntity.notFound().build();
        var resourceUpdated = SettingResourceFromEntityAssembler.toResourceFromEntity(setting.get());
        return ResponseEntity.ok(resourceUpdated);
    }

    @DeleteMapping("/{settingId}")
    @PreAuthorize("hasAuthority('ROLE_REPRESENTANTE')")
    @Operation(summary = "Delete setting by ID")
    public ResponseEntity<Void> deleteSettingById(@PathVariable Long settingId) {
        boolean deleted = settingCommandService.delete(settingId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
