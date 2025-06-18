package com.example.spliteasybackend.bills.application.acl;

import com.example.spliteasybackend.bills.domain.models.commands.CreateBillCommand;
import com.example.spliteasybackend.bills.domain.models.queries.GetBillByIdQuery;
import com.example.spliteasybackend.bills.domain.models.valueobjects.Money;
import com.example.spliteasybackend.bills.domain.services.BillCommandService;
import com.example.spliteasybackend.bills.domain.services.BillQueryService;
import com.example.spliteasybackend.bills.interfaces.acl.BillsContextFacade;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class BillsContextFacadeImpl implements BillsContextFacade {

    private final BillCommandService commandService;
    private final BillQueryService queryService;

    public BillsContextFacadeImpl(BillCommandService commandService, BillQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @Override
    public Long createBill(Long householdId, String description, double monto, Long createdBy, LocalDate fecha) {
        var command = new CreateBillCommand(
                householdId,
                description,
                new Money(BigDecimal.valueOf(monto)),
                createdBy,
                fecha
        );
        var result = commandService.handle(command);
        return result.map(b -> b.getId()).orElse(0L);
    }

    @Override
    public boolean existsBillById(Long id) {
        var query = new GetBillByIdQuery(id);
        return queryService.handle(query).isPresent();
    }
}
