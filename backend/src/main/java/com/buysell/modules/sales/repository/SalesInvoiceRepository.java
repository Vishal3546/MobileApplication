package com.buysell.modules.sales.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.buysell.modules.sales.entity.SalesInvoice;

@Repository
public interface SalesInvoiceRepository extends JpaRepository<SalesInvoice, UUID> {
    Optional<SalesInvoice> findByInvoiceNumber(String invoiceNumber);

    @Query(value = "SELECT CONCAT('INV-', TO_CHAR(CURRENT_DATE, 'YYYY'), '-', LPAD(NEXTVAL('invoice_number_seq')::TEXT, 6, '0'))", nativeQuery = true)
    String generateInvoiceNumber();
}
