package com.example.spliteasybackend.bills.infrastructure.persistance.jpa.repositories;

import com.example.spliteasybackend.bills.domain.models.aggregates.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
}
