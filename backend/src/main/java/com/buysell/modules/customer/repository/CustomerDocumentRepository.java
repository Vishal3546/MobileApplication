package com.buysell.modules.customer.repository;

import com.buysell.modules.customer.entity.CustomerDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CustomerDocumentRepository extends JpaRepository<CustomerDocument, UUID> {
    List<CustomerDocument> findByCustomerId(UUID customerId);
}
