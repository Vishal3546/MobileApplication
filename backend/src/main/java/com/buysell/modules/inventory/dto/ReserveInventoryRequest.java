package com.buysell.modules.inventory.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.ZonedDateTime;

@Data
public class ReserveInventoryRequest {
    @NotNull(message = "Reservation end time is required")
    @Future(message = "Reservation end time must be in the future")
    private ZonedDateTime reservedUntil;
}
