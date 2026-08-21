package com.buysell.modules.inventory.service;

import com.buysell.exception.BusinessException;
import com.buysell.modules.inventory.enums.InventoryStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Component
public class InventoryStateMachine {

    private final Map<InventoryStatus, Set<InventoryStatus>> allowedTransitions = new EnumMap<>(InventoryStatus.class);

    public InventoryStateMachine() {
        allowedTransitions.put(InventoryStatus.AVAILABLE, EnumSet.of(
                InventoryStatus.RESERVED,
                InventoryStatus.SOLD,
                InventoryStatus.DAMAGED,
                InventoryStatus.BLOCKED,
                InventoryStatus.IN_TRANSIT
        ));

        allowedTransitions.put(InventoryStatus.RESERVED, EnumSet.of(
                InventoryStatus.AVAILABLE,
                InventoryStatus.SOLD
        ));

        allowedTransitions.put(InventoryStatus.SOLD, EnumSet.of(
                InventoryStatus.RETURNED
        ));

        allowedTransitions.put(InventoryStatus.RETURNED, EnumSet.of(
                InventoryStatus.AVAILABLE,
                InventoryStatus.DAMAGED,
                InventoryStatus.BLOCKED
        ));

        allowedTransitions.put(InventoryStatus.DAMAGED, EnumSet.of(
                InventoryStatus.AVAILABLE,
                InventoryStatus.BLOCKED
        ));

        allowedTransitions.put(InventoryStatus.BLOCKED, EnumSet.of(
                InventoryStatus.AVAILABLE
        ));

        allowedTransitions.put(InventoryStatus.IN_TRANSIT, EnumSet.of(
                InventoryStatus.AVAILABLE
        ));
    }

    public void validateTransition(InventoryStatus currentStatus, InventoryStatus newStatus) {
        if (currentStatus == newStatus) {
            return;
        }

        Set<InventoryStatus> validNextStates = allowedTransitions.get(currentStatus);
        
        if (validNextStates == null || !validNextStates.contains(newStatus)) {
            throw new BusinessException(
                    "INVENTORY_STATUS_INVALID",
                    "Invalid transition from " + currentStatus + " to " + newStatus,
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}
