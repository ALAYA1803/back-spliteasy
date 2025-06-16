package com.example.spliteasybackend.user.interfaces.rest;

import com.example.spliteasybackend.user.domain.models.queries.GetAllUsersQuery;
import com.example.spliteasybackend.user.domain.models.queries.GetUserByIdQuery;
import com.example.spliteasybackend.user.domain.services.UserCommandService;
import com.example.spliteasybackend.user.domain.services.UserQueryService;
import com.example.spliteasybackend.user.interfaces.rest.resources.CreateUserResource;
import com.example.spliteasybackend.user.interfaces.rest.resources.UserResource;
import com.example.spliteasybackend.user.interfaces.rest.transform.CreateUserCommandFromResourceAssembler;
import com.example.spliteasybackend.user.interfaces.rest.transform.UserResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "Available User Endpoints")
public class UsersController {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    public UsersController(UserCommandService userCommandService, UserQueryService userQueryService) {
        this.userCommandService = userCommandService;
        this.userQueryService = userQueryService;
    }

    @PostMapping
    @Operation(summary = "Create a user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<UserResource> createUser(@RequestBody CreateUserResource resource) {
        var command = CreateUserCommandFromResourceAssembler.toCommandFromResource(resource);
        var user = userCommandService.handle(command);
        if (user.isEmpty()) return ResponseEntity.badRequest().build();
        var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(user.get());
        return new ResponseEntity<>(userResource, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all users")
    public ResponseEntity<List<UserResource>> getAllUsers() {
        var users = userQueryService.handle(new GetAllUsersQuery());
        if (users.isEmpty()) return ResponseEntity.notFound().build();
        var resources = users.stream()
                .map(UserResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserResource> getUserById(@PathVariable Long userId) {
        var query = new GetUserByIdQuery(userId);
        var user = userQueryService.handle(query);
        if (user.isEmpty()) return ResponseEntity.notFound().build();
        var resource = UserResourceFromEntityAssembler.toResourceFromEntity(user.get());
        return ResponseEntity.ok(resource);
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update user by ID")
    public ResponseEntity<UserResource> updateUserById(@PathVariable Long userId, @RequestBody CreateUserResource resource) {
        var command = CreateUserCommandFromResourceAssembler.toCommandFromResource(resource);
        var user = userCommandService.update(userId, command);
        if (user.isEmpty()) return ResponseEntity.notFound().build();
        var resourceUpdated = UserResourceFromEntityAssembler.toResourceFromEntity(user.get());
        return ResponseEntity.ok(resourceUpdated);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user by ID")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long userId) {
        boolean deleted = userCommandService.delete(userId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
