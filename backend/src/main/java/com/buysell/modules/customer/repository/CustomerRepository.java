package com.buysell.modules.customer.repository;

import com.buysell.modules.customer.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    
    @Query("SELECT c FROM Customer c WHERE " +
           "(:branchId IS NULL OR c.branch.id = :branchId) AND " +
           "(cast(:search as text) IS NULL OR LOWER(c.firstName) LIKE LOWER(CONCAT('%', cast(:search as text), '%')) OR " +
           "LOWER(c.lastName) LIKE LOWER(CONCAT('%', cast(:search as text), '%')) OR " +
           "c.phone LIKE CONCAT('%', cast(:search as text), '%') OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', cast(:search as text), '%')))")
    Page<Customer> searchCustomers(
            @Param("branchId") UUID branchId, 
            @Param("search") String search, 
            Pageable pageable);
}
