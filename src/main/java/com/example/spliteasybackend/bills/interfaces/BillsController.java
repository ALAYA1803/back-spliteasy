package com.example.spliteasybackend.bills.interfaces;

import com.example.spliteasybackend.bills.domain.models.queries.GetAllBillsQuery;
import com.example.spliteasybackend.bills.domain.models.queries.GetBillByIdQuery;
import com.example.spliteasybackend.bills.domain.services.BillCommandService;
import com.example.spliteasybackend.bills.domain.services.BillQueryService;
import com.example.spliteasybackend.bills.interfaces.rest.resources.BillResource;
import com.example.spliteasybackend.bills.interfaces.rest.resources.CreateBillResource;
import com.example.spliteasybackend.bills.interfaces.rest.transform.BillResourceFromEntityAssembler;
import com.example.spliteasybackend.bills.interfaces.rest.transform.CreateBillCommandFromResourceAssembler;
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
@RequestMapping(value = "/api/v1/bills", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Bills", description = "Available Bill Endpoints")
public class BillsController {

    private final BillCommandService billCommandService;
    private final BillQueryService billQueryService;

    public BillsController(BillCommandService billCommandService, BillQueryService billQueryService) {
        this.billCommandService = billCommandService;
        this.billQueryService = billQueryService;
    }

    @PostMapping
    @Operation(summary = "Create a bill")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Bill created"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<BillResource> createBill(@RequestBody CreateBillResource resource) {
        var command = CreateBillCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = billCommandService.handle(command);
        if (result.isEmpty()) return ResponseEntity.badRequest().build();
        var response = BillResourceFromEntityAssembler.toResourceFromEntity(result.get());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all bills")
    public ResponseEntity<List<BillResource>> getAllBills() {
        var result = billQueryService.handle(new GetAllBillsQuery());
        if (result.isEmpty()) return ResponseEntity.notFound().build();
        var resources = result.stream()
                .map(BillResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{billId}")
    @Operation(summary = "Get bill by ID")
    public ResponseEntity<BillResource> getBillById(@PathVariable Long billId) {
        var query = new GetBillByIdQuery(billId);
        var result = billQueryService.handle(query);
        if (result.isEmpty()) return ResponseEntity.notFound().build();
        var resource = BillResourceFromEntityAssembler.toResourceFromEntity(result.get());
        return ResponseEntity.ok(resource);
    }

    @PutMapping("/{billId}")
    @Operation(summary = "Update bill by ID")
    public ResponseEntity<BillResource> updateBillById(@PathVariable Long billId, @RequestBody CreateBillResource resource) {
        var command = CreateBillCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = billCommandService.update(billId, command);
        if (result.isEmpty()) return ResponseEntity.notFound().build();
        var updatedResource = BillResourceFromEntityAssembler.toResourceFromEntity(result.get());
        return ResponseEntity.ok(updatedResource);
    }

    @DeleteMapping("/{billId}")
    @Operation(summary = "Delete bill by ID")
    public ResponseEntity<Void> deleteBillById(@PathVariable Long billId) {
        boolean deleted = billCommandService.delete(billId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
