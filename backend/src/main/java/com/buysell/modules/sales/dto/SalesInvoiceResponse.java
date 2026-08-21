package com.buysell.modules.sales.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class SalesInvoiceResponse {
    private UUID id;
    private UUID saleTransactionId;
    private String invoiceNumber;
    private UUID mediaId;
    private UUID issuedById;
    private ZonedDateTime issuedAt;
}
