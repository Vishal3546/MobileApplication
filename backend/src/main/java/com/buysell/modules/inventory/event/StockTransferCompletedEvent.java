package com.buysell.modules.inventory.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class StockTransferCompletedEvent extends ApplicationEvent {
    private final UUID transferId;

    public StockTransferCompletedEvent(Object source, UUID transferId) {
        super(source);
        this.transferId = transferId;
    }
    public UUID getTransferId() {
        return this.transferId;
    }
}
